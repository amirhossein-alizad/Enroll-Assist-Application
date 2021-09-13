package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.user.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
