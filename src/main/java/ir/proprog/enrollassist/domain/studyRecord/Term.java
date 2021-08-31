package ir.proprog.enrollassist.domain.studyRecord;

import lombok.Getter;
import lombok.Value;

import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
@Value
public class Term {
    String termCode;

    public Term() {
        this.termCode = "00001";
    }

    public Term(String term) throws Exception {
        this.validate(term);
        this.termCode = term;
    }

    private void validate(String term) throws Exception {
        if (term.equals(""))
            throw new Exception("Term can not be empty.");
        try {
            Integer.parseInt(term);
        } catch (Exception exception) { throw new Exception("Term format  must be number."); }
        if (term.length() != 5)
            throw new Exception("Term format is not valid.");
        int season = Integer.parseInt(term.substring(4));
        if ((season < 1) || (season > 3))
            throw new Exception("Season of term is not valid.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return this.termCode.equals(term.termCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.termCode);
    }

}
