# FOJ 在线判题系统 —— 后端服务

基于 Spring Boot + RabbitMQ 的在线判题平台（OJ）后端，支持用户提交代码后
异步判题，代码在独立沙箱服务的 Docker 容器中安全执行。

## 技术栈

- 后端框架：Spring Boot + MyBatis-Plus
- 数据存储：MySQL（业务数据）、Redis（登录态/计数）
- 消息队列：RabbitMQ（异步判题核心链路）
- 代码沙箱：独立服务，Docker 容器隔离执行（见 foj-code-sandbox 仓库）

## 核心亮点

### 1. RabbitMQ 异步判题

- 用户提交后仅落库并向判题队列发送 submitId，**提交接口响应由同步判题的
  ~2s 降至 62ms**（F12 Network 实测），判题耗时由消费者侧承担；
- 队列为**持久化队列（durable）**，Broker 重启消息不丢；
- 消费者**手动 ACK**：判题成功才确认消费；失败执行 **NACK 且不重回队列**，
  避免毒消息无限重试导致队列堵塞；
- 消费者复用已有判题编排逻辑 `JudgeService.doJudge(submitId)`，对原有
  代码侵入性极小。

### 2. 判题链路

用户提交 → 落库(状态:等待中) → 发送 submitId 到 judge_queue
→ 62ms 返回提交 ID
消费者收到消息 → 置状态为"判题中" → 调用沙箱执行用户代码
→ 比对结果 → 回写判题结果 → ACK


### 3. 判题沙箱（配套仓库）

- Java 原生实现，Docker 容器隔离执行用户代码，限制运行时间/内存；
- 收集耗时、内存占用、标准输出，供判题比对。

## 快速启动

1. 准备环境：MySQL 8 / Redis / RabbitMQ（管理台 15672 端口）；
2. 修改 `application.yml` 中数据库、Redis、RabbitMQ 连接信息；
3. 启动沙箱服务（见 [foj-code-sandbox](https://github.com/Hob/foj-code-sandbox)）；
4. 运行 `MainApplication`，默认端口 8101（以实际配置为准）。

## 项目结构

com.U100.foj  
├── controller // 接口层：题目 / 提交 / 用户   
├── service // 业务层：含 QuestionSubmitServiceImpl（提交+发MQ）
├── judge // 判题编排与策略：JudgeService / JudgeManager / 策略模式
├── mq // JudgeMessageConsumer：判题消息消费者  
├── model // 实体 / DTO / 枚举  
└── config // RabbitMQ 配置（交换机/队列声明）等


## 后续规划

- [ ] 微服务化：按 老虎 网关/题目/判题 拆分（Spring Cloud Alibaba）
- [ ] 判题机横向扩容：多消费者并发消费判题队列
