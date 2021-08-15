package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

public class SectionTest {
    @Test
    public void None_number_character_is_not_valid_for_section_number() throws Exception {
        Course course = mock(Course.class);
        String exception = "";
        try {
            Section section = new Section(course, "e34");
        } catch (ExceptionList exceptionList) {
            exception = exceptionList.getExceptions().get(0).getMessage();
        }
        assertEquals(exception, "Section number must be number");
    }


    @Test
    public void SectionSchedule_with_incorrect_input_is_not_created() throws Exception {
        ExceptionList exceptionList = new ExceptionList();
        Course course = mock(Course.class);
        try {
            List<String> schedule = new ArrayList<>();
            schedule.add("Monday,");
            schedule.add("Fri,10:30,12:00");
            schedule.add("Fri,10-12");
            schedule.add("Sunday,13:30-12:00");
            Section section = new Section(course, "01", new ExamTime("2021-06-21T14:00", "2021-06-21T17:00"), schedule);
        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"Schedule format is not valid.(Monday,)\"," +
                                                        "\"2\":\"Schedule format is not valid.(Fri,10:30,12:00)\"," +
                                                        "\"3\":\"Fri is not valid week day.\"," +
                                                        "\"4\":\"10-12 is not valid time.\"," +
                                                        "\"5\":\"End time can not be before start time.(13:30-12:00)\"}");
    }

    @Test
    public void SectionSchedule_with_correct_input_is_created()  throws Exception {
        ExceptionList exceptionList = new ExceptionList();
        Course course = mock(Course.class);
        try {
            List<String> schedule = new ArrayList<>();
            schedule.add("Sunday,10:30-12:00");
            schedule.add("Monday,10:30-12:00");
            schedule.add("Tuesday,10:30-12:00");
            Section section = new Section(course, "01", new ExamTime("2021-06-21T14:00", "2021-06-21T17:00"), schedule);
        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }

    @Test
    public void SectionSchedule_can_set_in_section_with_correct_input()  throws Exception {
        ExceptionList exceptionList = new ExceptionList();
        Course course = mock(Course.class);
        try {
            Section section = new Section(course, "01");
            List<String> schedule = new ArrayList<>();
            schedule.add("Sunday,10:30-12:00");
            schedule.add("Monday,10:30-12:00");
            schedule.add("Tuesday,10:30-12:00");
            section.setPresentationSchedule(schedule);
        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }


}
