package org.example.movie.schedule.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.movie.schedule.dto.mq.MovieScheduleRequest;
import org.example.movie.schedule.dto.mq.MovieScheduleResponse;
import org.example.movie.schedule.service.MovieScheduleService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class MovieScheduleListener {

    private final MovieScheduleService movieScheduleService;

    @RabbitListener(queues = "movieScheduleQueue")
    public MovieScheduleResponse receiveMovieMessage(MovieScheduleRequest movieScheduleRequest) {
        return movieScheduleService.fetchMovieSchedule(movieScheduleRequest);
    }
}
