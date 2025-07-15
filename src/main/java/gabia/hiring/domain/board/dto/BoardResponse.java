package gabia.hiring.domain.board.dto;

import gabia.hiring.domain.board.entity.Board;
import gabia.hiring.domain.board.entity.BoardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BoardResponse {


    private Long id;
    private String title;
    private String content;
    private String message;
    private BoardType boardType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BoardResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.boardType = board.getBoardType();
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
    }

}
