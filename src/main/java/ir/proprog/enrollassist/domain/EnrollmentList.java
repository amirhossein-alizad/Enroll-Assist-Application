package ir.proprog.enrollassist.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // as required by JPA, don't use it in your code
@Getter
public class EnrollmentList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String listName;
    @ManyToOne
    Student owner;
    @ManyToMany
    List<Section> sections = new ArrayList<>();

    public EnrollmentList(@NonNull String listName, @NonNull Student owner) {
        if (listName == "")
            throw new IllegalArgumentException("Enrollment list must have a name");
        this.listName = listName;
        this.owner = owner;
    }

    public void addSections(Section... new_sections) {
        //TODO: check for duplicates
        Collections.addAll(sections, new_sections);
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrollmentList that = (EnrollmentList) o;
        return owner.equals(that.owner) && listName.equals(that.listName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, listName);
    }

    public List<EnrollmentRuleViolation> checkEnrollmentRules() {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        violations.addAll(checkHasPassedAllPrerequisites(owner));
        violations.addAll(checkHasNotAlreadyPassedCourses(owner));
        violations.addAll(checkNoCourseHasRequestedTwice());
        return violations;
    }

    private List<EnrollmentRuleViolation> checkNoCourseHasRequestedTwice() {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        for (int i = 0; i < sections.size(); i++)
            for (int j = i + 1; j < sections.size(); j++)
                if (sections.get(i).getCourse().equals(sections.get(j).getCourse()))
                    violations.add(new CourseRequestedTwice(sections.get(i), sections.get(j)));
        return violations;
    }

    private List<EnrollmentRuleViolation> checkHasNotAlreadyPassedCourses(Student s) {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        for (Section o : sections)
            if (s.hasPassed(o.getCourse()))
                violations.add(new RequestedCourseAlreadyPassed(o.getCourse()));
        return violations;
    }

    private List<EnrollmentRuleViolation> checkHasPassedAllPrerequisites(Student s) {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        for (Section o : sections)
            violations.addAll(o.getCourse().canBeTakenBy(s));
        return violations;
    }

    private List<EnrollmentRuleViolation> checkValidGPALimit(Student s) {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        float gpa = s.calculateGPA();
        int credits = sections.stream().mapToInt(e -> e.getCourse().getCredits()).sum();
        if (credits < 12)
            violations.add(new MinCreditLimitNotMet());
        else if (gpa < 12 && credits > 14)
            violations.add(new MaxCreditLimitExceeded(14));
        else if (gpa < 17 && credits > 20)
            violations.add(new MaxCreditLimitExceeded(20));
        else
            violations.add(new MaxCreditLimitExceeded(24));
        return violations;
    }
}
