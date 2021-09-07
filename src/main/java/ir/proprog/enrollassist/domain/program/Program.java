package ir.proprog.enrollassist.domain.program;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.student.StudentNumber;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Major major;
    private GraduateLevel graduateLevel;
    @ManyToMany
    private List<Course> courses;
    @Embedded
    private CreditRange creditRange;

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

    public void addCourse(Course ... course){
        this.courses.addAll(Arrays.asList(course));
    }
}
