package sit.int221.mytasksservice.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.int221.mytasksservice.models.primary.CollabBoard;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollabBoardRepository extends JpaRepository<CollabBoard, Integer> {
    List<CollabBoard> findByBoardsId(String boardsId);
    Optional<CollabBoard> findCollabByOidAndBoardsId(String collab_oid, String boardsId);
    boolean existsByOidAndBoardsId(String oid, String boardId);
    @Query("SELECT c.boardsId FROM CollabBoard c WHERE c.oid = :oid")
    List<String> findBoardsIdByOid(@Param("oid") String oid);
}
