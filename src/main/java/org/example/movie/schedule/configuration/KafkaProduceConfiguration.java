package org.example.movie.schedule.configuration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.movie.core.common.schedule.MovieScheduleResponse;
import org.example.movie.core.common.schedule.TheatreDetailsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProduceConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, MovieScheduleResponse> producerFactoryMovieScheduleResponse() {
        return new DefaultKafkaProducerFactory<>(getConfigurationMap());
    }

    @Bean
    public ProducerFactory<String, TheatreDetailsRequest> producerFactoryTheatreDetailsRequest() {
        return new DefaultKafkaProducerFactory<>(getConfigurationMap());
    }

    private Map<String, Object> getConfigurationMap() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        return configProps;
    }
}
