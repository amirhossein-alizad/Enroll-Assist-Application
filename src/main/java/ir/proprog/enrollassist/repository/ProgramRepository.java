package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.program.Program;
import org.springframework.data.repository.CrudRepository;

public interface ProgramRepository extends CrudRepository<Program, Long> {
}
