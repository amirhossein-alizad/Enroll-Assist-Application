package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

public class SectionTest {
    @Test
    public void None_number_character_is_not_valid_for_section_number() {
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
    public void scheduleToString_method_works_correctly() throws Exception {
        Course math1 = new Course("1111111", "MATH1", 3);
        Section section = new Section(math1, "01");
        section.setPresentationSchedule(Set.of( new PresentationSchedule("Sunday", "10:00", "12:00"), new PresentationSchedule("Wednesday", "10:00", "12:00")));
        assertThat(section.scheduleToString())
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrder("Sunday 10:00 - 12:00", "Wednesday 10:00 - 12:00");
    }
}
