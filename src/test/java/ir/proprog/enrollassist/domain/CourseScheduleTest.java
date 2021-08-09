package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.controller.Exception.ExceptionList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CourseScheduleTest {

    @Test
    public void Correct_input_create_CourseSchedule_correctly() {
        ExceptionList exceptionList = new ExceptionList();
        List<String> days = List.of("Sunday", "Monday");
        try {
            CourseSchedule courseSchedule = new CourseSchedule(days, "10:30", "12:00");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }

    @Test
    public void CourseSchedule_is_not_created_with_empty_time() {
        ExceptionList exceptionList = new ExceptionList();
        List<String> days = List.of("Sunday", "Monday");
        try {
            CourseSchedule courseSchedule = new CourseSchedule(days, "10:30", "");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"Time can not be empty.\"}");
    }

    @Test
    public void CourseSchedule_is_not_created_with_incorrect_time_syntax() {
        ExceptionList exceptionList = new ExceptionList();
        List<String> days = List.of("Sunday", "Monday");
        try {
            CourseSchedule courseSchedule = new CourseSchedule(days, "10,30", "12");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"Section time is not valid.\"}");
    }

    @Test
    public void CourseSchedule_is_not_created_with_endTime_that_is_less_than_startTime() {
        ExceptionList exceptionList = new ExceptionList();
        List<String> days = List.of("Sunday", "Monday");
        try {
            CourseSchedule courseSchedule = new CourseSchedule(days, "10:30", "09:00");

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"End time can not be before start time.\"}");
    }

    @Test
    public void CourseSchedule_is_not_created_with_incorrect_week_day() {
        ExceptionList exceptionList = new ExceptionList();
        List<String> days = List.of("Sun", "Mon");
        try {
            CourseSchedule courseSchedule = new CourseSchedule(days, "10:30", "12:00");

        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"Sun is not valid week day.\"," +
                "\"2\":\"Mon is not valid week day.\"}");

    }

    @Test
    public void Conflict_of_two_CourseSchedule_with_overlap_is_diagnosed_correctly() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            CourseSchedule courseSchedule1 = new CourseSchedule(List.of("Sunday", "Monday"), "10:30", "12:00");
            CourseSchedule courseSchedule2 = new CourseSchedule(List.of("Wednesday", "Monday"), "09:30", "11:00");
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
            CourseSchedule courseSchedule1 = new CourseSchedule(List.of("Sunday", "Monday"), "10:30", "12:00");
            CourseSchedule courseSchedule2 = new CourseSchedule(List.of("Wednesday", "Tuesday"), "10:30", "12:00");
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
            CourseSchedule courseSchedule1 = new CourseSchedule(List.of("Sunday", "Monday"), "10:30", "12:00");
            CourseSchedule courseSchedule2 = new CourseSchedule(List.of("Sunday", "Monday"), "12:00", "13:00");
            assertFalse(courseSchedule1.hasConflict(courseSchedule2));
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }
}
