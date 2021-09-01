package ir.proprog.enrollassist.domain.studyRecord;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.course.Course;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // as required by JPA, don't use it in your code
@Getter
public class StudyRecord {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    @Embedded
    private Term term;
    @ManyToOne
    private Course course;
    @ManyToOne
    private Student student;    // required by JPA, as the "opposite side" relation :(
    @Embedded
    private Grade grade;

    public StudyRecord(String term, Course course, double grade) throws ExceptionList {
        this.course = course;
        ExceptionList exceptionList = new ExceptionList();
        try {
            this.term = new Term(term);
        } catch (Exception e) { exceptionList.addNewException(e); }
        try {
            this.grade = new Grade(grade);
        } catch (Exception e) { exceptionList.addNewException(e); }

        if (exceptionList.hasException())
            throw exceptionList;
    }

    public String getTerm() {
        return this.term.getTermCode();
    }

    public double weightedScore() {
        return grade.getGrade() * course.getCredits();
    }

    public boolean isPassed(GraduateLevel graduateLevel) {
        return grade.isEqualMoreThan(graduateLevel.getMinValidGrade()) ||
                grade.isEqualMoreThan(course.getGraduateLevel().getMinValidGrade());
    }

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
