package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ExamTime {
    private LocalDateTime start;
    private LocalDateTime end;

    public void validate() throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        if (this.start.compareTo(this.end) >= 0)
            exceptionList.addNewException(new Exception("Exam start should be before its end."));
        if (!this.start.toLocalDate().equals(this.end.toLocalDate()))
            exceptionList.addNewException(new Exception("Exam cannot take more than one day."));
        if(exceptionList.hasException())
            throw exceptionList;
    }

    public ExamTime(String start, String end) throws Exception {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        try {
            this.start = LocalDateTime.parse(start, dateFormat);
            this.end = LocalDateTime.parse(end, dateFormat);
        } catch (Exception e) {
            throw new Exception("Dates must be of the format yyyy-MM-ddTHH:mm");
        }
    }

    public boolean hasTimeConflict(ExamTime other) {
        return !(other.end.compareTo(this.start) <= 0 || other.start.compareTo(this.end) >= 0);
    }

    @Override
    public int hashCode() { return Objects.hash(start, end); }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamTime examTime = (ExamTime) o;
        return start.equals(examTime.start) && end.equals(examTime.end);
    }
}
