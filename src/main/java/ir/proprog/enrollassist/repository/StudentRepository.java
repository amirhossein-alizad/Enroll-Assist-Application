package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.controller.EnrollmentListView;
import ir.proprog.enrollassist.domain.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Long> {
    Optional<Student> findByStudentNumber(String studentNumber);

    @Query(value = "select new ir.proprog.enrollassist.controller.EnrollmentListView(list) from EnrollmentList list, Student student where student.studentNumber=?1 and list.owner.studentNumber=?1")
    List<EnrollmentListView> findAllListsForStudent(String studentNo);
}
