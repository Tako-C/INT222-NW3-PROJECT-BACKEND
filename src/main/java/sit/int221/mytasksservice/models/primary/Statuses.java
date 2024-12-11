package sit.int221.mytasksservice.models.primary;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "statuses")
public class Statuses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;
    @Size(min=1, max=50)
    private String name;
    @Size(min=1, max=200)
    private String description;
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Boards boards;
}
