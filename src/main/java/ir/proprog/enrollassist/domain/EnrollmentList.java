package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.domain.EnrollmentRules.*;
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

    public String isValid()
    {
        if (listName.equals(""))
            return "Enrollment list must have a name";
        return null;
    }

    public EnrollmentList(@NonNull String listName, @NonNull Student owner) {
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
        violations.addAll(checkValidGPALimit());
        violations.addAll(checkExamTimeConflicts());
        violations.addAll(checkSectionScheduleConflicts());
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
        for (Section sec : sections)
            if (sec.studentHasPassedCourse(s))
                violations.add(new RequestedCourseAlreadyPassed(sec.getCourse()));
        return violations;
    }

    List<EnrollmentRuleViolation> checkHasPassedAllPrerequisites(Student s) {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        for (Section sec : sections)
            violations.addAll(sec.courseCanBeTakenBy(s));
        return violations;
    }

    List<EnrollmentRuleViolation> checkValidGPALimit() {
        double GPA = owner.calculateGPA();
        int credits = sections.stream().mapToInt(section -> section.getCourse().getCredits()).sum();
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        if(credits < 12)
            violations.add(new MinCreditsRequiredNotMet(12));
        if(GPA == 0 && owner.getTotalTakenCredits() == 0){
            if (credits > 20)
                violations.add(new MaxCreditsLimitExceeded(20));
        }
        else if(owner.getTotalTakenCredits() > 0) {
            if (credits > 14 && GPA < 12)
                violations.add(new MaxCreditsLimitExceeded(14));
            else if (credits > 20 && GPA < 17)
                violations.add(new MaxCreditsLimitExceeded(20));
            else if (credits > 24 && GPA >= 17)
                violations.add(new MaxCreditsLimitExceeded(24));
        }
        return violations;
    }

    List<EnrollmentRuleViolation> checkExamTimeConflicts() {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        for (int i = 0; i < sections.size(); i++) {
            Section s1 = sections.get(i);
            if (!s1.getCourse().getHasExam())
                continue;
            for (int j = i + 1; j < sections.size(); j++) {
                Section s2 = sections.get(j);
                if (!s2.getCourse().getHasExam())
                    continue;
                if (s1 != s2 && s1.getExamTime().hasTimeConflict(s2.getExamTime()))
                    violations.add(new ExamTimeCollision(s1, s2));
            }
        }
        return violations;
    }

    List<EnrollmentRuleViolation> checkSectionScheduleConflicts() {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        for (int i=0; i<this.sections.size(); i++) {
            for (int j=i+1; j<this.sections.size(); j++) {
                if (this.sections.get(i).equals(this.sections.get(j)))
                    continue;
                if (this.sections.get(i).hasConflict(this.sections.get(j)))
                    violations.add(new ConflictOfClassSchedule(this.sections.get(i).getCourse(), this.sections.get(j).getCourse()));
            }
        }
        return violations;
    }

}
