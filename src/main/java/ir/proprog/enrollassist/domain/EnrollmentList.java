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

    public Boolean isValid()
    {
        if (listName.equals(""))
            return false;
        return true;
    }

    public EnrollmentList(@NonNull String listName, @NonNull Student owner) {
        if (listName.equals(""))
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

    public void removeSection(Section section) {
        sections.remove(section);
    }

    public void clear() { sections.clear(); }

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
        violations.addAll(checkValidGPALimit(owner));
        return violations;
    }

    List<EnrollmentRuleViolation> checkNoCourseHasRequestedTwice() {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        Set<Course> Courses = new HashSet<>();
        for (Section section : sections)
            if (Courses.contains(section.getCourse()))
                violations.add(new CourseRequestedTwice(section.getCourse()));
            else
                Courses.add(section.getCourse());

        return violations;
    }


    List<EnrollmentRuleViolation> checkHasNotAlreadyPassedCourses(Student s) {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        for (Section o : sections)
            if (s.hasPassed(o.getCourse()))
                violations.add(new RequestedCourseAlreadyPassed(o.getCourse()));
        return violations;
    }

    List<EnrollmentRuleViolation> checkHasPassedAllPrerequisites(Student s) {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        for (Section o : sections)
            violations.addAll(o.getCourse().canBeTakenBy(s));
        return violations;
    }

    List<EnrollmentRuleViolation> checkValidGPALimit(Student s) {
        double GPA = s.calculateGPA();
        int credits = sections.stream().mapToInt(section -> section.getCourse().getCredits()).sum();
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        if(s.calculateGPA() == 0 && s.getTotalTakenCredits() == 0){
            if (credits > 20)
                violations.add(new MaxCreditsLimitExceeded(20));
        }
        else if(s.getTotalTakenCredits() > 0) {
            if (credits > 14 && GPA < 12)
                violations.add(new MaxCreditsLimitExceeded(14));
            else if (credits > 20 && GPA < 17)
                violations.add(new MaxCreditsLimitExceeded(20));
            else if (credits > 24 && GPA >= 17)
                violations.add(new MaxCreditsLimitExceeded(24));
        }
        return violations;
    }

}
