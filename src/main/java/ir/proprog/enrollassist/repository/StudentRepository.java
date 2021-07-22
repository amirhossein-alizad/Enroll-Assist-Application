package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.Student;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, Long> {
}
