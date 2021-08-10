package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.ExamTime;
import ir.proprog.enrollassist.domain.Section;
import lombok.Getter;

@Getter
public class SectionView {
    private Long sectionId;
    private String sectionNo;
    private ExamTime examTime;
    private Long courseId;
    private String courseNumber;
    private String courseTitle;
    private int courseCredits;

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
    }

    public SectionView(Section section, ExamTime examTime) {
        this.sectionId = section.getId();
        this.sectionNo = section.getSectionNo();
        this.examTime = examTime;
        this.courseId = section.getCourse().getId();
        this.courseNumber = section.getCourse().getCourseNumber();
        this.courseTitle = section.getCourse().getTitle();
        this.courseCredits = section.getCourse().getCredits();
    }

}
