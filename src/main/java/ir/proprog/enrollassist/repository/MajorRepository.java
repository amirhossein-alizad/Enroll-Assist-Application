package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.major.Major;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface MajorRepository extends CrudRepository<Major, Long> {
    Optional<Major> findByMajorName(String majorName);
}
