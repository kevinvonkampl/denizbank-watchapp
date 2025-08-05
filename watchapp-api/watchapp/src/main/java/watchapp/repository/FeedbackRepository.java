package watchapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import watchapp.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {}
