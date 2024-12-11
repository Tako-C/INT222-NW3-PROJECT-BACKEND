package sit.int221.mytasksservice.models.secondary;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Setter
@Getter
@Entity
@Table(name = "users")
public class Users {
    @Id
    public String oid;
    public String name;
    private String username;
    public String email;
    private String password;
    public String role;
    public Users(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Users() {}
}
