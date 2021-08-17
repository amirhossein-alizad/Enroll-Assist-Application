package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CourseNumberTest {
    @Test
    void CourseNo_with_empty_field_cant_be_created() {
        String error = "";
        try {
            CourseNumber courseNumber = new CourseNumber("");
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "Course number cannot be empty.");
    }

    @Test
    void CourseNo_with_none_number_string_cant_be_created() {
        String error = "";
        try {
            CourseNumber courseNumber = new CourseNumber("ABC32");
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "Course number must be number.");
    }


    @Test
    void CourseNo_with_string_of_numbers_that_dous_not_have_7_characters_cant_be_created() {
        String error = "";
        try {
            CourseNumber courseNumber = new CourseNumber("112");
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "Course number must contain 7 numbers.");
    }

    @Test
    void CourseNo_with_valid_input_string_can_be_created() {
        String error = "Ok";
        try {
            CourseNumber courseNumber = new CourseNumber("3287651");
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "Ok");
    }

    @Test
    void Equals_function_do_its_job_correctly() throws Exception {
        CourseNumber courseNumber1 = new CourseNumber("3287651");
        CourseNumber courseNumber2 = new CourseNumber("3287651");
        assertTrue(courseNumber1.equals(courseNumber2));
    }
}
