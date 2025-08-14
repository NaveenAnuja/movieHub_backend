package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.constants.TokenType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;
    private Boolean revoked;
    private Boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_token"))
    private UserEntity user;
}
