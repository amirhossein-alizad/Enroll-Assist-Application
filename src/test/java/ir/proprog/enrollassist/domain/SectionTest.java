package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class SectionTest {
    @Test
    public void None_number_character_is_not_valid_for_section_number() {
        Course course = mock(Course.class);
        String exception = "";
        try {
            Section section = new Section(course, "e34");
        } catch (IllegalArgumentException illegalArgumentException) {
            exception = illegalArgumentException.getMessage();
        }
        assertEquals(exception, "Section number must be number");
    }


    @Test
    public void SectionSchedule_can_not_set_in_section_with_incorrect_input() {
        ExceptionList exceptionList = new ExceptionList();
        Course course = mock(Course.class);
        try {
            Section section = new Section(course, "01");
            List<String> schedule = new ArrayList<>();
            schedule.add("Monday,");
            schedule.add("Fri,10:30,12:00");
            schedule.add("Fri,10-12");
            schedule.add("Sunday,13:30-12:00");
            section.setSectionSchedule(schedule);

        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"Schedule format is not valid.(Monday,)\"," +
                                                        "\"2\":\"Schedule format is not valid.(Fri,10:30,12:00)\"," +
                                                        "\"3\":\"Fri is not valid week day.\"," +
                                                        "\"4\":\"10-12 is not valid time.\"," +
                                                        "\"5\":\"End time can not be before start time.(13:30-12:00)\"}");
    }
}
