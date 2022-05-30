package org.example.movie.schedule.utility;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Converter {
    private Converter() {
    }

    public static Date convertToSQLDate(String localDateParameter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //convert String to LocalDate
        LocalDate localDate = LocalDate.parse(localDateParameter, formatter);
        return Optional.ofNullable(localDate)
                .map(Date::valueOf)
                .orElseGet(() -> Date.valueOf(LocalDate.now()));
    }
}
