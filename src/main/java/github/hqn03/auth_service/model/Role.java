package github.hqn03.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = true)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id",  nullable = false),
            inverseJoinColumns = @JoinColumn(name = "permission_id", nullable = false)
    )
    private Set<Permission> permissions;
}
