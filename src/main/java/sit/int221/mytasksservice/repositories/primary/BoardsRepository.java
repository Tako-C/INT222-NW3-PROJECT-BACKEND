package sit.int221.mytasksservice.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.mytasksservice.models.primary.Boards;

public interface BoardsRepository extends JpaRepository<Boards, String> {
  List<Boards> findBoardsByOid(String oid);
}
