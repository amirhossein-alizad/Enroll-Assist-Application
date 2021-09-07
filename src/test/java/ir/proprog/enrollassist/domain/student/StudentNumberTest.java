package ir.proprog.enrollassist.domain.student;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StudentNumberTest {
    @Test
    public void Empty_student_number_is_not_accepted() {
        Throwable error = assertThrows(IllegalArgumentException.class, () -> {
            StudentNumber studentNumber = new StudentNumber("");
        });
        assertEquals(error.getMessage(), "Student number can not be empty.");
    }

    @Test
    public void Student_number_instance_is_created_correctly(){
        StudentNumber studentNumber = new StudentNumber("123456789");
        assertEquals(studentNumber.getNumber(), "123456789");
    }

    @Test
    public void Student_number_instances_with_same_number_are_equals(){
        StudentNumber studentNumber1 = new StudentNumber("123456789");
        StudentNumber studentNumber2 = new StudentNumber("123456789");
        assertEquals(studentNumber1, studentNumber2);
    }
}
