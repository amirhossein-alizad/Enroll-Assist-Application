package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class StudentTest {

    @Test
    void a_Study_Record_With_Grade_Less_Than_Ten_is_Not_Passed(){
        Student bebe = new Student("810197546", "bebe");
        Course math1 = new Course("4", "MATH1", 3);
        bebe.setGrade("01", math1, 9.99);
        assertThat(bebe.hasPassed(math1))
                .isFalse();
    }

    @Test
    void a_Study_Record_With_Grade_Less_Than_Ten_is_Not_Passed_But_Calculated_in_GPA(){
        Student bebe = new Student("810197546", "bebe");
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course economy = new Course("1", "ECO", 3);
        Course maaref = new Course("5", "MAAREF", 2);
        bebe.setGrade("t1", math1, 15.5);
        bebe.setGrade("t1", phys1, 9);
        bebe.setGrade("t1", prog, 17.25);
        bebe.setGrade("t1", economy, 19.5);
        bebe.setGrade("t1", maaref, 16);
        assertThat(bebe.calculateGPA())
                .isEqualTo(15.53F);
    }

    @Test
    void Student_has_not_passed_records_that_are_not_in_grades_set() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        assertThat(bebe.hasPassed(math1))
                .isEqualTo(false);
    }

    @Test
    void Student_has_passed_records_that_are_in_grades_set() {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1", "MATH1", 3);
        bebe.setGrade("3900", math1, 19);
        assertThat(bebe.hasPassed(math1))
                .isEqualTo(true);
    }

    @Test
    void Student_gpa_with_one_study_record_is_returned_correctly() {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1", "MATH1", 3);
        bebe.setGrade("3900", math1, 19);
        assertThat(bebe.calculateGPA())
                .isEqualTo(19);
    }

    @Test
    void Student_gpa_with_multiple_study_records_is_returned_correctly() {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 3);
        Course andishe = new Course("3", "ANDISHE", 2);
        bebe.setGrade("3900", math1, 19);
        bebe.setGrade("3900", prog, 17);
        bebe.setGrade("3900", andishe, 19);
        assertThat(bebe.calculateGPA())
                .isEqualTo(18.25F);
    }

}
