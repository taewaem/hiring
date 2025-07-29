package gabia.hiring.domain.board.entity;


import gabia.hiring.domain.user.entity.Role;
import gabia.hiring.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * @EntityListeners(AuditingEntityListener.class) JPA가 엔티티의 생명주기 이벤트를 감지하여 자동으로 감사 정보를 처리하도록 등록
 */
@Table(name = "board")
@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;


    @Size(max = 50)
    private String title;

    @Size(max = 2000, min = 10)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    @Builder
    public Board(String title, String content, User user, BoardType boardType) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.boardType = boardType;
    }

    // 게시글 수정
    public void updateBoard(String title, String content) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title;
        }
        if (content != null && !content.trim().isEmpty()) {
            this.content = content;
        }
    }

    // 접근 권한 체크
    public boolean canAccess(Role userRole) {
        switch (this.boardType) {
            case BOARD_PUBLIC:
                return true;
            case BOARD_COMPANY:
                return userRole == Role.COMPANY || userRole == Role.ADMIN;
            case BOARD_ADMIN:
                return userRole == Role.ADMIN;
            default:
                return false;
        }
    }

    //게시글 관리 가능 여부
    public boolean canManage(Long userId, Role userRole) {
        return userRole == Role.ADMIN || userId.equals(this.user.getId());
    }

}
