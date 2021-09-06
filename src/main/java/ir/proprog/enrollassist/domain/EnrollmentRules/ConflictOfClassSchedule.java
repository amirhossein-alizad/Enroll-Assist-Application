package ir.proprog.enrollassist.domain.EnrollmentRules;

import ir.proprog.enrollassist.domain.section.Section;

public class ConflictOfClassSchedule extends EnrollmentRuleViolation{
    private Section section1;
    private Section section2;

    public ConflictOfClassSchedule(Section section1, Section section2) {
        this.section1 = section1;
        this.section2 = section2;
    }

    @Override
    public String toString() {
        return String.format("%s course and %s course have conflict in schedule.", this.section1, this.section2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConflictOfClassSchedule that = (ConflictOfClassSchedule) o;
        return this.section1.equals(that.section1) && this.section2.equals(that.section2);
    }
}
