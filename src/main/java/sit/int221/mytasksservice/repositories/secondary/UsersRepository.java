package sit.int221.mytasksservice.repositories.secondary;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.mytasksservice.models.secondary.Users;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, String>{
    Optional<Users> findByUsername(String username);
    Users findByOid(String oid);
    Optional<Users> findByEmail(String email);
}
