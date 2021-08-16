package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
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
        assertThat(exceptionList.getExceptions())
                .hasSize(2);
    }
}
