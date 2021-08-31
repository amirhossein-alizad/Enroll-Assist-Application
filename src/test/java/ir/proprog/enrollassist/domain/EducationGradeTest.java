package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EducationGradeTest {

    @Test
    public void EducationGrade_class_cant_be_created_with_invalid_input_1() {
        String error = "";
        try {
            EducationGrade educationGrade = new EducationGrade("");
        } catch (Exception exception) {
            error = exception.getMessage();
        }
        assertEquals(error, "Education grade is not valid.");
    }


    @Test
    public void EducationGrade_class_cant_be_created_with_invalid_input_2() {
        String error = "";
        try {
            EducationGrade educationGrade = new EducationGrade("Student");
        } catch (Exception exception) {
            error = exception.getMessage();
        }
        assertEquals(error, "Education grade is not valid.");
    }

    @Test
    public void EducationGrade_class_can_be_created_with_valid_input() {
        String error = "";
        try {
            EducationGrade educationGrade = new EducationGrade("PHD");
            assertEquals(educationGrade.getGrade(), "PHD");
            assertEquals(educationGrade.getMinValidGPA(), 14.0);
        } catch (Exception exception) {
            error = exception.getMessage();
        }
        assertEquals(error, "");
    }
}
