package org.example.movie.schedule.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "movie_schedule", schema = "movie_booking", catalog = "")
public class MovieSchedule {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ms_id", nullable = false)
    private Integer msId;
    @Basic
    @Column(name = "ms_mcm_id", nullable = true, length = 20)
    private String msMcmId;
    @Basic
    @Column(name = "ms_date", nullable = false)
    private Date msDate;
    @Basic
    @Column(name = "ms_time", nullable = false, length = 5)
    private String msTime;
    @Basic
    @Column(name = "ms_theatre_unique_id", nullable = true, length = 255)
    private String msTheatreUniqueId;

    public Integer getMsId() {
        return msId;
    }

    public void setMsId(Integer msId) {
        this.msId = msId;
    }

    public String getMsMcmId() {
        return msMcmId;
    }

    public void setMsMcmId(String msMcmId) {
        this.msMcmId = msMcmId;
    }

    public Date getMsDate() {
        return msDate;
    }

    public void setMsDate(Date msDate) {
        this.msDate = msDate;
    }

    public String getMsTime() {
        return msTime;
    }

    public void setMsTime(String msTime) {
        this.msTime = msTime;
    }

    public String getMsTheatreUniqueId() {
        return msTheatreUniqueId;
    }

    public void setMsTheatreUniqueId(String msTheatreUniqueId) {
        this.msTheatreUniqueId = msTheatreUniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieSchedule that = (MovieSchedule) o;
        return Objects.equals(msId, that.msId) && Objects.equals(msMcmId, that.msMcmId) && Objects.equals(msDate, that.msDate) && Objects.equals(msTime, that.msTime) && Objects.equals(msTheatreUniqueId, that.msTheatreUniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msId, msMcmId, msDate, msTime, msTheatreUniqueId);
    }
}
