package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Long> {
    Optional<Student> findByStudentNumber(String studentNumber);
}
