package ir.proprog.enrollassist.controller.section;

import ir.proprog.enrollassist.domain.CourseNumber;
import ir.proprog.enrollassist.domain.ExamTime;
import ir.proprog.enrollassist.domain.PresentationSchedule;
import ir.proprog.enrollassist.domain.Section;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class SectionView {
    private Long sectionId;
    private String sectionNo;
    private ExamTime examTime;
    private Long courseId;
    private CourseNumber courseNumber;
    private String courseTitle;
    private int courseCredits;
    Set<PresentationSchedule> schedule = new HashSet<>();

    public SectionView() {
    }

    public SectionView(Section section) {
        this.sectionId = section.getId();
        this.sectionNo = section.getSectionNo();
        this.examTime = section.getExamTime();
        this.courseId = section.getCourse().getId();
        this.courseNumber = section.getCourse().getCourseNumber();
        this.courseTitle = section.getCourse().getTitle();
        this.courseCredits = section.getCourse().getCredits();
        this.schedule = section.getPresentationSchedule();
    }

    public SectionView(Section section, ExamTime examTime, Set<PresentationSchedule> schedule) {
        this.sectionId = section.getId();
        this.sectionNo = section.getSectionNo();
        this.examTime = examTime;
        this.schedule = schedule;
        this.courseId = section.getCourse().getId();
        this.courseNumber = section.getCourse().getCourseNumber();
        this.courseTitle = section.getCourse().getTitle();
        this.courseCredits = section.getCourse().getCredits();
    }

}
