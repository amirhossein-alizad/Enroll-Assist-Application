package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.Course;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Long> {
    Optional<Course> findCourseByCourseNumber(String courseNumber);
}
