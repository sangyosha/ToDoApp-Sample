package todo_demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TODO")
@Data
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String task;
    @Column(nullable = false)  // ← フィールドに付ける
    private boolean done = false; // ← デフォルト false に設定

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // USERS.id と紐付け
    private User user;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
