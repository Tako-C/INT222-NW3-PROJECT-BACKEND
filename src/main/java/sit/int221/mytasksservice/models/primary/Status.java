package sit.int221.mytasksservice.models.primary;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="statuss")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Size(min=1, max=50)
    private String name;
    @Size(min=1, max=200)
    private String description;
}
