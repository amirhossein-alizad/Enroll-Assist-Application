package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.controller.enrollmentList.EnrollmentListView;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.student.StudentNumber;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Long> {
    Optional<Student> findByStudentNumber(StudentNumber studentNumber);

    @Query(value = "select new ir.proprog.enrollassist.controller.enrollmentList.EnrollmentListView(list) from EnrollmentList list, Student student where student.studentNumber.number=?1 and list.owner.studentNumber.number=?1")
    List<EnrollmentListView> findAllListsForStudent(String studentNo);
}
