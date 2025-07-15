package gabia.hiring.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardUpdateRequest {


    private String title;
    private String content;


    //추가 기능 비밀번호 입력하여 일치하면 게시판 수정
    private String password;
}
