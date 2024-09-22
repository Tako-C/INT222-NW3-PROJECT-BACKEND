package sit.int221.mytasksservice.repositories.primary;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.mytasksservice.models.primary.Statuses;
import sit.int221.mytasksservice.models.primary.Tasks;

import java.util.List;
import java.util.Optional;


public interface TasksRepository extends JpaRepository<Tasks, Integer> {
    List<Tasks> findByBoardsBoardId(String boardId,Sort sort);
    Optional<Tasks> findByIdAndBoardsBoardId(Integer id , String boardId);
    List<Tasks> findByStatus_StatusIdAndBoards_BoardId(Integer StatusId, String boardId);

}
