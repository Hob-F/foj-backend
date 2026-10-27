package com.U100.foj.mq;

import com.U100.foj.constant.MqConstant;
import com.U100.foj.judge.JudgeService;          // ← 你的编排层在这里
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class JudgeMessageConsumer {

    @Resource
    private JudgeService judgeService;

    @SneakyThrows
    @RabbitListener(queues = MqConstant.JUDGE_QUEUE, ackMode = "MANUAL")
    public void receiveMessage(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String submitIdStr = new String(message.getBody());
        log.info("收到判题消息: submitId = {}", submitIdStr);
        try {
            judgeService.doJudge(Long.parseLong(submitIdStr));
            channel.basicAck(deliveryTag, false);
            log.info("判题完成，已 ACK: submitId = {}", submitIdStr);
        } catch (Exception e) {
            log.error("判题失败，NACK: submitId = {}", submitIdStr, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
