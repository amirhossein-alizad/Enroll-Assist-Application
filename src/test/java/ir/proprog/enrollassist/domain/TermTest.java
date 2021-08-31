package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.domain.studyRecord.Term;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TermTest {

    @Test
    void Term_with_empty_field_cant_be_created() {
        String error = "";
        try {
            Term term = new Term("");
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "Term can not be empty.");
    }

    @Test
    void Term_with_none_number_string_cant_be_created() {
        String error = "";
        try {
            Term term = new Term("AS23");
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "Term format  must be number.");
    }

    @Test
    void Term_with_length_that_is_not_equal_to_5_is_not_created() {
        String error = "";
        try {
            Term term = new Term("1234");
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "Term format is not valid.");
    }

    @Test
    void Term_with_invalid_season_is_not_created() {
        String error = "";
        try {
            Term term = new Term("12985");
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "Season of term is not valid.");
    }

    @Test
    void Equals_function_do_its_job_correctly() throws Exception {
        Term term1 = new Term("13981");
        Term term2 = new Term("13981");
        assertTrue(term1.equals(term2));
    }
}
