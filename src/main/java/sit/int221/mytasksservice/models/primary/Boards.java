package sit.int221.mytasksservice.models.primary;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "boards")
public class Boards {
    @Id
    @Column(name = "board_id")
    private String boardId;
    private String oid;
    private String board_name;

    //    @OneToMany(mappedBy = "boards", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "boards",fetch = FetchType.EAGER)
    private Set<Statuses> statuses;

    @OneToMany(mappedBy = "boards")
    private Set<Tasks> tasks;
}
