package gabia.hiring.domain.user.entity;

import gabia.hiring.domain.board.entity.Board;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"loginId"}
        ),
        @UniqueConstraint(
                columnNames = {"email"}
        )
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String userName;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private IndividualProfile individualProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CompanyProfile companyProfile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Board> boardList = new ArrayList<>();

    @Builder
    public User(String email, String password, String userName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
    }

    public User toEntity(String encodePassword) {
        return User.builder()
                .email(this.email)
                .password(encodePassword)
                .userName(this.userName)
                .build();
    }
}
