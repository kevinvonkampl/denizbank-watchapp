package watchapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import watchapp.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {}