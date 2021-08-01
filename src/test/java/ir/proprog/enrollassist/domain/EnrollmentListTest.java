package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnrollmentListTest {
    @Test
    void Enrollment_list_cannot_have_courses_with_prerequisites_not_taken_by_owner() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course phys1 = new Course("2", "PHYS1", 3);
        Course prog = new Course("3", "PROG", 4);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        when(bebe.hasPassed(math1)).thenReturn(false);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, phys1_1, math2_1, prog_1);
        assertThat(list.checkHasPassedAllPrerequisites(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_returns_no_violation_when_all_courses_prerequisites_have_been_passed() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course phys1 = new Course("2", "PHYS1", 3);
        Course prog = new Course("3", "PROG", 4);
        Course ap = new Course("3", "AP", 3).withPre(prog);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section math2_1 = new Section(math2, "01");
        Section ap_1 = new Section(ap, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(prog)).thenReturn(true);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, phys1_1, math2_1, ap_1);
        assertThat(list.checkHasPassedAllPrerequisites(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_cannot_have_courses_already_been_passed_by_owner() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course phys1 = new Course("2", "PHYS1", 3);
        Course prog = new Course("3", "PROG", 4);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, phys1_1, math2_1, prog_1);
        assertThat(list.checkHasNotAlreadyPassedCourses(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_duplicate_courses() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course phys1 = new Course("2", "PHYS1", 3);
        Course prog = new Course("3", "PROG", 4);
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section prog_1 = new Section(prog, "01");
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, phys1_1, prog_1, math1_1);
        assertThat(list.checkNoCourseHasRequestedTwice())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_more_than_14_credits_belonging_to_student_with_gpa_less_than_12() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 4);
        Course eco = new Course("3", "ECO", 3);
        Course ap = new Course("4", "AP", 3);
        Course ds = new Course("6", "DS", 3);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        Section ds_1 = new Section(ds, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(11.5F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, eco_1, ap_1, ds_1);
        assertThat(list.checkValidGPALimit(bebe))
                .isNotNull();
    }

    @Test
    void Enrollment_list_cannot_have_more_than_20_credits_belonging_to_student_with_gpa_less_than_17() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 4);
        Course eco = new Course("3", "ECO", 6);
        Course ap = new Course("4", "AP", 9);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(16F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, eco_1, ap_1);
        assertThat(list.checkValidGPALimit(bebe))
                .isNotNull();
    }

    @Test
    void Nobody_can_take_more_than_24_credits() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 8);
        Course eco = new Course("3", "ECO", 12);
        Course math2 = new Course("5", "MATH2", 6).withPre(math1);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(20F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, eco_1);
        assertThat(list.checkValidGPALimit(bebe))
                .isNotNull();
    }

    @Test
    void Enrollment_list_has_no_violations() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        bebe.setGrade("1", math1, 11);
        Course prog = new Course("2", "PROG", 4);
        Course eco = new Course("3", "ECO", 3);
        Course ap = new Course("4", "AP", 3);
        Course ds = new Course("6", "DS", 3);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        Section ds_1 = new Section(ds, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(15F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, eco_1, ap_1, ds_1);
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_cannot_have_duplicate_courses_and_more_than_24_credits() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 4);
        Course eco = new Course("3", "ECO", 3);
        Course ap = new Course("4", "AP", 3);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Course ds = new Course("6", "DS", 3);
        Course da = new Course("7", "DS", 3);
        Course dm = new Course("8", "DM", 3);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        Section ds_1 = new Section(ds, "01");
        Section da_1 = new Section(da, "01");
        Section dm_1 = new Section(dm, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(20F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, eco_1, ap_1, ds_1, da_1, dm_1, dm_1);
        ArrayList<EnrollmentRuleViolation> violations = (ArrayList<EnrollmentRuleViolation>) list.checkEnrollmentRules();
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(2);
        assertThat(violations.get(0).getClass())
                .isEqualTo(CourseRequestedTwice.class);
        assertThat(violations.get(1).getClass())
                .isEqualTo(MaxCreditsLimitExceeded.class);
    }

    @Test
    void Students_cannot_take_courses_without_their_prerequisites_taken_and_more_than_14_credits_with_gpa_less_than_12() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 4);
        Course eco = new Course("3", "ECO", 3);
        Course ap = new Course("4", "AP", 3);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Course ds = new Course("6", "DS", 3);
        Course da = new Course("7", "DS", 3);
        Course dm = new Course("8", "DM", 3);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        Section ds_1 = new Section(ds, "01");
        Section da_1 = new Section(da, "01");
        Section dm_1 = new Section(dm, "01");
        when(bebe.hasPassed(math1)).thenReturn(false);
        when(bebe.calculateGPA()).thenReturn(11F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, eco_1, ap_1, ds_1, da_1, dm_1);
        ArrayList<EnrollmentRuleViolation> violations = (ArrayList<EnrollmentRuleViolation>) list.checkEnrollmentRules();
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(2);
        assertThat(violations.get(0).getClass())
                .isEqualTo(PrerequisiteNotTaken.class);
        assertThat(violations.get(1).getClass())
                .isEqualTo(MaxCreditsLimitExceeded.class);
    }

    @Test
    void Enrollment_list_cannot_have_duplicate_courses_and_their_prerequisites_not_taken() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course eco = new Course("3", "ECO", 3);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Section math2_1 = new Section(math2, "01");
        Section eco_1 = new Section(eco, "01");
        when(bebe.hasPassed(math1)).thenReturn(false);
        when(bebe.calculateGPA()).thenReturn(11F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, eco_1, eco_1);
        ArrayList<EnrollmentRuleViolation> violations = (ArrayList<EnrollmentRuleViolation>) list.checkEnrollmentRules();
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(2);
        assertThat(violations.get(0).getClass())
                .isEqualTo(PrerequisiteNotTaken.class);
        assertThat(violations.get(1).getClass())
                .isEqualTo(CourseRequestedTwice.class);
    }

    void New_Students_Can_Take_More_Than_Twelve_Credits(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course economy = new Course("1", "ECO", 3);
        Course maaref = new Course("5", "MAAREF", 2);
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        when(bebe.hasPassed(any(Course.class))).thenReturn(false);
        list1.addSections(prog1_1, math1_1, phys1_1, economy1_1, maaref1_1);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void New_Students_Cant_Take_More_Than_Twenty_Credits(){
        Student bebe = mock(Student.class);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course maaref = new Course("5", "MAAREF", 2);
        Course dm = new Course("3", "DM", 3);
        Course economy = new Course("1", "ECO", 3);
        Course english = new Course("10", "EN", 2);
        Course akhlagh = new Course("11", "AKHLAGH", 2);
        Course karafarini = new Course("13", "KAR", 3);
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section dm_1 = new Section(dm, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        Section akhlagh_1 = new Section(akhlagh, "01");
        Section english_1 = new Section(english, "01");
        Section karafarini_1 = new Section(karafarini, "01");
        when(bebe.calculateGPA()).thenReturn(0.0F);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, dm_1, maaref1_1, economy1_1, akhlagh_1, english_1, karafarini_1);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Students_Taking_Less_Than_Twelve_Credits_is_not_a_Violation() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        when(bebe.hasPassed(any(Course.class))).thenReturn(false);
        list1.addSections(prog1_1, math1_1, phys1_1);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Students_Can_Take_More_Than_Twelve_Credits() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course economy = new Course("1", "ECO", 3);
        Course maaref = new Course("5", "MAAREF", 2);
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        when(bebe.hasPassed(any(Course.class))).thenReturn(false);
        when(bebe.calculateGPA()).thenReturn(15.0F);
        list1.addSections(prog1_1, math1_1, phys1_1, economy1_1, maaref1_1);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Students_With_GPA_Less_Than_Twelve_Taking_More_Than_Fourteen_Credits_is_a_Violation(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course maaref = new Course("5", "MAAREF", 2);
        Course dm = new Course("3", "DM", 3).withPre(math1);
        Course economy = new Course("1", "ECO", 3);
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section dm_1 = new Section(dm, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.calculateGPA()).thenReturn(11.99F);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, dm_1, maaref1_1, economy1_1);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Students_With_GPA_More_Than_Twelve_Can_Take_More_Than_Fourteen_Credits(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course maaref = new Course("5", "MAAREF", 2);
        Course dm = new Course("3", "DM", 3).withPre(math1);
        Course economy = new Course("1", "ECO", 3);
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section dm_1 = new Section(dm, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(12.0F);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, dm_1, maaref1_1, economy1_1);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Students_With_GPA_Less_Than_Seventeen_Taking_More_Than_Twenty_Credits_is_a_Violation(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course maaref = new Course("5", "MAAREF", 2);
        Course dm = new Course("3", "DM", 3).withPre(math1);
        Course economy = new Course("1", "ECO", 3);
        Course english = new Course("10", "EN", 2);
        Course akhlagh = new Course("11", "AKHLAGH", 2);
        Course karafarini = new Course("13", "KAR", 3);
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section dm_1 = new Section(dm, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        Section akhlagh_1 = new Section(akhlagh, "01");
        Section english_1 = new Section(english, "01");
        Section karafarini_1 = new Section(karafarini, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.calculateGPA()).thenReturn(16.99F);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, dm_1, maaref1_1, economy1_1, akhlagh_1, english_1, karafarini_1);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Students_With_GPA_More_Than_Seventeen_Can_Take_More_Than_Twenty_Credits(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course maaref = new Course("5", "MAAREF", 2);
        Course dm = new Course("3", "DM", 3).withPre(math1);
        Course economy = new Course("1", "ECO", 3);
        Course english = new Course("10", "EN", 2);
        Course akhlagh = new Course("11", "AKHLAGH", 2);
        Course karafarini = new Course("13", "KAR", 3);
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section dm_1 = new Section(dm, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        Section akhlagh_1 = new Section(akhlagh, "01");
        Section english_1 = new Section(english, "01");
        Section karafarini_1 = new Section(karafarini, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.calculateGPA()).thenReturn(17.01F);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, dm_1, maaref1_1, economy1_1, akhlagh_1, english_1, karafarini_1);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Students_With_GPA_More_Than_Seventeen_Taking_more_Than_TwentyFour_Credits_is_a_Violation(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course maaref = new Course("5", "MAAREF", 2);
        Course dm = new Course("3", "DM", 3);
        Course economy = new Course("1", "ECO", 3);
        Course english = new Course("10", "EN", 2);
        Course akhlagh = new Course("11", "AKHLAGH", 2);
        Course karafarini = new Course("13", "KAR", 3);
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section dm_1 = new Section(dm, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        Section akhlagh_1 = new Section(akhlagh, "01");
        Section english_1 = new Section(english, "01");
        Section karafarini_1 = new Section(karafarini, "01");
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.calculateGPA()).thenReturn(17.01F);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys1_1, prog1_1, dm_1, maaref1_1, economy1_1, akhlagh_1, english_1, karafarini_1);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Requesting_Courses_That_Has_Been_Requested_Before_is_a_Violation(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course maaref = new Course("5", "MAAREF", 2);
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section maaref1_1 = new Section(maaref, "01");
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys1_1, math1_1, maaref1_1, math1_1, phys1_1);
        when(bebe.calculateGPA()).thenReturn(15F);
        when(bebe.hasPassed(any(Course.class))).thenReturn(false);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .hasSize(3);
    }

    @Test
    void Requesting_Courses_Which_You_Have_Already_Passed_is_a_Violation(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course maaref = new Course("5", "MAAREF", 2);
        Course economy = new Course("1", "ECO", 3);
        Course akhlagh = new Course("11", "AKHLAGH", 2);
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        Section akhlagh_1 = new Section(akhlagh, "01");
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys1_1, prog1_1, maaref1_1, economy1_1, akhlagh_1);
        when(bebe.calculateGPA()).thenReturn(15.0F);
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(phys1)).thenReturn(true);
        when(bebe.hasPassed(maaref)).thenReturn(true);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .hasSize(3);
    }

    @Test
    void Requesting_Courses_With_Two_Prerequisites_Which_None_Has_Been_Passed_Violates_Two_Rules(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course maaref = new Course("5", "MAAREF", 2);
        Course economy = new Course("1", "ECO", 3);
        Course akhlagh = new Course("11", "AKHLAGH", 2);
        Course phys2 = new Course("9", "PHYS2", 3).withPre(math1, phys1);
        Section prog1_1 = new Section(prog, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        Section akhlagh_1 = new Section(akhlagh, "01");
        Section phys2_1 = new Section(phys2, "01");
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(prog1_1, maaref1_1, economy1_1, akhlagh_1, phys2_1);
        when(bebe.calculateGPA()).thenReturn(15F);
        when(bebe.hasPassed(math1)).thenReturn(false);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void Requesting_Courses_With_2_Prerequisites_Which_One_Has_Been_Passed_is_a_Violation(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course maaref = new Course("5", "MAAREF", 2);
        Course economy = new Course("1", "ECO", 3);
        Course akhlagh = new Course("11", "AKHLAGH", 2);
        Course phys2 = new Course("9", "PHYS2", 3).withPre(math1, phys1);
        Section prog1_1 = new Section(prog, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        Section akhlagh_1 = new Section(akhlagh, "01");
        Section phys2_1 = new Section(phys2, "01");
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(prog1_1, maaref1_1, economy1_1, akhlagh_1, phys2_1);
        when(bebe.calculateGPA()).thenReturn(15F);
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .hasSize(1);
    }
    @Test
    void Requesting_a_Course_Which_is_Already_Passed_and_A_Course_With_None_of_its_Prerequisites_Passed_Violates_Two_Rule(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course maaref = new Course("5", "MAAREF", 2);
        Course economy = new Course("1", "ECO", 3);
        Course akhlagh = new Course("11", "AKHLAGH", 2);
        Course phys2 = new Course("9", "PHYS2", 3).withPre(math1, phys1);
        Section prog1_1 = new Section(prog, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        Section akhlagh_1 = new Section(akhlagh, "01");
        Section phys2_1 = new Section(phys2, "01");
        Section phys1_1 = new Section(phys1, "01");
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, maaref1_1, economy1_1, akhlagh_1, phys2_1);
        when(bebe.calculateGPA()).thenReturn(15F);
        when(bebe.hasPassed(math1)).thenReturn(false);
        when(bebe.hasPassed(phys1)).thenReturn(true);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .hasSize(2);
    }

}
