package org.example.movie.schedule.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {

    @Value("${kafka.movieBookingApi.movieSchedule.topic.request}")
    private String movieScheduleTopicName;


    @Bean
    public NewTopic topicMovieScheduleTopicName() {
        return TopicBuilder.name(movieScheduleTopicName).build();
    }


}