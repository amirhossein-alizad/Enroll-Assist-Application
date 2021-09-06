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
    public void Correct_input_create_presentationSchedule_correctly() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            PresentationSchedule presentationSchedule = new PresentationSchedule("Sunday", "10:00", "12:00");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }

    @Test
    public void Incorrect_input_does_not_create_presentationSchedule_correctly() {
        Throwable error = assertThrows(
                ExceptionList.class, () -> {
                    PresentationSchedule presentationSchedule = new PresentationSchedule("Sun", "10", "12");
                }
        );
        assertEquals(error.toString(), "{\"1\":\"Sun is not valid week day.\"," +
                                                        "\"2\":\"Time format is not valid\"}");
    }

    @Test
    public void presentationSchedule_is_not_created_with_endTime_that_is_less_than_startTime() {
        Throwable error = assertThrows(
                ExceptionList.class, () -> {
                    PresentationSchedule presentationSchedule = new PresentationSchedule("Monday", "10:00", "08:00");
                }
        );
        assertEquals(error.toString(), "{\"1\":\"End time can not be before start time.\"}");
    }

    @Test
    public void Conflict_of_two_presentationSchedule_with_overlap_is_diagnosed_correctly() throws ExceptionList {
        PresentationSchedule coursePresentationSchedule1 = new PresentationSchedule("Sunday", "10:00", "12:00");
        PresentationSchedule coursePresentationSchedule2 = new PresentationSchedule("Sunday", "09:00", "11:00");
        assertTrue(coursePresentationSchedule1.hasConflict(coursePresentationSchedule2));
    }


    @Test
    public void No_conflict_is_diagnosed_correctly_1() throws ExceptionList {
        PresentationSchedule coursePresentationSchedule1 = new PresentationSchedule("Sunday", "10:00", "12:00");
        PresentationSchedule coursePresentationSchedule2 = new PresentationSchedule("Wednesday", "10:00", "12:00");
        assertFalse(coursePresentationSchedule1.hasConflict(coursePresentationSchedule2));
    }

    @Test
    public void No_conflict_is_diagnosed_correctly_2() throws ExceptionList {
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
