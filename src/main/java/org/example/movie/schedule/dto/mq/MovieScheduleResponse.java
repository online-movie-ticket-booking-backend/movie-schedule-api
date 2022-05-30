package org.example.movie.schedule.dto.mq;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data(staticConstructor = "of")
@Accessors(chain = true)
public class MovieScheduleResponse {
    private String scheduleDate;
    private List<TheatreDetailsResponse> theatreDetailsResponseList;
}
