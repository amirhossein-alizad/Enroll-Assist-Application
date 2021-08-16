package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.EnrollmentRules.EnrollmentRuleViolation;
import ir.proprog.enrollassist.domain.EnrollmentRules.PrerequisiteNotTaken;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // as required by JPA, don't use it in your code
@Getter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String courseNumber;
    private String title;
    @Embedded
    private Credit credits;
    private boolean hasExam = false;
    @ManyToMany
    private Set<Course> prerequisites = new HashSet<>();

    public Course(String courseNumber, String title, int credits) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        this.courseNumber = courseNumber;
        this.title = title;
        try {
            this.credits = new Credit(credits);
        } catch (Exception e) {
            exceptionList.addNewException(e);
        }
        exceptionList.addExceptions(this.validateCourseInfo(courseNumber, title));
        if (exceptionList.hasException())
            throw exceptionList;
    }

    public int getCredits() { return credits.getCredit(); }

    private List<Exception> validateCourseInfo(String courseNumber, String title) {
        List<Exception> exceptions = new ArrayList<>();
        try {
            this.validateCourseNumber(courseNumber);
        }catch (Exception e) {
            exceptions.add(e);
        }
        if (title.equals(""))
            exceptions.add(new Exception("Course must have a name."));
        return exceptions;
    }

    private void validateCourseNumber(String courseNumber) throws Exception {
        if (courseNumber.equals(""))
            throw new Exception("Course number cannot be empty.");
        try {
            Integer.parseInt(courseNumber);
            if (courseNumber.length() != 7)
                throw new Exception("Course number must contain 7 numbers.");
        }catch (Exception exception) {
            throw new Exception("Course number must contain 7 numbers.");
        }
    }

    public Course withPre(Course... pres) {
        prerequisites.addAll(Arrays.asList(pres));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return courseNumber.equals(course.courseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseNumber);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", courseNumber, title);
    }

    public void setPrerequisites(Set<Course> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public Course setHasExam(boolean hasExam) {
        this.hasExam = hasExam;
        return this;
    }

    public boolean getHasExam() { return hasExam; }

    public List<EnrollmentRuleViolation> canBeTakenBy(Student student) {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        Set<Course> prereqs = getPrerequisites();
        for (Course pre : prereqs) {
            if (!student.hasPassed(pre))
                violations.add(new PrerequisiteNotTaken(this, pre));
        }
        return violations;
    }

}
