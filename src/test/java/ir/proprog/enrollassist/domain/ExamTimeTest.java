package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ExamTimeTest {
    @Test
    public void Exams_on_different_days_are_not_colliding() throws Exception {
        ExamTime time1 = new ExamTime("2021-06-21T8:00:00", "2021-06-21T11:00:00");
        ExamTime time2 = new ExamTime("2021-06-22T8:00:00", "2021-06-22T11:00:00");
        assertFalse(time1.hasTimeConflict(time2));
    }

    @Test
    public void Exams_on_same_day_and_conflicting_times_collide() throws Exception {
        ExamTime time1 = new ExamTime("2021-06-21T8:00:00", "2021-06-21T11:00:00");
        ExamTime time2 = new ExamTime("2021-06-21T9:30:00", "2021-06-21T11:00:00");
        assertTrue(time1.hasTimeConflict(time2));
    }

    @Test
    public void Exams_dont_collide_when_they_share_one_point() throws Exception {
        ExamTime time1 = new ExamTime("2021-06-21T8:00:00", "2021-06-21T11:00:00");
        ExamTime time2 = new ExamTime("2021-06-21T11:00:00", "2021-06-21T14:00:00");
        assertFalse(time1.hasTimeConflict(time2));
    }

    @Test
    public void Exams_dates_must_be_of_correct_format() {
        String exception = "";
        try {
            ExamTime time1 = new ExamTime("2021/06/21T8:00:00", "2021/06/21T11:00:00");
        } catch(Exception e) {
            exception = e.getMessage();
        }
        assertEquals(exception, "Dates must be of the format yyyy-MM-dd'T'hh:mm:ss");
    }

    @Test
    public void Exams_start_date_cannot_be_after_end_date() throws Exception {
        ExamTime time1 = new ExamTime("2021-06-21T11:00:00", "2021-06-21T10:00:00");
        List<String> errors = time1.validate();
        assertThat(errors).hasSize(1).contains("Exam start should be before its end.");
    }


}
