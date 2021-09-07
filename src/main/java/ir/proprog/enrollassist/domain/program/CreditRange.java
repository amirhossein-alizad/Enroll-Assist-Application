package ir.proprog.enrollassist.domain.program;

import ir.proprog.enrollassist.domain.section.ExamTime;
import lombok.Value;

import javax.persistence.Embeddable;
import java.util.Objects;

@Value
@Embeddable
public class CreditRange {
    int minimum;
    int maximum;

    public CreditRange(int min, int max) throws Exception {
        if (min <= 0)
            throw new Exception("Minimum number of credits must be a positive integer.");
        if (max <= 0)
            throw new Exception("Maximum number of credits must be a positive integer.");
        if (min > max)
            throw new Exception("Maximum number of credits must larger than the minimum.");
        this.minimum = min;
        this.maximum = max;
    }

    public CreditRange() {
        this.minimum = 0;
        this.maximum = 140;
    }


    @Override
    public int hashCode() { return Objects.hash(minimum, maximum); }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditRange range = (CreditRange) o;
        return minimum == range.minimum && maximum == range.maximum;
    }
}
