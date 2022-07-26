package org.example.movie.schedule.adapter;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.movie.core.common.schedule.MovieScheduleRequest;
import org.example.movie.core.common.schedule.MovieScheduleResponse;
import org.example.movie.core.common.schedule.MovieScheduleTheatre;
import org.example.movie.core.common.schedule.TheatreDetails;
import org.example.movie.core.common.schedule.TheatreDetailsRequest;
import org.example.movie.core.common.schedule.TheatreDetailsResponse;
import org.example.movie.schedule.service.MovieScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KafkaProducerAdapter {
    private ReplyingKafkaTemplate<String, TheatreDetailsRequest, TheatreDetailsResponse> kafkaTheatreDetailsReplyTemplate;

    @Value("${kafka.movieBookingApi.theatreDetails.topic.request}")
    private String theatreDetailsTopicName;

    private MovieScheduleService movieScheduleService;

    @Autowired
    public void setKafkaTheatreDetailsReplyTemplate(
            ReplyingKafkaTemplate<String, TheatreDetailsRequest, TheatreDetailsResponse> kafkaTheatreDetailsReplyTemplate) {
        this.kafkaTheatreDetailsReplyTemplate = kafkaTheatreDetailsReplyTemplate;
    }

    @Autowired
    public void setTheatreDetailsTopicName(MovieScheduleService movieScheduleService) {
        this.movieScheduleService = movieScheduleService;
    }

    public TheatreDetailsResponse kafkaTheatreDetailsRequestReplyObject(String uniqueId,
                                                                        TheatreDetailsRequest movieDetailsRequest)
            throws ExecutionException, InterruptedException, TimeoutException {
        ProducerRecord<String, TheatreDetailsRequest> record =
                new ProducerRecord<>(theatreDetailsTopicName, uniqueId, movieDetailsRequest);
        RequestReplyFuture<String, TheatreDetailsRequest, TheatreDetailsResponse> replyFuture =
                kafkaTheatreDetailsReplyTemplate.sendAndReceive(record);
        SendResult<String, TheatreDetailsRequest> sendResult =
                replyFuture.getSendFuture().get(10, TimeUnit.SECONDS);
        ConsumerRecord<String, TheatreDetailsResponse> consumerRecord =
                replyFuture.get(10, TimeUnit.SECONDS);
        return consumerRecord.value();
    }

    @KafkaListener(topics = "${kafka.movieBookingApi.movieSchedule.topic.request}",
            containerFactory = "movieScheduleRequestListenerContainerFactory",
            groupId = "${kafka.movieBookingApi.groupName}"
    )
    @SendTo()
    public MovieScheduleResponse receive(
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String uniqueId,
            MovieScheduleRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        log.info("Received Message with : {}", uniqueId);
        Map<String, List<MovieScheduleTheatre>> movieScheduleMap =
                movieScheduleService.fetchMovieSchedule(uniqueId, request);
        List<String> uniqueTheatreList = movieScheduleMap
                .values()
                .stream()
                .flatMap(List::stream)
                .map(movieScheduleTheatre -> movieScheduleTheatre.getTheatreDetails().getTheatreUniqueId())
                .distinct()
                .collect(Collectors.toList());

        Map<String, TheatreDetails> theatreDetailsMap = Optional
                .ofNullable(kafkaTheatreDetailsRequestReplyObject(uniqueId,
                        TheatreDetailsRequest
                                .of()
                                .setTheatreUniqueIdList(uniqueTheatreList)))
                .orElseGet(TheatreDetailsResponse::of)
                .getTheatreDetailsMap();
        movieScheduleMap
                .forEach((theatreName, theatreDetails) -> theatreDetails
                        .forEach(movieScheduleTheatre ->
                                movieScheduleTheatre.getTheatreDetails()
                                        .setTheatreName(Optional.ofNullable(theatreDetailsMap
                                                        .get(movieScheduleTheatre.getTheatreDetails().getTheatreUniqueId()))
                                                .orElseGet(TheatreDetails::of)
                                                .getTheatreName())));
        return MovieScheduleResponse.of().setMovieScheduleMap(movieScheduleMap);
    }

    @KafkaListener(topics = "${kafka.movieBookingApi.movieSchedule.topic.request-unique-id}",
            containerFactory = "movieScheduleRequestListenerContainerFactory",
            groupId = "${kafka.movieBookingApi.groupName}"
    )
    @SendTo()
    public MovieScheduleResponse receiveUniqueId(
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String uniqueId,
            MovieScheduleRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        log.info("Received Message with : {}", uniqueId);
       return movieScheduleService.fetchMovieScheduleByUniqueId(uniqueId,request);
    }
}

