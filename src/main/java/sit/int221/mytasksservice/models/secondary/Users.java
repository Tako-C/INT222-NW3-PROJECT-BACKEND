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

    public Users() {

    }
    public Users(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
    }

    // Add getter and setter methods for authorities if needed
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Implement this if needed
        return null; // Modify as needed
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        // Implement this if needed
    }
}
