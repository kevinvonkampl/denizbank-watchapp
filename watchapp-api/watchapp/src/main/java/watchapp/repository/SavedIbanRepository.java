package watchapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import watchapp.entity.SavedIban;

import java.util.List;

public interface SavedIbanRepository extends JpaRepository<SavedIban, Long> {
    List<SavedIban> findByUserId(Long userId);
}