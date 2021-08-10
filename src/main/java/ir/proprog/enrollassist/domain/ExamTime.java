package ir.proprog.enrollassist.domain;

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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ExamTime {
    private LocalDateTime start;
    private LocalDateTime end;

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (this.start.compareTo(this.end) >= 0)
            errors.add("Exam start should be before its end.");

        if (!this.start.toLocalDate().equals(this.end.toLocalDate()))
            errors.add("Exam cannot take more than one day.");

        return errors;
    }

    public ExamTime(String start, String end) throws Exception {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);
        try {
            this.start = LocalDateTime.parse(start, dateFormat);
            this.end = LocalDateTime.parse(end, dateFormat);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Dates must be of the format yyyy-MM-dd HH:mm");
        }
    }

    public boolean hasTimeConflict(ExamTime other) {
        return !(other.end.compareTo(this.start) <= 0 || other.start.compareTo(this.end) >= 0);
    }
}
