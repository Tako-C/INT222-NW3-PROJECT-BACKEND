package sit.int221.mytasksservice.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.mytasksservice.models.primary.Kradanboard;

import java.util.List;

public interface KradanboardRepository extends JpaRepository<Kradanboard, Integer> {
    List<Kradanboard> findByStatusId(Integer statusId);
}
