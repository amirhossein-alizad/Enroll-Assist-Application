package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.Major;
import lombok.Getter;

@Getter
public class MajorView {
    private Long majorId;
    private String majorNumber;
    private String majorName;

    public MajorView() {
    }

    public MajorView(Major major) {
        this.majorId = major.getId();
        this.majorNumber = major.getMajorNumber();
        this.majorName = major.getMajorName();
    }
}
