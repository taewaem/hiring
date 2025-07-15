package gabia.hiring.domain.user.repository;

import gabia.hiring.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
