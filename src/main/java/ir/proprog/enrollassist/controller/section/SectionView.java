package ir.proprog.enrollassist.controller.section;

import ir.proprog.enrollassist.domain.course.CourseNumber;
import ir.proprog.enrollassist.domain.section.ExamTime;
import ir.proprog.enrollassist.domain.section.PresentationSchedule;
import ir.proprog.enrollassist.domain.section.Section;
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

}
