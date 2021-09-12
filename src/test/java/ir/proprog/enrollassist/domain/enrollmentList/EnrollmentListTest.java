package ir.proprog.enrollassist.domain.enrollmentList;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.EnrollmentRules.CourseRequestedTwice;
import ir.proprog.enrollassist.domain.EnrollmentRules.EnrollmentRuleViolation;
import ir.proprog.enrollassist.domain.EnrollmentRules.MaxCreditsLimitExceeded;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.studyRecord.Grade;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.section.ExamTime;
import ir.proprog.enrollassist.domain.section.PresentationSchedule;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.utils.TestCourseBuilder;
import ir.proprog.enrollassist.domain.utils.TestPresentationScheduleBuilder;
import ir.proprog.enrollassist.domain.utils.TestSectionBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnrollmentListTest {
    private TestCourseBuilder testCourseBuilder = new TestCourseBuilder();
    private TestPresentationScheduleBuilder testPresentationScheduleBuilder = new TestPresentationScheduleBuilder();
    private TestSectionBuilder testSectionBuilder = new TestSectionBuilder();
    private Course math1, prog, ap , ds, phys1, maaref, english, phys2;
    private Section math1_1, math1_2, prog_1, ap_1, ds_1, phys1_1, maaref1_1, english_1, phys2_1;
    private Student bebe;
    private PresentationSchedule schedule1, schedule2, schedule3;
    ExamTime examTime1, examTime2, examTime3;

    @BeforeEach
    void setUp() throws Exception {
        bebe = mock(Student.class);
        setUpCourse();
        setUpSection();
        setUpSchedule();
        setUpExamTime();
    }

    void setUpCourse() throws Exception{
        math1 = testCourseBuilder
                .courseNumber("1111111")
                .build();
        prog = testCourseBuilder
                .courseNumber("1111112")
                .credits(4)
                .build();
        ap = testCourseBuilder
                .courseNumber("1111113")
                .credits(3)
                .build()
                .withPre(prog);
        ds = testCourseBuilder
                .courseNumber("1111116")
                .build();
        phys1 = testCourseBuilder
                .courseNumber("1111117")
                .credits(4)
                .build();
        maaref = testCourseBuilder
                .courseNumber("1111118")
                .build();
        english = testCourseBuilder
                .courseNumber("1111119")
                .build();
        phys2 = testCourseBuilder
                .courseNumber("1111120")
                .build()
                .withPre(phys1, math1);
    }

    void setUpSection() throws Exception{
        prog_1 = testSectionBuilder
                .course(prog)
                .build();
        maaref1_1 = testSectionBuilder
                .course(maaref)
                .build();
        english_1 = testSectionBuilder
                .course(english)
                .build();
        math1_1 = testSectionBuilder
                .course(math1)
                .build();
        math1_2 = testSectionBuilder
                .course(math1)
                .sectionNumber("02")
                .build();
        phys1_1 = testSectionBuilder
                .course(phys1)
                .sectionNumber("01")
                .build();
        phys2_1 = testSectionBuilder
                .course(phys2)
                .build();
        ds_1 = testSectionBuilder
                .course(ds)
                .build();
        ap_1 = testSectionBuilder
                .course(ap)
                .build();
    }

    void setUpSchedule() throws Exception{
        schedule1 = testPresentationScheduleBuilder
                .build();
        schedule2 = testPresentationScheduleBuilder
                .dayOfWeek("Wednesday")
                .build();
        schedule3 = testPresentationScheduleBuilder
                .dayOfWeek("Monday")
                .startTime("11:30")
                .endTime("13:00")
                .build();
    }

    void setUpExamTime() throws Exception{
        examTime1 = new ExamTime("2021-06-21T11:00", "2021-06-21T13:00");
        examTime2 = new ExamTime("2021-06-21T13:00", "2021-06-21T16:00");
        examTime3 = new ExamTime("2021-06-21T12:00", "2021-06-21T14:00");
    }

    @Test
    void Enrollment_list_returns_no_violation_when_all_courses_prerequisites_have_been_passed() throws ExceptionList {
        Course math2 = new Course("4444444", "MATH2", 3, "Undergraduate").withPre(math1);
        Section math2_1 = new Section(math2, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(prog)).thenReturn(true);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, ap_1);
        assertThat(list.checkHasPassedAllPrerequisites())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_cannot_have_courses_already_been_passed_by_owner() {
        when(bebe.hasPassed(math1)).thenReturn(true);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1);
        assertThat(list.checkHasNotAlreadyPassedCourses())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_duplicate_courses() {
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, math1_2);
        assertThat(list.checkNoCourseHasRequestedTwice())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_more_than_14_credits_belonging_to_student_with_gpa_less_than_12() throws Exception {
        when(bebe.calculateGPA()).thenReturn(new Grade(11.5));
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, english_1, ap_1, ds_1);
        assertThat(list.checkValidGPALimit())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_more_than_20_credits_belonging_to_student_with_gpa_less_than_17() throws Exception {
        when(bebe.calculateGPA()).thenReturn(new Grade(16));
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, english_1, ap_1, phys1_1, maaref1_1);
        assertThat(list.checkValidGPALimit())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Nobody_can_take_more_than_24_credits() throws Exception {
        when(bebe.calculateGPA()).thenReturn(new Grade(20));
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, phys2_1, phys1_1, maaref1_1, ap_1, english_1);
        assertThat(list.checkValidGPALimit())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_duplicate_courses_and_more_than_24_credits() throws Exception {
        Course eco = new Course("5555555", "ECO", 3, "Undergraduate");
        Section eco_1 = new Section(eco, "01");
        when(bebe.calculateGPA()).thenReturn(new Grade(20));
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, phys1_1, maaref1_1, eco_1, ds_1, english_1, ds_1);
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
    void New_students_cant_take_more_than_twenty_credits() {
        when(bebe.calculateGPA()).thenReturn(new Grade());
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog_1, ds_1, math1_2, maaref1_1, english_1);
        assertThat(list1.checkValidGPALimit())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Students_with_GPA_more_than_12_can_take_more_than_14_credits() throws Exception {
        when(bebe.calculateGPA()).thenReturn(new Grade(12.0));
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog_1, math1_1, ap_1);
        assertThat(list1.checkValidGPALimit())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Students_with_GPA_more_than_17_can_take_more_than_20_credits() throws Exception {
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.calculateGPA()).thenReturn(new Grade(17.01));
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog_1, maaref1_1, english_1, math1_2);
        assertThat(list1.checkValidGPALimit())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Requesting_courses_with_two_prerequisites_where_none_has_been_passed_violates_two_rules() throws Exception {
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys2_1);
        when(bebe.calculateGPA()).thenReturn(new Grade(15));
        when(bebe.hasPassed(math1)).thenReturn(false);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(list1.checkHasPassedAllPrerequisites())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void Requesting_courses_with_2_prerequisites_when_one_has_been_passed_is_a_violation() throws Exception {
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys2_1);
        when(bebe.calculateGPA()).thenReturn(new Grade(15));
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(list1.checkHasPassedAllPrerequisites())
                .isNotNull()
                .hasSize(1);
    }


    @Test
    void Enrollment_list_cannot_have_sections_on_the_same_day_and_conflicting_exam_time() throws Exception {
        math1_1.setExamTime(examTime1);
        phys1_1.setExamTime(examTime3);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys1_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_may_have_sections_intersecting_at_one_exam_time() throws Exception {
        math1_1.setExamTime(examTime1);
        phys1_1.setExamTime(examTime2);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys1_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_cannot_have_one_section_when_its_exam_time_is_colliding_with_two_other_sections() throws Exception {
        math1_1.setExamTime(examTime1);
        phys1_1.setExamTime(examTime2);
        prog_1.setExamTime(examTime3);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, prog_1, phys1_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void Students_cant_take_less_than_twelve_credits() throws Exception {
        EnrollmentList list1 = new EnrollmentList("list", bebe);
        list1.addSections(new Section(math1, "01"));
        when(bebe.calculateGPA()).thenReturn(new Grade(16));
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        when(bebe.getTotalTakenCredits()).thenReturn(18);
        assertThat(list1.checkValidGPALimit())
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_conflict_in_section_schedules() {
        math1_1.setPresentationSchedule(Set.of(schedule1, schedule2));
        phys1_1.setPresentationSchedule(Set.of(schedule3));
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys1_1);
        assertThat(list1.checkSectionScheduleConflicts())
                .isNotNull()
                .hasSize(1);
    }


    @Test
    void Number_of_new_conflicts_are_zero_when_examTime_set_to_time_that_has_no_conflicts_with_others() throws Exception {
        prog_1.setExamTime(examTime1);
        math1_1.setExamTime(examTime3);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(prog_1, math1_1);
        assertThat(list.makeExamTimeConflict(math1_1, examTime2))
                .isEqualTo(false);
    }

    @Test
    void Number_of_new_conflicts_are_not_zero_when_examTime_set_to_time_that_has_conflicts_with_others() throws Exception {
        prog_1.setExamTime(examTime1);
        math1_1.setExamTime(examTime2);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(prog_1, math1_1);
        assertThat(list.makeExamTimeConflict(prog_1, examTime2))
                .isEqualTo(true);
    }

    @Test
    void Number_of_new_conflicts_are_not_zero_when_Schedule_set_to_time_that_has_conflicts_with_others_1() {
        prog_1.setPresentationSchedule(Set.of(schedule1));
        math1_1.setPresentationSchedule(Set.of(schedule2));
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(prog_1, math1_1);
        assertThat(list.makePresentationScheduleConflict(math1_1, List.of(schedule3)))
                .isEqualTo(true);
    }

    @Test
    void Number_of_new_conflicts_are_zero_when_Schedule_set_to_time_that_has_no_conflicts_with_others() {
        prog_1.setPresentationSchedule(Set.of(schedule1));
        math1_1.setPresentationSchedule(Set.of(schedule2));
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(prog_1, math1_1);
        assertThat(list.makePresentationScheduleConflict(prog_1, List.of(schedule3)))
                .isEqualTo(false);
    }
}

