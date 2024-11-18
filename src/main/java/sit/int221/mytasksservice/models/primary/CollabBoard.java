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

    @Column(name = "status_invite", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private InviteStatus statusInvite;

    @Column(insertable = false, updatable = false)
    private Timestamp added_on;

    @Column(insertable = false, updatable = false)
    private Timestamp updated_on;

    @Column(name = "token", unique = true)
    private String token;

    @PrePersist
    protected void onCreate() {
        if (this.statusInvite == null) {
            this.statusInvite = InviteStatus.PENDING;
        }
    }
}
