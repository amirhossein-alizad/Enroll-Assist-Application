package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Long> {
}
