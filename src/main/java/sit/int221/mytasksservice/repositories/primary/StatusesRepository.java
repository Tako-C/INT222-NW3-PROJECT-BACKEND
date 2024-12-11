package sit.int221.mytasksservice.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.mytasksservice.models.primary.Statuses;

import java.util.List;
import java.util.Optional;

public interface StatusesRepository extends JpaRepository<Statuses, Integer> {
    Optional<Statuses> findByStatusIdAndBoardsBoardId(Integer statusId , String boardId);
}
