package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.controller.EnrollmentListView;
import ir.proprog.enrollassist.domain.EnrollmentList;
import ir.proprog.enrollassist.domain.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StudentRepositoryTest {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EnrollmentListRepository enrollmentRepository;



    @Test
    public void Student_with_specific_student_number_is_returned_correctly() {
        Student mahsa = new Student("810199999", "Mahsa Mahsaei");
        studentRepository.save(mahsa);
        Optional<Student> res = studentRepository.findByStudentNumber("810199999");
        assertThat(res.get().getStudentNumber()).isEqualTo("810199999");
        assertThat(res.get().getName()).isEqualTo("Mahsa Mahsaei");
    }

    @Test
    public void No_student_is_returned_if_student_number_is_invalid() {
        Optional<Student> res = studentRepository.findByStudentNumber("810199999");
        assertThat(res).isEmpty();
    }

    @Test
    public void All_lists_for_student_with_specific_student_number_is_returned_correctly() {
        Student john = new Student("810100000", "John  Doe");
        studentRepository.save(john);
        EnrollmentList list1 = new EnrollmentList("1st list", john);
        EnrollmentList list2 = new EnrollmentList("2nd list", john);
        enrollmentRepository.save(list1);
        enrollmentRepository.save(list2);
        List<EnrollmentListView> res = studentRepository.findAllListsForStudent("810100000");
        assertThat(res).hasSize(2);
    }
}
