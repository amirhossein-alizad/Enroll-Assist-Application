package ir.proprog.enrollassist.domain.student;

import com.google.common.annotations.VisibleForTesting;
import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.studyRecord.Grade;
import ir.proprog.enrollassist.domain.studyRecord.StudyRecord;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // as required by JPA, don't use it in your code
@Getter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private GraduateLevel graduateLevel;
    @Embedded
    private StudentNumber studentNumber;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<StudyRecord> grades = new HashSet<>();
    private String name;
    @ManyToOne
    Major major;

    public Student(@NonNull String studentNumber, @NonNull String name) {
        this.studentNumber = new StudentNumber(studentNumber);
        this.name = name;
    }

    public Student(@NonNull String studentNumber, @NonNull String name, @NonNull Major major, @NonNull String graduateLevel) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        try {
            this.studentNumber = new StudentNumber(studentNumber);
        } catch (Exception e) { exceptionList.addNewException(e); }
        if (name.equals(""))
            exceptionList.addNewException(new Exception("Student name can not be empty."));
        try {
            this.graduateLevel = GraduateLevel.valueOf(graduateLevel);
        } catch (Exception e) { exceptionList.addNewException(new Exception("Graduate level is not valid.")); }

        if (exceptionList.hasException())
            throw exceptionList;

        this.name = name;
        this.major = major;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return studentNumber.equals(student.studentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber);
    }


    public boolean hasPassed(Course course) {
        for (StudyRecord sr : grades) {
            if (sr.getCourse().equals(course))
                return sr.isPassed(this.graduateLevel);
        }
        return false;
    }

    public Student setGrade(String term, Course course, double grade) throws ExceptionList {
        grades.add(new StudyRecord(term, course, grade));
        return this;
    }

    public int getTotalTakenCredits() {
        return grades.stream().mapToInt(e -> e.getCourse().getCredits()).sum();
    }

    public Grade calculateGPA() {
        double sum = grades.stream().mapToDouble(StudyRecord::weightedScore).sum();
        int credits = grades.stream().mapToInt(sr -> sr.getCourse().getCredits()).sum();
        if (credits == 0) return Grade.ZERO;
        try {
            return new Grade(sum / credits);
        } catch (Exception e) {
            return Grade.ZERO;
        }
    }

    @VisibleForTesting
    List<Course> getTakeableCourses(){
        List<Course> passed = grades.stream().filter(sr -> sr.isPassed(this.graduateLevel)).map(StudyRecord::getCourse).collect(Collectors.toList());
        List<Course> all = new ArrayList<>(major.getCoursesByGraduateLevel(this.graduateLevel));
        all.removeAll(passed);
        return all.stream().filter(course -> course.canBeTakenBy(this).isEmpty()).collect(Collectors.toList());
    }

    public List<Section> getTakeableSections(Iterable<Section> allSections){
        List<Course> courses = getTakeableCourses();
        List<Section> all = StreamSupport.stream(allSections.spliterator(), false).collect(Collectors.toList());
        return all.stream().filter(section -> courses.contains(section.getCourse())).collect(Collectors.toList());
    }

    public void setMajor(Major major) {
        this.major = major;
    }

}
