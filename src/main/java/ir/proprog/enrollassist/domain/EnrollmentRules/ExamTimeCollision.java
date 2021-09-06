package ir.proprog.enrollassist.domain.EnrollmentRules;

import ir.proprog.enrollassist.domain.section.Section;

import java.util.Objects;

public class ExamTimeCollision extends EnrollmentRuleViolation {
    private final Section section1;
    private final Section section2 ;

    public ExamTimeCollision(Section section1, Section section2) {
        this.section1 = section1;
        this.section2 = section2;
    }

    @Override
    public String toString() {
        return String.format("%s is not passed as a prerequisite of %s", section1, section2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamTimeCollision that = (ExamTimeCollision) o;
        return this.section1.equals(that.section1) && this.section2.equals(that.section2);
    }
}
