package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FacultyTest {
    @Test
    public void Faculty_instance_is_created_correctly_when_input_is_correct(){
        ExceptionList exceptionList = new ExceptionList();
        try{
            Faculty faculty = new Faculty("ece");
        } catch(ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.getExceptions().size(), 0);
    }

    @Test
    public void Exceptions_are_returned_correctly_if_input_was_incorrect() {
        ExceptionList exceptionList = new ExceptionList();
        try{
            Faculty faculty = new Faculty("");
        } catch(ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.getExceptions().size(), 1);
    }

    @Test
    public void Majors_are_added_correctly_to_faculty() {
        try {
            Major major1 = new Major("major1", "8101");
            Major major2 = new Major("major2", "8101");
            Major major3 = new Major("major3", "8101");
            Faculty faculty = new Faculty("faculty");
            faculty.addMajor(major1, major2, major3);
            assertThat(faculty.getMajors())
                    .containsExactlyInAnyOrder(major1, major2, major3);
        } catch (ExceptionList ignored) {
        }
    }
}
