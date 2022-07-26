package org.example.movie.schedule.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.movie.core.common.schedule.MovieScheduleRequest;
import org.example.movie.core.common.schedule.MovieScheduleTheatre;
import org.example.movie.core.common.schedule.MovieShow;
import org.example.movie.core.common.schedule.TheatreDetails;
import org.example.movie.schedule.entity.MovieSchedule;
import org.example.movie.schedule.repository.MovieScheduleRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Slf4j
@Service
@AllArgsConstructor
public class MovieScheduleService {
    private final MovieScheduleRepository movieScheduleRepository;

    public Map<String, List<MovieScheduleTheatre>> fetchMovieSchedule(String uniqueId, MovieScheduleRequest movieScheduleRequest) {
        Map<String, List<MovieScheduleTheatre>> returnMap = new HashMap<>();
        movieScheduleRequest
                .getMovieCityMappingIdList()
                .forEach(cityMapping ->
                        returnMap.put(cityMapping,
                                makeMovieScheduleTheatre(
                                        movieScheduleRequest.getScheduleDate(),
                                        cityMapping)));
        return returnMap;

    }

    private List<MovieScheduleTheatre> makeMovieScheduleTheatre(String scheduleDate, String movieCityMappingId) {
        return movieScheduleRepository.findAllByMsDateAndMsMcmId(
                        Date.valueOf(scheduleDate),
                        movieCityMappingId)
                .stream()
                .collect(Collectors.groupingBy(MovieSchedule::getMsTheatreUniqueId))
                .entrySet()
                .stream()
                .map(movieScheduleEntry ->
                        MovieScheduleTheatre
                                .of()
                                .setTheatreDetails(TheatreDetails.of()
                                        .setTheatreUniqueId(movieScheduleEntry.getKey()))
                                .setMovieShowList(
                                        movieScheduleEntry
                                                .getValue()
                                                .stream()
                                                .map(movieSchedule ->
                                                        MovieShow
                                                                .of()
                                                                .setShowtime(movieSchedule.getMsTime())
                                                                .setSeatCount(movieSchedule.getMsTotalSeatCount()))
                                                .collect(Collectors.toList())
                                )).collect(Collectors.toList());
    }
}
