package sit.int221.mytasksservice.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.int221.mytasksservice.models.primary.Boards;

import java.util.List;

public interface BoardsRepository extends JpaRepository<Boards, String> {
    List<Boards> findByBoardIdIn(List<String> boardIds);
    @Query("SELECT b FROM Boards b LEFT JOIN FETCH b.tasks LEFT JOIN FETCH b.statuses WHERE b.visibility = 'Public'")
    List<Boards> findPublicBoards();

    @Query("SELECT b FROM Boards b LEFT JOIN FETCH b.tasks LEFT JOIN FETCH b.statuses WHERE b.oid = ?1 OR b.visibility = 'Public'")
    List<Boards> findByOidOrVisibility(String oid);
}
