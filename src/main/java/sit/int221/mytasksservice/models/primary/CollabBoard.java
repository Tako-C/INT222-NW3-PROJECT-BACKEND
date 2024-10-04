package sit.int221.mytasksservice.models.primary;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "collab_board")
public class CollabBoard {

    @Id
    @Column(name = "collab_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer collabId;

    private String oid;
    private String name;

    private String email;

    @Column(name = "access_right")
    private String accessRight;

    @Column(name = "boards_id")
    private String boardsId;

    @Column(insertable = false, updatable = false)
    private Timestamp added_on;

    @Column(insertable = false, updatable = false)
    private Timestamp updated_on;
}
