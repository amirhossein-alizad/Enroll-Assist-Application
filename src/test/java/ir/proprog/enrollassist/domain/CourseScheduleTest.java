package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CourseScheduleTest {

    @Test
    public void Correct_input_create_SectionSchedule_correctly() {
        ExceptionList exceptionList = new ExceptionList();
        List<String> days = List.of("Sunday");
        List<String> times = List.of("10:00-12:00");
        try {
            SectionSchedule sectionSchedule = new SectionSchedule(days, times);

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }


    @Test
    public void SectionSchedule_is_not_created_with_endTime_that_is_less_than_startTime() {
        ExceptionList exceptionList = new ExceptionList();
        List<String> days = List.of("Sunday", "Monday", "Tuesday", "Wednesday", "Fri");
        List<String> times = List.of("", "10:30,12:00", "10-12", "14:00-12:00");
        try {
            SectionSchedule sectionSchedule = new SectionSchedule(days, times);

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"Every day should have time schedule.\"," +
                                                        "\"2\":\"Fri is not valid week day.\"," +
                                                        "\"3\":\"Time can not be empty.(1)\"," +
                                                        "\"4\":\"10:30,12:00 is not valid time.\"," +
                                                        "\"5\":\"10-12 is not valid time.\"," +
                                                        "\"6\":\"End time can not be before start time.(4)\"}");
    }

//
//    @Test
//    public void Conflict_of_two_CourseSchedule_with_overlap_is_diagnosed_correctly() {
//        ExceptionList exceptionList = new ExceptionList();
//        try {
//            SectionSchedule courseSchedule1 = new SectionSchedule(List.of("Sunday", "Monday"), "10:30", "12:00");
//            SectionSchedule courseSchedule2 = new SectionSchedule(List.of("Wednesday", "Monday"), "09:30", "11:00");
//            assertTrue(courseSchedule1.hasConflict(courseSchedule2));
//        } catch (ExceptionList e) {
//            exceptionList.addExceptions(e.getExceptions());
//        }
//        assertFalse(exceptionList.hasException());
//    }
//
//
//    @Test
//    public void No_conflict_is_diagnosed_correctly_1() {
//        ExceptionList exceptionList = new ExceptionList();
//        try {
//            SectionSchedule courseSchedule1 = new SectionSchedule(List.of("Sunday", "Monday"), "10:30", "12:00");
//            SectionSchedule courseSchedule2 = new SectionSchedule(List.of("Wednesday", "Tuesday"), "10:30", "12:00");
//            assertFalse(courseSchedule1.hasConflict(courseSchedule2));
//        } catch (ExceptionList e) {
//            exceptionList.addExceptions(e.getExceptions());
//        }
//        assertFalse(exceptionList.hasException());
//    }
//
//    @Test
//    public void No_conflict_is_diagnosed_correctly_2() {
//        ExceptionList exceptionList = new ExceptionList();
//        try {
//            SectionSchedule courseSchedule1 = new SectionSchedule(List.of("Sunday", "Monday"), "10:30", "12:00");
//            SectionSchedule courseSchedule2 = new SectionSchedule(List.of("Sunday", "Monday"), "12:00", "13:00");
//            assertFalse(courseSchedule1.hasConflict(courseSchedule2));
//        } catch (ExceptionList e) {
//            exceptionList.addExceptions(e.getExceptions());
//        }
//        assertFalse(exceptionList.hasException());
//    }
}
