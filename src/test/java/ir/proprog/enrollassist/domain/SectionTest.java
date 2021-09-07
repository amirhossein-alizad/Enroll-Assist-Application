package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.section.PresentationSchedule;
import ir.proprog.enrollassist.domain.section.Section;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        Section section = new Section(math1, "01");
        section.setPresentationSchedule(Set.of( new PresentationSchedule("Sunday", "10:00", "12:00"), new PresentationSchedule("Wednesday", "10:00", "12:00")));
        assertThat(section.scheduleToString())
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrder("Sunday 10:00 - 12:00", "Wednesday 10:00 - 12:00");
    }

    @Test
    public void Conflict_of_schedules_find_correctly() throws Exception {
        Course course = new Course("1111111", "MATH1", 3, "Undergraduate");
        PresentationSchedule p1 = new PresentationSchedule("Sunday", "10:00", "12:00");
        PresentationSchedule p2 = new PresentationSchedule("Sunday", "11:00", "13:00");
        Section section1 = new Section(course, "01");
        Section section2 = new Section(course, "02");
        section1.setPresentationSchedule(Set.of(p1));
        section2.setPresentationSchedule(Set.of(p2));
        assertTrue(section1.hasScheduleConflict(section2));
    }
}
