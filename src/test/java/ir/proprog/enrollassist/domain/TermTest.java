package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.studyRecord.Term;
import ir.proprog.enrollassist.domain.utils.TestStudentBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class TermTest {

    @Test
    void Term_with_empty_field_cant_be_created() {
        Throwable error = assertThrows(Exception.class, () -> { Term term = new Term(""); });
        assertEquals(error.getMessage(), "Term can not be empty.");
    }

    @Test
    void Term_with_none_number_string_cant_be_created() {
        Throwable error = assertThrows(Exception.class, () -> { Term term = new Term("AS23"); });
        assertEquals(error.getMessage(), "Term format  must be number.");
    }

    @Test
    void Term_with_length_that_is_not_equal_to_5_is_not_created() {
        Throwable error = assertThrows(Exception.class, () -> { Term term = new Term("1234"); });
        assertEquals(error.getMessage(), "Term format is not valid.");
    }

    @Test
    void Term_with_invalid_season_is_not_created() {
        Throwable error = assertThrows(Exception.class, () -> { Term term = new Term("12985"); });
        assertEquals(error.getMessage(), "Season of term is not valid.");
    }

    @Test
    void Equals_function_do_its_job_correctly() throws Exception {
        Term term1 = new Term("13981");
        Term term2 = new Term("13981");
        assertTrue(term1.equals(term2));
    }
}
