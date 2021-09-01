package ir.proprog.enrollassist.domain.course;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.EnrollmentRules.EnrollmentRuleViolation;
import ir.proprog.enrollassist.domain.EnrollmentRules.PrerequisiteNotTaken;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.student.Student;
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
    private String title;
    private GraduateLevel graduateLevel;
    @Embedded
    private CourseNumber courseNumber;
    @Embedded
    private Credit credits;
    @ManyToMany
    private Set<Course> prerequisites = new HashSet<>();

    public Course(String courseNumber, String title, int credits, String graduateLevel) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        if (title.equals("")) {
            exceptionList.addNewException(new Exception("Course must have a name."));
        }
        this.title = title;
        try {
            this.courseNumber = new CourseNumber(courseNumber);
        } catch (Exception e) { exceptionList.addNewException(e); }
        try {
            this.credits = new Credit(credits);
        } catch (Exception e) { exceptionList.addNewException(e); }
        try {
            this.graduateLevel = GraduateLevel.valueOf(graduateLevel);
        } catch (Exception e) { exceptionList.addNewException(new Exception("Graduate level is not valid.")); }

        if (exceptionList.hasException())
            throw exceptionList;
    }

    public int getCredits() { return credits.getCredit(); }

    public Course withPre(Course... pres) {
        prerequisites.addAll(Arrays.asList(pres));
        return this;
    }

    public boolean equalsEducationGrade(GraduateLevel graduateLevel) {
        return this.graduateLevel.equals(graduateLevel);
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
        return Objects.hash(courseNumber.getCourseNumber());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", courseNumber.getCourseNumber(), title);
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
