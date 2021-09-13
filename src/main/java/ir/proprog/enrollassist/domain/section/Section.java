package ir.proprog.enrollassist.domain.section;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.EnrollmentRules.EnrollmentRuleViolation;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.course.Course;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.*;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // as required by JPA, don't use it in your code
@Getter
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String sectionNo;
    @Embedded
    private ExamTime examTime;
    @ManyToOne
    private Course course;
    @ElementCollection
    private Set<PresentationSchedule> presentationSchedule = new HashSet<>();

    public Section(@NonNull Course course, String sectionNo) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        try {
            this.validateSectionNo(sectionNo);
        } catch (IllegalArgumentException e) {
            exceptionList.addNewException(e);
        }
        if (exceptionList.hasException())
            throw exceptionList;
        this.sectionNo = sectionNo;
        this.course = course;
    }

    public Section(@NonNull Course course, String sectionNo, @NonNull ExamTime examTime, Set<PresentationSchedule> schedule) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        try {
            this.validateSectionNo(sectionNo);
        }catch (IllegalArgumentException e) {
            exceptionList.addNewException(e);
        }
        try {
            examTime.validate();
        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        if (exceptionList.hasException()) {
            throw exceptionList;
        }

        this.presentationSchedule = schedule;
        this.sectionNo = sectionNo;
        this.course = course;
        this.examTime = examTime;
    }

    private void validateSectionNo(String sectionNo) {
        if (sectionNo.equals(""))
            throw new IllegalArgumentException("Section number cannot be empty");
        try {
            Integer.parseInt(sectionNo);
        } catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException("Section number must be number");
        }
    }

    public void setExamTime(ExamTime examTime) throws ExceptionList {
        examTime.validate();
        this.examTime = examTime;
    }

    public void setPresentationSchedule(Set<PresentationSchedule> schedule) {
        this.presentationSchedule = schedule;
    }

    public boolean hasScheduleConflict(Section otherSection) {
        for (PresentationSchedule thisSectionSchedule: this.presentationSchedule) {
            for (PresentationSchedule otherSectionSchedule: otherSection.presentationSchedule) {
                if (thisSectionSchedule.hasConflict(otherSectionSchedule))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return course.equals(section.course) && sectionNo.equals(section.sectionNo);
    }

    public List<String> scheduleToString(){
        List<String> list = new ArrayList<>();
        for(PresentationSchedule ps: presentationSchedule)
            list.add(ps.toString());
        return list;
    }

    public boolean studentHasPassedCourse(Student s){ return s.hasPassed(course); }

    @Override
    public int hashCode() {
        return Objects.hash(course, sectionNo);
    }

}
