package ir.proprog.enrollassist.domain.program;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.major.Major;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Major major;
    @Getter
    private GraduateLevel graduateLevel;
    @Getter
    @ManyToMany
    private Set<Course> courses = new HashSet<>();
    @Embedded
    private CreditRange creditRange;
    @Getter private ProgramType programType;

    public Program(Major major, String graduateLevel, int minimum, int maximum) throws ExceptionList {
        this.major = major;
        ExceptionList exceptionList = new ExceptionList();
        try {
            this.graduateLevel = GraduateLevel.valueOf(graduateLevel);
        } catch (Exception e) { exceptionList.addNewException(new Exception("Graduate level is not valid.")); }
        try {
            this.creditRange = new CreditRange(minimum, maximum);
        } catch (Exception e) { exceptionList.addNewException(e); }
        if (exceptionList.hasException())
            throw exceptionList;
    }

    public Program addCourse(Course ... course) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        for (Course c: course) {
            if (!c.getGraduateLevel().equals(this.graduateLevel))
                exceptionList.addNewException(new Exception(String.format("Course with course number %s must have the same graduate level as program.", c.getCourseNumber().getCourseNumber())));
        }
        if (exceptionList.hasException())
            throw exceptionList;
        this.courses.addAll(Arrays.asList(course));
        return this;
    }

    public boolean hasCourse(Course course) {
        return this.courses.contains(course);
    }
}
