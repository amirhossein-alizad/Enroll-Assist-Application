package ir.proprog.enrollassist.domain;

import java.util.Objects;

public class CourseRequestedTwice extends EnrollmentRuleViolation {
    private Section section1;
    private Section section2;

    public CourseRequestedTwice(Section section1, Section section2) {
        this.section1 = section1;
        this.section2 = section2;
    }

    public Section getSection1() {
        return section1;
    }

    public Section getSection2() {
        return section2;
    }

    public Course getCourse() {
        return section1.getCourse();
    }

    @Override
    public String toString() {
        return String.format("%s is requested to be taken twice", getCourse());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseRequestedTwice that = (CourseRequestedTwice) o;
        return Objects.equals(section1, that.section1) &&
                Objects.equals(section2, that.section2);
    }
}
