package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // as required by JPA, don't use it in your code
@Getter
public class StudyRecord {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private String term;
    @ManyToOne
    private Course course;
    @ManyToOne
    private Student student;    // required by JPA, as the "opposite side" relation :(
    @Embedded
    private Grade grade;

    public StudyRecord(String term, Course course, double grade) throws ExceptionList {
        this.term = term;
        this.course = course;
        ExceptionList exceptionList = new ExceptionList();
        try {
            this.grade = new Grade(grade);
        } catch (Exception e) {
            exceptionList.addNewException(e);
            throw exceptionList;
        }
    }

    public double getGrade() { return this.grade.getGrade(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyRecord that = (StudyRecord) o;
        return term.equals(that.term) &&
                course.equals(that.course) &&
                student.equals(that.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, course, student);
    }
}
