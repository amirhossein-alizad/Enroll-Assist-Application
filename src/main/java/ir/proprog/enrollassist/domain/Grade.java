package ir.proprog.enrollassist.domain;

import lombok.Getter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
@Access(AccessType.FIELD)
public class Grade {
    private final double grade;

    public Grade() {
        this.grade = 0.0;
    }

    public Grade(double grade) throws Exception {
        if (grade < 0 || grade > 20)
            throw new Exception("Grade must be in range of (0, 20).");
        String[] number = Double.toString(grade).split("\\.");
        if (number.length > 1)
            if (number[1].length() > 2)
                throw new Exception("There should be at most 2 decimal places in grade.");
        this.grade = grade;
    }

    public boolean isPassingGrade() { return this.grade >= 10.0; }

    @Override
    public int hashCode() { return Objects.hash(grade); }

    @Override
    public boolean equals(Object o){
        Grade other = (Grade) o;
        if (o == null || getClass() != o.getClass()) return false;
        return this.grade == other.getGrade();
    }
}
