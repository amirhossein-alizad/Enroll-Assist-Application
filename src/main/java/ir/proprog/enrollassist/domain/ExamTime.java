package ir.proprog.enrollassist.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExamTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date start;
    private Date end;

    public ExamTime(String start, String end) {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        try {
            this.start = dataFormat.parse(start);
            this.end = dataFormat.parse(end);
        }
        catch (Exception ignored) {}
    }

    public boolean hasTimeConflict(ExamTime other) {
        return !(other.end.compareTo(this.start) <= 0 || other.start.compareTo(this.end) >= 0);
    }
}
