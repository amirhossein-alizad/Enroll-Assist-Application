package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.faculty.Faculty;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.utils.TestCourseBuilder;
import ir.proprog.enrollassist.domain.utils.TestFacultyBuilder;
import ir.proprog.enrollassist.domain.utils.TestMajorBuilder;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class FacultyTest {
    private Faculty faculty;
    private TestFacultyBuilder testFacultyBuilder = new TestFacultyBuilder();
    private TestMajorBuilder testMajorBuilder = new TestMajorBuilder();
    private ExceptionList exceptionList = new ExceptionList();
    private Major major1, major2;

    @Test
    public void Faculty_instance_is_created_correctly_when_input_is_correct(){
        try{
            faculty = testFacultyBuilder.build();
        } catch(ExceptionList ignored) {
        }
        assertEquals(exceptionList.getExceptions().size(), 0);
    }

    @Test
    public void Faculty_name_can_not_be_empty() {
        Throwable error = assertThrows(ExceptionList.class, () ->
                faculty = testFacultyBuilder.facultyName("")
                        .build());
        assertEquals(error.toString(),
                "{\"1\":\"Faculty name can not be Empty.\"}");
    }

    @Test
    public void Majors_are_added_correctly_to_faculty() throws ExceptionList{
        major1 = testMajorBuilder.majorName("M1")
                .build();
        major2 = testMajorBuilder.majorName("M2")
                .build();
        faculty = testFacultyBuilder.build();
        faculty.addMajor(major1, major2);
        assertThat(faculty.getMajors())
                .containsExactlyInAnyOrder(major1, major2);
    }
}
