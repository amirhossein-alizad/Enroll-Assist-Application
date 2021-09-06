package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.utils.TestCourseBuilder;
import ir.proprog.enrollassist.domain.utils.TestMajorBuilder;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MajorTest {
    private TestMajorBuilder majorBuilder;
    private Course course1, course2, course3;
    Major major;
    @BeforeEach
    public void SetUp() throws ExceptionList{
        course1 = new TestCourseBuilder()
                .courseNumber("1111111")
                .title("MATH1")
                .credits(3)
                .graduateLevel("Undergraduate")
                .build();
        course2 = new TestCourseBuilder()
                .courseNumber("2222222")
                .title("MATH2")
                .credits(3)
                .graduateLevel("Undergraduate")
                .build();
        course3 = new TestCourseBuilder()
                .courseNumber("3333333")
                .title("MATH3")
                .credits(3)
                .graduateLevel("Undergraduate")
                .build();
        majorBuilder = new TestMajorBuilder()
                .majorNumber("8101")
                .majorName("CE")
                .courses(course1, course2, course3);
        major = null;
    }

    @Test
    public void Major_instance_is_created_correctly_when_input_is_correct(){
        ExceptionList exceptionList = new ExceptionList();
        try{
            Major major = majorBuilder.build();
        } catch(ExceptionList e) {
           exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.getExceptions().size(), 0);
    }

    @Test
    public void Major_name_and_major_number_can_not_be_empty(){
        Throwable error = assertThrows(ExceptionList.class, () ->
                major = majorBuilder
                        .majorName("")
                        .majorNumber("")
                        .build());
        assertEquals(error.toString(),
                "{\"1\":\"Major name can not be empty.\","
                        + "\"2\":\"Major number can not be empty.\"}");
    }

    @Test
    public void Courses_are_added_correctly_to_major() throws ExceptionList{
        major = majorBuilder.build();
        assertThat(major.getCourses())
                .containsExactlyInAnyOrder(course1, course2, course3);
    }
}
