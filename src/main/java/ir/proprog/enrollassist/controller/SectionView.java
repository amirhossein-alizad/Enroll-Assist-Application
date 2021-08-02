package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Section;
import lombok.AccessLevel;
import lombok.Getter;

public class SectionView {
    private Long sectionId;
    @Getter private String sectionNo;
    @Getter private Long courseId;
    private String courseNumber;
    private String courseTitle;
    private int courseCredits;

    public SectionView() {
    }

    public SectionView(Section section) {
        this.sectionId = section.getId();
        this.sectionNo = section.getSectionNo();
        this.courseId = section.getCourse().getId();
        this.courseNumber = section.getCourse().getCourseNumber();
        this.courseTitle = section.getCourse().getTitle();
        this.courseCredits = section.getCourse().getCredits();
    }

}
