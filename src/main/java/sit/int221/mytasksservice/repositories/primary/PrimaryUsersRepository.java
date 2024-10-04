package sit.int221.mytasksservice.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.mytasksservice.models.primary.PrimaryUsers;

public interface PrimaryUsersRepository extends JpaRepository<PrimaryUsers, String> {
    boolean existsByOid(String oid);
}
