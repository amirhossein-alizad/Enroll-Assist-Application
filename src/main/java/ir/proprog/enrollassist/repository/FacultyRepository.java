package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.Faculty;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FacultyRepository extends CrudRepository<Faculty, Long> {
    Optional<Faculty> findByFacultyName(String name);
}
