package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.EnrollmentRules.EnrollmentRuleViolation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.*;

import static org.hibernate.query.criteria.internal.ValueHandlerFactory.isNumeric;

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

    public Section(@NonNull Course course, String sectionNo) {
        this.validateSectionNo(sectionNo);
        this.sectionNo = sectionNo;
        this.course = course;
    }

    public Section(@NonNull Course course, String sectionNo, ExamTime examTime) {
        this.validateSectionNo(sectionNo);
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

    public void setExamTime(ExamTime examTime) {
        this.examTime = examTime;
    }


    public void setPresentationSchedule(List<String> schedule) throws ExceptionList {
        Set<PresentationSchedule> parsedPresentationSchedule = new HashSet<>();
        ExceptionList exceptionList = new ExceptionList();
        for (String s: schedule) {
            List<String> scheduleString = Arrays.asList(s.split(","));
            if (scheduleString.size() != 2)
                exceptionList.addNewException(new Exception(String.format("Schedule format is not valid.(%s)", s)));
            else {
                try {
                    PresentationSchedule sectionSchedule = new PresentationSchedule(scheduleString.get(0), scheduleString.get(1));
                    parsedPresentationSchedule.add(sectionSchedule);
                } catch (ExceptionList list) {
                    exceptionList.addExceptions(list.getExceptions());
                }
            }
        }
        if (exceptionList.hasException())
            throw exceptionList;
        else
            this.presentationSchedule = parsedPresentationSchedule;
    }

    public boolean hasConflict(Section otherSection) {
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

    public List<EnrollmentRuleViolation> courseCanBeTakenBy(Student student){ return course.canBeTakenBy(student); }

    public boolean studentHasPassedCourse(Student s){ return s.hasPassed(course); }

    public boolean courseIsEqualTo(Course that) { return course.equals(that); }

    @Override
    public int hashCode() {
        return Objects.hash(course, sectionNo);
    }

}
