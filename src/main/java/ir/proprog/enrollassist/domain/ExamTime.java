package ir.proprog.enrollassist.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExamTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
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
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
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
