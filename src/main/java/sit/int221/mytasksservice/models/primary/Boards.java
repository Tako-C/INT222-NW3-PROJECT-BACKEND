package sit.int221.mytasksservice.models.primary;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "boards")
public class Boards {
    @Id
    @Column(name = "board_id")
    private String boardId;
    @Column(name = "users_oid")
    private String oid;
    private String board_name;
    @OneToMany(mappedBy = "boards",fetch = FetchType.EAGER)
    private Set<Statuses> statuses;
    @OneToMany(mappedBy = "boards" ,fetch = FetchType.EAGER)
    private Set<Tasks> tasks;
    private String visibility;
    @Column(insertable = false, updatable = false)
    private Timestamp createdOn;
    @Column(insertable = false, updatable = false)
    private Timestamp updatedOn;

}
