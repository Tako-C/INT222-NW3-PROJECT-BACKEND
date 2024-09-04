package sit.int221.mytasksservice.repositories.secondary;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.mytasksservice.models.secondary.Users;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, String>{
    Users findByUsername(String username);
    Users findByOid(String oid);
}
