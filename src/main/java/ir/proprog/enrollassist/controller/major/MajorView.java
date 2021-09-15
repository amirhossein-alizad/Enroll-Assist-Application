package ir.proprog.enrollassist.controller.major;

import ir.proprog.enrollassist.domain.major.Major;
import lombok.Getter;

@Getter
public class MajorView {
    private Long majorId;
    private String majorNumber;
    private String majorName;
    private String faculty;

    public MajorView() {
    }

    public MajorView(Major major) {
        this.majorId = major.getId();
        this.majorNumber = major.getMajorNumber();
        this.majorName = major.getMajorName();
        this.faculty = major.getFaculty().toString();
    }
}
