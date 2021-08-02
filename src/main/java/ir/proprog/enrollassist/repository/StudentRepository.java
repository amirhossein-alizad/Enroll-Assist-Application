package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, Long> {
    @Query(value = "select std from Student std where std.studentNumber=?1")
    Student findStudentByStudentNumber(String studentNo);
}
