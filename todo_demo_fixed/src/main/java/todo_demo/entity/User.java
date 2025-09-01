package todo_demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "USERS", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)  // DBレベルでもユニーク制約あり
    private String username;

    @Column(nullable = false)
    private String password;

    // getter, setter
}
