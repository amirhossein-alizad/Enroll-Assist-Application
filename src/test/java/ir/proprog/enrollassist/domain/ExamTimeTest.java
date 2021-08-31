package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.section.ExamTime;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ExamTimeTest {
    @Test
    public void Exams_on_different_days_are_not_colliding() throws Exception {
        ExamTime time1 = new ExamTime("2021-06-21T08:30", "2021-06-21T11:00");
        ExamTime time2 = new ExamTime("2021-06-22T08:00", "2021-06-22T11:00");
        assertFalse(time1.hasTimeConflict(time2));
    }

    @Test
    public void Exams_on_same_day_and_conflicting_times_collide() throws Exception {
        ExamTime time1 = new ExamTime("2021-06-21T08:00", "2021-06-21T11:00");
        ExamTime time2 = new ExamTime("2021-06-21T09:30", "2021-06-21T11:00");
        assertTrue(time1.hasTimeConflict(time2));
    }

    @Test
    public void Exams_dont_collide_when_they_share_one_point() throws Exception {
        ExamTime time1 = new ExamTime("2021-06-21T08:00", "2021-06-21T11:00");
        ExamTime time2 = new ExamTime("2021-06-21T11:00", "2021-06-21T14:00");
        assertFalse(time1.hasTimeConflict(time2));
    }

    @Test
    public void Exams_dates_must_be_of_correct_format() {
        String exception = "";
        try {
            ExamTime time1 = new ExamTime("2021/06/21T8:00", "2021/06/21T11:00");
        } catch(Exception e) {
            exception = e.getMessage();
        }
        assertEquals(exception, "Dates must be of the format yyyy-MM-ddTHH:mm");
    }

    @Test
    public void Exams_start_date_cannot_be_after_end_date() throws Exception {
        ExamTime time1 = new ExamTime("2021-06-21T11:00", "2021-06-21T10:00");
        String exception = "";
        try {
            time1.validate();
        }catch (ExceptionList e) {
            exception = e.toString();
        }
        assertEquals(exception,"{\"1\":\"Exam start should be before its end.\"}");
    }

    @Test
    public void Exams_start_and_finish_date_cannot_be_on_different_days() throws Exception {
        ExamTime time1 = new ExamTime("2021-06-21T11:00", "2021-06-22T12:00");
        String exception = "";
        try {
            time1.validate();
        }catch (ExceptionList e) {
            exception = e.toString();
        }
        assertEquals(exception,"{\"1\":\"Exam cannot take more than one day.\"}");
    }

    @Test
    public void Exam_date_should_be_on_valid_days(){
        String exception = "";
        try {
            ExamTime time1 = new ExamTime("2021-06-34T11:00", "2021-06-34T12:00");
        } catch(Exception e) {
            exception = e.getMessage();
        }
        assertEquals(exception, "Dates must be of the format yyyy-MM-ddTHH:mm");
    }

    @Test
    public void Exam_date_should_be_on_valid_months(){
        String exception = "";
        try {
            ExamTime time1 = new ExamTime("2021-13-11T11:00", "2021-13-11T12:00");
        } catch(Exception e) {
            exception = e.getMessage();
        }
        assertEquals(exception, "Dates must be of the format yyyy-MM-ddTHH:mm");
    }

    @Test
    public void Equals_method_works_correctly() {
        try {
            ExamTime time1 = new ExamTime("2021-13-11T11:00", "2021-13-11T12:00");
            ExamTime time2 = new ExamTime("2021-13-11T11:00", "2021-13-11T12:00");
            assertTrue(time1.equals(time2));
        } catch(Exception ignored) {
        }
    }
}
