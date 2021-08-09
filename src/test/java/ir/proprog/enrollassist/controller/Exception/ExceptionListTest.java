package ir.proprog.enrollassist.controller.Exception;

import ir.proprog.enrollassist.Exception.ExceptionList;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionListTest {

    @Test
    public void List_Of_Exception_get_to_string_correctly() {
        ExceptionList exceptionList = new ExceptionList();
        Exception e = new Exception("This is not correct");
        exceptionList.addNewException(e);
        assertThat(exceptionList.toString()).isEqualTo("{\"1\":\"This is not correct\"}");
    }
}
