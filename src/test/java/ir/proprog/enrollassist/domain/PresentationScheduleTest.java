package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

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
        ExceptionList exceptionList = new ExceptionList();
        try {
            PresentationSchedule presentationSchedule = new PresentationSchedule("Sun", "10", "12");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"Sun is not valid week day.\"," +
                                                        "\"2\":\"Time format is not valid\"}");
    }

    @Test
    public void presentationSchedule_is_not_created_with_endTime_that_is_less_than_startTime() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            PresentationSchedule presentationSchedule = new PresentationSchedule("Monday", "10:00", "08:00");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"End time can not be before start time.\"}");
    }

    @Test
    public void Conflict_of_two_presentationSchedule_with_overlap_is_diagnosed_correctly() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            PresentationSchedule coursePresentationSchedule1 = new PresentationSchedule("Sunday", "10:00", "12:00");
            PresentationSchedule coursePresentationSchedule2 = new PresentationSchedule("Sunday", "09:00", "11:00");
            assertTrue(coursePresentationSchedule1.hasConflict(coursePresentationSchedule2));
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }


    @Test
    public void No_conflict_is_diagnosed_correctly_1() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            PresentationSchedule coursePresentationSchedule1 = new PresentationSchedule("Sunday", "10:00", "12:00");
            PresentationSchedule coursePresentationSchedule2 = new PresentationSchedule("Wednesday", "10:00", "12:00");
            assertFalse(coursePresentationSchedule1.hasConflict(coursePresentationSchedule2));
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }

    @Test
    public void No_conflict_is_diagnosed_correctly_2() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            PresentationSchedule coursePresentationSchedule1 = new PresentationSchedule("Sunday", "10:00", "12:00");
            PresentationSchedule coursePresentationSchedule2 = new PresentationSchedule("Sunday", "12:00", "14:00");
            assertFalse(coursePresentationSchedule1.hasConflict(coursePresentationSchedule2));
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }
}
