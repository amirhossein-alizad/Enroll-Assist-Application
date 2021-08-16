package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GradeTest {
    @Test
    public void Grade_cannot_hold_more_than_two_decimal_points() {
        Exception exception = assertThrows(Exception.class, () -> {
            Grade grade = new Grade(13.567);
        });
        assertEquals(exception.getMessage(), "There should be at most 2 decimal places in grade.");
    }

    @Test
    public void Grade_cannot_be_more_than_20() {
        Exception exception = assertThrows(Exception.class, () -> {
            Grade grade = new Grade(21.3);
        });
        assertEquals(exception.getMessage(), "Grade must be in range of (0, 20).");
    }

    @Test
    public void Grade_cannot_be_negative() {
        Exception exception = assertThrows(Exception.class, () -> {
            Grade grade = new Grade(-1);
        });
        assertEquals(exception.getMessage(), "Grade must be in range of (0, 20).");
    }

    @Test
    public void Valid_grades_are_created_correctly() throws Exception {
        Grade grade = new Grade(19.45);
        assertEquals(grade.getGrade(), 19.45);
    }
}
