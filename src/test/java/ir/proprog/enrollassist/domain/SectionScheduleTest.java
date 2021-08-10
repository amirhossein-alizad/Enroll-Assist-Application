package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SectionScheduleTest {

    @Test
    public void Correct_input_create_SectionSchedule_correctly() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            SectionSchedule sectionSchedule = new SectionSchedule("Sunday", "10:00-12:00");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }


    @Test
    public void SectionSchedule_is_not_created_with_invalid_time_format_1() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            SectionSchedule sectionSchedule = new SectionSchedule("Monday", "10:30,12:00");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"10:30,12:00 is not valid time.\"}");
    }

    @Test
    public void SectionSchedule_is_not_created_with_invalid_time_format_2() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            SectionSchedule sectionSchedule = new SectionSchedule("Monday", "10-12");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"10-12 is not valid time.\"}");
    }

    @Test
    public void SectionSchedule_is_not_created_with_empty_time() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            SectionSchedule sectionSchedule = new SectionSchedule("Monday", "");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"Time can not be empty\"}");
    }

    @Test
    public void SectionSchedule_is_not_created_with_endTime_that_is_less_than_startTime() throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        try {
            SectionSchedule sectionSchedule = new SectionSchedule("Monday", "14:00-12:00");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"End time can not be before start time.(14:00-12:00)\"}");
    }

    @Test
    public void Conflict_of_two_CourseSchedule_with_overlap_is_diagnosed_correctly() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            SectionSchedule courseSchedule1 = new SectionSchedule("Sunday", "10:30-12:00");
            SectionSchedule courseSchedule2 = new SectionSchedule("Sunday", "10:00-11:30");
            assertTrue(courseSchedule1.hasConflict(courseSchedule2));
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }


    @Test
    public void No_conflict_is_diagnosed_correctly_1() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            SectionSchedule courseSchedule1 = new SectionSchedule("Sunday", "10:30-12:00");
            SectionSchedule courseSchedule2 = new SectionSchedule("Wednesday", "10:30-12:00");
            assertFalse(courseSchedule1.hasConflict(courseSchedule2));
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }

    @Test
    public void No_conflict_is_diagnosed_correctly_2() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            SectionSchedule courseSchedule1 = new SectionSchedule("Sunday", "10:30-12:00");
            SectionSchedule courseSchedule2 = new SectionSchedule("Sunday", "12:00-14:00");
            assertFalse(courseSchedule1.hasConflict(courseSchedule2));
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }
}
