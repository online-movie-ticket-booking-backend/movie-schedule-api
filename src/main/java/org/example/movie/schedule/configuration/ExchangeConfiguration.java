package org.example.movie.schedule.configuration;

import lombok.Data;

@Data
public class ExchangeConfiguration {
    private String exchange;
    private String routingKey;
    private String queue;
}
