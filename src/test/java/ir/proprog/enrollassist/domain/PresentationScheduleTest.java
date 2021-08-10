package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PresentationScheduleTest {

    @Test
    public void Correct_input_create_SectionSchedule_correctly() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            PresentationSchedule presentationSchedule = new PresentationSchedule("Sunday", "10:00-12:00");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }


    @Test
    public void SectionSchedule_is_not_created_with_invalid_time_format_1() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            PresentationSchedule presentationSchedule = new PresentationSchedule("Monday", "10:30,12:00");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"10:30,12:00 is not valid time.\"}");
    }

    @Test
    public void SectionSchedule_is_not_created_with_invalid_time_format_2() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            PresentationSchedule presentationSchedule = new PresentationSchedule("Monday", "10-12");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"10-12 is not valid time.\"}");
    }

    @Test
    public void SectionSchedule_is_not_created_with_empty_time() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            PresentationSchedule presentationSchedule = new PresentationSchedule("Monday", "");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"Time can not be empty\"}");
    }

    @Test
    public void SectionSchedule_is_not_created_with_endTime_that_is_less_than_startTime() throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        try {
            PresentationSchedule presentationSchedule = new PresentationSchedule("Monday", "14:00-12:00");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"End time can not be before start time.(14:00-12:00)\"}");
    }

    @Test
    public void Conflict_of_two_CourseSchedule_with_overlap_is_diagnosed_correctly() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            PresentationSchedule coursePresentationSchedule1 = new PresentationSchedule("Sunday", "10:30-12:00");
            PresentationSchedule coursePresentationSchedule2 = new PresentationSchedule("Sunday", "10:00-11:30");
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
            PresentationSchedule coursePresentationSchedule1 = new PresentationSchedule("Sunday", "10:30-12:00");
            PresentationSchedule coursePresentationSchedule2 = new PresentationSchedule("Wednesday", "10:30-12:00");
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
            PresentationSchedule coursePresentationSchedule1 = new PresentationSchedule("Sunday", "10:30-12:00");
            PresentationSchedule coursePresentationSchedule2 = new PresentationSchedule("Sunday", "12:00-14:00");
            assertFalse(coursePresentationSchedule1.hasConflict(coursePresentationSchedule2));
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }
}
