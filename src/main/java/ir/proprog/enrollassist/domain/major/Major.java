package ir.proprog.enrollassist.domain.major;


import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.faculty.Faculty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Major {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String majorNumber;
    private String majorName;
    @ManyToMany
    Set<Course> courses = new HashSet<>();
    @ManyToOne(cascade = CascadeType.MERGE)
    Faculty faculty;

    public Major(String majorNumber, String majorName) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        if(majorName.equals(""))
            exceptionList.addNewException(new Exception("Major name can not be empty"));
        if(majorNumber.equals(""))
            exceptionList.addNewException(new Exception("Major number can not be empty"));
        if(exceptionList.hasException())
            throw exceptionList;
        this.majorName = majorName;
        this.majorNumber = majorNumber;
    }

    public Set<Course> getCoursesByGraduateLevel(GraduateLevel graduateLevel) {
        Set<Course> coursesOfEducationGrade = new HashSet<>();
        for (Course c: this.courses)
            if (c.equalsEducationGrade(graduateLevel))
                coursesOfEducationGrade.add(c);
        return coursesOfEducationGrade;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Major major = (Major) o;
        return majorNumber.equals(major.majorNumber);
    }

    @Override
    public int hashCode() { return Objects.hash(majorNumber, majorName); }

    public void addCourse(Course ... course){
        this.courses.addAll(Arrays.asList(course));
    }
}
