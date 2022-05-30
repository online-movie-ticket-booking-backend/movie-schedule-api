package org.example.movie.schedule.configuration;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("message")
public class MovieScheduleMQConfiguration {

    private MessageConfiguration messageConfiguration;
    private ExchangeConfiguration theatreDetailsExchangeConfiguration;
    private ExchangeConfiguration movieScheduleExchangeConfiguration;

    @Bean
    public Exchange movieScheduleExchange() {
        return ExchangeBuilder.topicExchange(movieScheduleExchangeConfiguration.getExchange())
                .build();
    }

    @Bean
    public Queue movieScheduleQueue() {
        return new Queue(movieScheduleExchangeConfiguration.getQueue(), true);
    }

    @Bean
    public ExchangeConfiguration theatreDetailsExchange() {
        return theatreDetailsExchangeConfiguration;
    }

    @Bean
    public Binding movieScheduleBinding() {
        return BindingBuilder.bind(movieScheduleQueue())
                .to(movieScheduleExchange())
                .with(movieScheduleExchangeConfiguration.getRoutingKey())
                .noargs();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory =
                new CachingConnectionFactory(messageConfiguration.getHost());
        cachingConnectionFactory.setUsername(messageConfiguration.getUsername());
        cachingConnectionFactory.setPassword(messageConfiguration.getPassword());
        cachingConnectionFactory.setPort(messageConfiguration.getPort());
        cachingConnectionFactory.setVirtualHost(messageConfiguration.getVirtualHost());
        return cachingConnectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
