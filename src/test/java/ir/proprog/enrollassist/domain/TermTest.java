package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.domain.studyRecord.Term;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TermTest {

    @Test
    void Term_with_empty_field_cannot_be_created() {
        Throwable error = assertThrows(Exception.class, () -> { Term term = new Term(""); });
        assertEquals(error.getMessage(), "Term can not be empty.");
    }

    @Test
    void Term_number_can_only_consist_of_numerical_characters() {
        Throwable error = assertThrows(Exception.class, () -> { Term term = new Term("AS23"); });
        assertEquals(error.getMessage(), "Term format  must be number.");
    }

    @Test
    void Term_number_can_only_contain_five_digits() {
        Throwable error = assertThrows(Exception.class, () -> { Term term = new Term("1234"); });
        assertEquals(error.getMessage(), "Term format is not valid.");
    }

    @Test
    void Term_with_invalid_season_cannot_be_created() {
        Throwable error = assertThrows(Exception.class, () -> { Term term = new Term("12985"); });
        assertEquals(error.getMessage(), "Season of term is not valid.");
    }
}
