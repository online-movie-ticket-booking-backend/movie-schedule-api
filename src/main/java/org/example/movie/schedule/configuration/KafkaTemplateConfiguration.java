package org.example.movie.schedule.configuration;

import org.example.movie.core.common.schedule.MovieScheduleResponse;
import org.example.movie.core.common.schedule.TheatreDetailsRequest;
import org.example.movie.core.common.schedule.TheatreDetailsResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class KafkaTemplateConfiguration {

    @Bean
    public KafkaTemplate<String, MovieScheduleResponse> kafkaMovieScheduleReplyTemplate(ProducerFactory<String,
            MovieScheduleResponse> producerFactoryMovieScheduleResponse) {
        return new KafkaTemplate<>(producerFactoryMovieScheduleResponse);
    }

    @Bean
    public ReplyingKafkaTemplate<String, TheatreDetailsRequest, TheatreDetailsResponse> kafkaTheatreDetailsReplyTemplate(
            ProducerFactory<String, TheatreDetailsRequest> producerFactoryTheatreDetailsListRequest,
            ConcurrentMessageListenerContainer<String, TheatreDetailsResponse> movieTheatreDetailsListenerContainer) {
        return new ReplyingKafkaTemplate<>(producerFactoryTheatreDetailsListRequest, movieTheatreDetailsListenerContainer);
    }
}