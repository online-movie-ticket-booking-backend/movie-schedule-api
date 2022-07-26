package org.example.movie.schedule.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.movie.core.common.schedule.MovieScheduleRequest;
import org.example.movie.core.common.schedule.MovieScheduleResponse;
import org.example.movie.core.common.schedule.TheatreDetailsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${kafka.movieBookingApi.groupName}")
    private String groupName;

    @Value("${kafka.movieBookingApi.theatreDetails.topic.serialization-class}")
    private String theatreDetailsSerializationClass;

    @Value("${kafka.movieBookingApi.theatreDetails.topic.response}")
    private String theatreDetailsResponseTopic;

    @Value("${kafka.movieBookingApi.movieSchedule.topic.serialization-class}")
    private String movieScheduleSerializationClass;

    @Value("${kafka.movieBookingApi.movieSchedule.topic.request}")
    private String movieScheduleRequestTopic;

    @Bean
    public ConsumerFactory<String, MovieScheduleRequest> consumerFactoryMovieScheduleRequest() {
        return new DefaultKafkaConsumerFactory<>(
                getConfigurationMapForListener(movieScheduleSerializationClass));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MovieScheduleRequest> movieScheduleRequestListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MovieScheduleRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryMovieScheduleRequest());
        return factory;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, MovieScheduleRequest> movieScheduleRequestListenerContainer(
            ConcurrentKafkaListenerContainerFactory<String, MovieScheduleRequest> movieScheduleRequestListenerContainerFactory) {
        ConcurrentMessageListenerContainer<String, MovieScheduleRequest> repliesContainer =
                movieScheduleRequestListenerContainerFactory.createContainer(movieScheduleRequestTopic);
        repliesContainer.getContainerProperties().setGroupId(groupName);
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, MovieScheduleRequest>>
    movieScheduleRequestListenerContainerFactory(KafkaTemplate<String, MovieScheduleResponse> kafkaMovieScheduleReplyTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, MovieScheduleRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryMovieScheduleRequest());
        factory.setReplyTemplate(kafkaMovieScheduleReplyTemplate);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, TheatreDetailsResponse> consumerFactoryTheatreDetailsResponse() {
        return new DefaultKafkaConsumerFactory<>(
                getConfigurationMapForListener(theatreDetailsSerializationClass));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TheatreDetailsResponse> theatreDetailsResponseListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TheatreDetailsResponse> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryTheatreDetailsResponse());
        return factory;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, TheatreDetailsResponse> theatreDetailsResponseListenerContainer(
            ConcurrentKafkaListenerContainerFactory<String, TheatreDetailsResponse>
                    movieScheduleResponseResponseListenerContainerFactory) {
        ConcurrentMessageListenerContainer<String, TheatreDetailsResponse> repliesContainer =
                movieScheduleResponseResponseListenerContainerFactory.createContainer(theatreDetailsResponseTopic);
        repliesContainer.getContainerProperties().setGroupId(groupName);
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    private Map<String, Object> getConfigurationMapForListener(String defaultValueType) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(JsonDeserializer.KEY_DEFAULT_TYPE, "java.lang.String");
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, defaultValueType);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "org.example.movie.core.common.schedule");
        return configProps;
    }
}