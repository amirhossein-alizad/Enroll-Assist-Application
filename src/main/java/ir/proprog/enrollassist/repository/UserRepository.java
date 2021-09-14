package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.student.StudentNumber;
import ir.proprog.enrollassist.domain.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUserId(String userId);

    @Query(value = "select user from User user join user.students as student where (student.id = ?1)")
    Optional<User> findByStudentId(Long id);

}
