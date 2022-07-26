package org.example.movie.schedule.repository;

import org.example.movie.schedule.entity.MovieSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieScheduleRepository extends JpaRepository<MovieSchedule, Integer> {

    List<MovieSchedule> findAllByMsDateAndMsMcmId(Date msDate, String msMcmId);
    Optional<MovieSchedule> findByMsUniqueId(String msUniqueId);
}
