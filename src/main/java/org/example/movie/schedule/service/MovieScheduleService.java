package org.example.movie.schedule.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.movie.schedule.configuration.ExchangeConfiguration;
import org.example.movie.schedule.dto.mq.MovieScheduleRequest;
import org.example.movie.schedule.dto.mq.MovieScheduleResponse;
import org.example.movie.schedule.dto.mq.TheatreDetailsRequest;
import org.example.movie.schedule.dto.mq.TheatreDetailsResponse;
import org.example.movie.schedule.entity.MovieSchedule;
import org.example.movie.schedule.repository.MovieScheduleRepository;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Slf4j
@Service
@AllArgsConstructor
public class MovieScheduleService {

    private final RabbitTemplate rabbitTemplate;
    private final ExchangeConfiguration theatreDetailsExchange;
    private final MovieScheduleRepository movieScheduleRepository;

    public MovieScheduleResponse fetchMovieSchedule(MovieScheduleRequest movieScheduleRequest) {
        return MovieScheduleResponse
                .of()
                .setScheduleDate(movieScheduleRequest.getScheduleDate())
                .setTheatreDetailsResponseList(

                        movieScheduleRepository.findAllByMsMcmIdAndMsDate(
                                        movieScheduleRequest.getMovieCityMappingId(),
                                        Date.valueOf(movieScheduleRequest.getScheduleDate()))
                                .stream()
                                .collect(Collectors.groupingBy(MovieSchedule::getMsTheatreUniqueId))
                                .entrySet()
                                .stream()
                                .map(this::getTheaterDetailsAndMovieScheduleDTO)
                                .collect(Collectors.toList()));
    }

    private TheatreDetailsResponse getTheaterDetailsAndMovieScheduleDTO(Map.Entry<String, List<MovieSchedule>> scheduleDTO) {
        return TheatreDetailsResponse
                .of()
                .setTheatreName(getTheatreDetails(TheatreDetailsRequest.of().setTheatreUniqueId(scheduleDTO.getKey())).getTheatreName())
                .setShowtime(getShowTimeDetails(scheduleDTO.getValue().stream()));
    }

    private List<String> getShowTimeDetails(Stream<MovieSchedule> movieScheduleStream) {
        return movieScheduleStream.map(MovieSchedule::getMsTime)
                .sorted()
                .collect(Collectors.toList());
    }


    private TheatreDetailsResponse getTheatreDetails(TheatreDetailsRequest theatreDetailsRequest) {
        CorrelationData coreCorrelationData = new CorrelationData(UUID.randomUUID().toString());

        return sendMessageToExchange(theatreDetailsRequest, coreCorrelationData);
    }

    private TheatreDetailsResponse sendMessageToExchange(TheatreDetailsRequest message, CorrelationData correlationData) {
        return rabbitTemplate.convertSendAndReceiveAsType(
                theatreDetailsExchange.getExchange(),
                theatreDetailsExchange.getRoutingKey(),
                message,
                messageProperties -> {
                    messageProperties.getMessageProperties().setReplyTo("theatreDetailsQueue");
                    return messageProperties;
                },
                correlationData,
                ParameterizedTypeReference.forType(TheatreDetailsResponse.class));

    }
}
