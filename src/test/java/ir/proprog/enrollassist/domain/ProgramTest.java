package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.course.CourseNumber;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.program.Program;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProgramTest {

    @Test
    public void Program_with_invalid_inputs_cant_be_created() {
        String error = "";
        try {
            Program program = new Program(mock(Major.class), "Undergrad", 20, 12);
        } catch (ExceptionList exceptionList) {
            error = exceptionList.toString();
        }
        assertEquals(error, "{\"1\":\"Graduate level is not valid.\"," +
                "\"2\":\"Maximum number of credits must larger than the minimum.\"}");
    }

    @Test
    public void Course_with_different_GraduateLevel_cant_be_added_to_program() {
        String error = "";
        try {
            Course ap = mock(Course.class);
            when(ap.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
            when(ap.getCourseNumber()).thenReturn(new CourseNumber("1111222"));
            Program program = new Program(mock(Major.class), "PHD", 140, 142).addCourse(ap);
        } catch (Exception exception) {
            error = exception.toString();
        }
        assertEquals(error, "{\"1\":\"Course with course number 1111222 must have the same graduate level as program.\"}");
    }

}
