package ir.proprog.enrollassist.domain;

import lombok.Getter;
import lombok.Value;

import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
@Value
public class EducationGrade {
    String grade;
    double minValidGPA;

    public static final EducationGrade ZERO = new EducationGrade();

    public EducationGrade() {
        this.grade = "";
        this.minValidGPA = 0.0;
    }

    public EducationGrade(String grade) throws Exception {
        if (grade.equals("Undergraduate"))
            this.minValidGPA = 10.0;
        else if (grade.equals("Masters"))
            this.minValidGPA = 12.0;
        else if (grade.equals("PHD"))
            this.minValidGPA = 14.0;
        else
            throw new Exception("Education grade is not valid.");

        this.grade = grade;
    }

    @Override
    public int hashCode() { return Objects.hash(grade); }

    @Override
    public boolean equals(Object o){
        EducationGrade other = (EducationGrade) o;
        if (o == null || (this.getClass() != o.getClass())) return false;
        return this.grade.equals(other.grade);
    }

}
