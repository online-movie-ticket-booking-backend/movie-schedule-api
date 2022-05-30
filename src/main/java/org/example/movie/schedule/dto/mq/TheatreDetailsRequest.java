package org.example.movie.schedule.dto.mq;

import lombok.Data;
import lombok.experimental.Accessors;

@Data(staticConstructor = "of")
@Accessors(chain = true)
public class TheatreDetailsRequest {
    private String theatreUniqueId;
}
