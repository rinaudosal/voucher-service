package com.docomodigital.delorean.voucher.config;

import com.docomodigital.delorean.voucher.service.VoucherQueueReceiverService;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

/**
 * 2020/02/06
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Configuration
@EnableRabbit
public class RabbitMQConfiguration {
    private static final String INPUT_QUEUE_NAME = "tinder-api2plugin";
    private static final String OUTPUT_QUEUE_NAME = "tinder-plugin2api";

    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    @Profile("!test")
    Queue api2plugin() {
        return new Queue(INPUT_QUEUE_NAME);
    }

    @Bean
    @Profile("!test")
    Queue plugin2api() {
        return new Queue(OUTPUT_QUEUE_NAME);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(INPUT_QUEUE_NAME);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(VoucherQueueReceiverService receiver) {
        return new MessageListenerAdapter(receiver, messageConverter());
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }

}
