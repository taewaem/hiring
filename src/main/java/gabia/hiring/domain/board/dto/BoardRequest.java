package gabia.hiring.domain.board.dto;

import gabia.hiring.domain.board.entity.BoardType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardRequest {

    @NotBlank(message = "제목을 입력하세요.")
    private String title;

    @NotBlank(message = "내용을 입력하세요. 최소 10글자 이상 작성하세요.")
    private String content;

}
