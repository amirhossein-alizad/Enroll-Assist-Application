package ir.proprog.enrollassist.domain;

import lombok.Getter;
import lombok.Value;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Embeddable
@Value
public class EducationGrade {
    String grade;

    public static final EducationGrade ZERO = new EducationGrade();

    public EducationGrade() { this.grade = ""; }

    public EducationGrade(String grade) throws Exception {
        List<String> allGrades = List.of("");
        if (allGrades.contains(grade))
            this.grade = grade;
        else
            throw new Exception("Education grade is not valid.");
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
