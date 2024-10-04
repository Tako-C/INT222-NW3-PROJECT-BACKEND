package sit.int221.mytasksservice.models.primary;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users")
public class PrimaryUsers {
    @Id
    public String oid;
    public String name;
    private String username;
    public String email;
}
