package org.example.movie.schedule.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("messageConfiguration")
public class MessageConfiguration {
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String virtualHost;
}
