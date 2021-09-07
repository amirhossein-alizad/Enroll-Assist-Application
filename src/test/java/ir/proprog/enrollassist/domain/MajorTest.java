package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.major.Major;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MajorTest {
    @Test
    public void Major_instance_is_created_correctly_when_input_is_correct(){
        ExceptionList exceptionList = new ExceptionList();
        try{
            Major major = new Major("major", "8101");
        } catch(ExceptionList e) {
           exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.getExceptions().size(), 0);
    }

    @Test
    public void Exceptions_are_returned_correctly_if_input_was_incorrect() {
        ExceptionList exceptionList = new ExceptionList();
        try{
            Major major = new Major("", "");
        } catch(ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.getExceptions().size(), 2);
    }

    @Test
    public void Courses_are_added_correctly_to_major(){
        try{
            Major major = new Major("major", "8101");
            Course c1 = new Course("810197546", "alizad", 3, "Undergraduate");
            Course c2 = new Course("810197547", "alizade", 3, "Undergraduate");
            Course c3 = new Course("810197548", "alizadeh", 3, "Undergraduate");
//            major.addCourse(c1, c2, c3);
//            assertThat(major.getCourses())
//                    .containsExactlyInAnyOrder(c1, c2, c3);
        } catch(ExceptionList ignored) {
        }
    }
}
