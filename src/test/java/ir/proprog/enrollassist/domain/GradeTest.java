package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.domain.studyRecord.Grade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GradeTest {
    @Test
    public void Grade_should_hold_two_decimal_points() throws Exception {
        Grade grade = new Grade(13.567);
        assertEquals(grade.getGrade(), 13.57);
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
