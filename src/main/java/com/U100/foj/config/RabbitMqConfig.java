package com.U100.foj.config;

import com.U100.foj.constant.MqConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean
    public DirectExchange judgeExchange() {
        return new DirectExchange(MqConstant.JUDGE_EXCHANGE, true, false);
    }
    @Bean
    public Queue judgeQueue() {
        return QueueBuilder.durable(MqConstant.JUDGE_QUEUE).build();
    }
    @Bean
    public Binding judgeBinding() {
        return BindingBuilder.bind(judgeQueue()).to(judgeExchange())
                .with(MqConstant.JUDGE_ROUTING_KEY);
    }
}
