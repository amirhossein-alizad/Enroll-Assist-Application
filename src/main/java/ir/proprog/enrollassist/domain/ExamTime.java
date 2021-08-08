package ir.proprog.enrollassist.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExamTime {
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
}
