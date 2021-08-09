package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.Exception.CourseException.CourseCreditsNegative;
import ir.proprog.enrollassist.domain.Exception.CourseException.CourseNumberEmpty;
import ir.proprog.enrollassist.domain.Exception.CourseException.CourseNumberInvalid;
import ir.proprog.enrollassist.domain.Exception.CourseException.CourseTitleEmpty;
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
    private int credits;
    @ManyToMany
    private Set<Course> prerequisites = new HashSet<>();

    public Course(String courseNumber, String title, int credits) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        exceptionList.addExceptions(this.validateCourseInfo(courseNumber, title, credits));
        if (exceptionList.hasException())
            throw exceptionList;
        this.courseNumber = courseNumber;
        this.title = title;
        this.credits = credits;
    }

    private List<Exception> validateCourseInfo(String courseNumber, String title, int credits) {
        List<Exception> exceptions = new ArrayList<>();
        try {
            this.validateCourseNumber(courseNumber);
        }catch (Exception e) {
            exceptions.add(e);
        }
        if (title.equals(""))
            exceptions.add(new CourseTitleEmpty());
        if (credits < 0)
            exceptions.add(new CourseCreditsNegative());
        return exceptions;
    }

    private void validateCourseNumber(String courseNumber) throws Exception {
        if (courseNumber.equals(""))
            throw new CourseNumberEmpty();
        try {
            Integer.parseInt(courseNumber);
            if (courseNumber.length() != 7)
                throw new CourseNumberInvalid();
        }catch (Exception exception) {
            throw new CourseNumberInvalid();
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
