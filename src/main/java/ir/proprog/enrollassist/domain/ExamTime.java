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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExamTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date start;
    private Date end;

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (this.start.compareTo(this.end) >= 0)
            errors.add("Exam start should be before its end.");
        Instant instant1 = this.start.toInstant()
                .truncatedTo(ChronoUnit.DAYS);
        Instant instant2 = this.end.toInstant()
                .truncatedTo(ChronoUnit.DAYS);
        if (!instant1.equals(instant2))
            errors.add("Exam cannot take more than one day.");
        return errors;
    }

    public ExamTime(String start, String end) throws Exception {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        try {
            this.start = dataFormat.parse(start);
            this.end = dataFormat.parse(end);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Dates must be of the format yyyy-MM-dd'T'hh:mm:ss");
        }
    }

    public boolean hasTimeConflict(ExamTime other) {
        return !(other.end.compareTo(this.start) <= 0 || other.start.compareTo(this.end) >= 0);
    }
}
