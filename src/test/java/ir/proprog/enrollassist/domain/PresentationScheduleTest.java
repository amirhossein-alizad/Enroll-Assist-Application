package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.section.PresentationSchedule;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.utils.TestStudentBuilder;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class PresentationScheduleTest {

    SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm");

    @Test
    public void Presentation_schedule_with_given_valid_weekday_and_time_is_created_correctly() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            PresentationSchedule presentationSchedule = new PresentationSchedule("Sunday", "10:00", "12:00");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }

    @Test
    public void Presentation_schedule_with_invalid_weekday_and_time_returns_errors() {
        Throwable error = assertThrows(
                ExceptionList.class, () -> {
                    PresentationSchedule presentationSchedule = new PresentationSchedule("Sun", "10", "12");
                }
        );
        assertEquals(error.toString(), "{\"1\":\"Sun is not valid week day.\"," +
                                                        "\"2\":\"Time format is not valid\"}");
    }

    @Test
    public void Presentation_schedule_cannot_be_created_if_start_time_is_later_than_finish_time() {
        Throwable error = assertThrows(
                ExceptionList.class, () -> {
                    PresentationSchedule presentationSchedule = new PresentationSchedule("Monday", "10:00", "08:00");
                }
        );
        assertEquals(error.toString(), "{\"1\":\"End time can not be before start time.\"}");
    }

    @Test
    public void Overlapping_presentation_schedules_are_detected_correctly() throws ExceptionList {
        PresentationSchedule coursePresentationSchedule1 = new PresentationSchedule("Sunday", "10:00", "12:00");
        PresentationSchedule coursePresentationSchedule2 = new PresentationSchedule("Sunday", "09:00", "11:00");
        assertTrue(coursePresentationSchedule1.hasConflict(coursePresentationSchedule2));
    }


    @Test
    public void Presentation_schedules_on_different_days_do_not_overlap() throws ExceptionList {
        PresentationSchedule coursePresentationSchedule1 = new PresentationSchedule("Sunday", "10:00", "12:00");
        PresentationSchedule coursePresentationSchedule2 = new PresentationSchedule("Wednesday", "10:00", "12:00");
        assertFalse(coursePresentationSchedule1.hasConflict(coursePresentationSchedule2));
    }

    @Test
    public void Presentation_schedules_on_same_day_and_with_no_overlap_in_time_do_not_collide() throws ExceptionList {
        PresentationSchedule coursePresentationSchedule1 = new PresentationSchedule("Sunday", "10:00", "12:00");
        PresentationSchedule coursePresentationSchedule2 = new PresentationSchedule("Sunday", "12:00", "14:00");
        assertFalse(coursePresentationSchedule1.hasConflict(coursePresentationSchedule2));
    }

    @Test
    public void Equals_method_works_correctly_for_two_identical_instances_of_PresentationSchedule_class() throws ExceptionList {
        PresentationSchedule coursePresentationSchedule1 = new PresentationSchedule("Sunday", "10:00", "12:00");
        PresentationSchedule coursePresentationSchedule2 = new PresentationSchedule("Sunday", "10:00", "12:00");
        assertEquals(coursePresentationSchedule1, coursePresentationSchedule2);
    }
}
