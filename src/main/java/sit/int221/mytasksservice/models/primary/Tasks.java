package sit.int221.mytasksservice.models.primary;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "tasks")
public class Tasks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z", timezone = "UTC")
    @Column(insertable = false, updatable = false)
    private Timestamp createdOn;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z", timezone = "UTC")
    @Column(insertable = false, updatable = false)
    private Timestamp updatedOn;
    @ManyToOne
    @JoinColumn(name = "status_id")
    private Statuses status;
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Boards boards;
}
