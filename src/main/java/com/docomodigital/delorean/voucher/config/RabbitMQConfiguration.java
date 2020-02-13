package com.docomodigital.delorean.voucher.config;

import com.docomodigital.delorean.voucher.service.VoucherQueueReceiverService;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RabbitMQConfiguration {

//    public static final String TOPIC_EXCHANGE_NAME = "voucher-consume";
//    public static final String QUEUE_NAME = "voucher-queue";

    @Bean
    Queue queue(QueueProperties queueProperties) {
        return new Queue(queueProperties.getInputQueueName(), false);
    }

//    @Bean
//    TopicExchange exchange() {
//        return new TopicExchange(TOPIC_EXCHANGE_NAME);
//    }

//    @Bean
//    Binding binding(Queue queue, TopicExchange exchange) {
//        return BindingBuilder.bind(queue).to(exchange).with("com.docomodigital.delorean.voucher.#");
//    }

    @Bean
    @Profile("!test")
    ConnectionFactory connectionFactory() {
        return new SimpleRoutingConnectionFactory();
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter,
                                             QueueProperties queueProperties) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueProperties.getInputQueueName());
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    MessageListenerAdapter listenerAdapter(VoucherQueueReceiverService receiver) {
        return new MessageListenerAdapter(receiver, messageConverter());
    }

}
