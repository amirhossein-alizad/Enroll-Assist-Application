package ir.proprog.enrollassist.domain.course;

import lombok.Getter;
import lombok.Value;

import javax.persistence.Embeddable;
import java.util.Objects;

@Value
@Getter
@Embeddable
public class Credit {
    int credit;

    public Credit() {
        this.credit = 0;
    }

    public Credit(int credit) throws Exception {
        if (credit < 0 || credit > 4)
            throw new Exception("Credit must be one of the following values: 0, 1, 2, 3, 4.");
        this.credit = credit;
    }

    @Override
    public int hashCode() { return Objects.hash(credit); }

    @Override
    public boolean equals(Object o){
        if (o == null || getClass() != o.getClass()) return false;
        Credit other = (Credit) o;
        return this.credit == other.getCredit();
    }
}
