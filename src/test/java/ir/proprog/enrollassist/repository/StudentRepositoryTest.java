package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
public class StudentRepositoryTest {
    @Autowired
    private StudentRepository studentRepository;

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
        System.out.println(res);
        assertThat(res).isEmpty();
    }
}
