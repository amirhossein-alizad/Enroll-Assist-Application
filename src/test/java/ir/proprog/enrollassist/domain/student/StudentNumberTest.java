package ir.proprog.enrollassist.domain.student;

import ir.proprog.enrollassist.domain.student.StudentNumber;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StudentNumberTest {
    @Test
    public void Empty_student_number_is_not_accepted(){
        try{
            StudentNumber studentNumber = new StudentNumber("");
        }catch (IllegalArgumentException e){
            assertEquals(e.getMessage(), "Student number can not be empty.");
        }
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
        assertTrue(studentNumber1.equals(studentNumber2));
    }
}
