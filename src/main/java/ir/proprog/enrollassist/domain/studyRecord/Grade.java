package ir.proprog.enrollassist.domain.studyRecord;

import lombok.Value;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.util.Objects;

@Value
@Embeddable
@Access(AccessType.FIELD)
public class Grade {
    public static final Grade ZERO = new Grade();

    double grade;

    public Grade() {
        this.grade = 0.0;
    }

    public Grade(double grade) throws Exception {
        if (grade < 0 || grade > 20)
            throw new Exception("Grade must be in range of (0, 20).");
        this.grade = (Math.round(grade * 100.0) / 100.0);;
    }

    public boolean isLessThan(double grade) { return this.grade < grade; }

    public boolean isEqualMoreThan(double otherGrade) { return this.grade >= otherGrade; }

    @Override
    public int hashCode() { return Objects.hash(grade); }

    @Override
    public boolean equals(Object o){
        Grade other = (Grade) o;
        if (o == null || getClass() != o.getClass()) return false;
        return this.grade == other.grade;
    }
}
