package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.ExamTime;
import ir.proprog.enrollassist.domain.Section;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SectionView {
    private Long sectionId;
    private String sectionNo;
    private ExamTime examTime;
    private Long courseId;
    private String courseNumber;
    private String courseTitle;
    private int courseCredits;
    List<String> schedule = new ArrayList<>();

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
        schedule = section.presentationScheduleToString();
    }

    public SectionView(Section section, ExamTime examTime, List<String> schedule) {
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
