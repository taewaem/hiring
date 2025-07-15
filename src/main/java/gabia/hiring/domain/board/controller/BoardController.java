package gabia.hiring.domain.board.controller;


import gabia.hiring.domain.board.dto.BoardRequest;
import gabia.hiring.domain.board.dto.BoardResponse;
import gabia.hiring.domain.board.dto.BoardUpdateRequest;
import gabia.hiring.domain.board.service.impl.BoardServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardServiceImpl boardService;
    /**
     * 게시판 생성
     */
    @PostMapping("/create")
    public ResponseEntity<BoardResponse> createBoard(@Valid @RequestBody BoardRequest boardRequest) {

        BoardResponse response = boardService.createBoard(boardRequest);

        log.info("게시판 생성 요청 - 제목: {}", boardRequest.getTitle() );

        return ResponseEntity.ok(response);
    }

    /**
     * 게시판 단건 조회
     */
    @GetMapping("{boardId}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable Long boardId) {

        BoardResponse response = boardService.getBoard(boardId);

        log.info("{} 게시판 단건 조회 요청", boardId);

        return ResponseEntity.ok(response);
    }

    /**
     * 게시판 전체 조회
     */
    @GetMapping
    public ResponseEntity<List<BoardResponse>> getAllBoards() {

        log.info("모든 게시판 조회 요청");

        List<BoardResponse> responses = boardService.getAllBoards();

        return ResponseEntity.ok(responses);
    }




    /**
     * 제목으로 게시판 조회
     */
    @GetMapping("/search")
    public ResponseEntity<List<BoardResponse>> searchBoard(@RequestParam(required = false) String title) {

        List<BoardResponse> responses = boardService.searchBoards(title);

        log.info("{}이 포함된 제목 게시판 조회 요청", title);

        return ResponseEntity.ok(responses);
    }

    /**
     * 게시판 수정
     * user 토큰으로 인증 후 만약 맞으면 그 유저의 게시물 중 board 수정 가능
     */
    @PatchMapping("update/{boardId}")
    public ResponseEntity<BoardResponse> updateBoard(@PathVariable("boardId") Long boardId,
                                                @RequestBody BoardUpdateRequest boardUpdateRequest) {
        BoardResponse response = boardService.updateBoard(boardId, boardUpdateRequest);

        log.info("{}: {} 게시판 수정 요청", boardId, boardUpdateRequest.getTitle());

        return ResponseEntity.ok(response);
    }

    /**
     * 게시판 삭제
     */
    @DeleteMapping("/delete/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {

        boardService.deleteBoard(boardId);

        log.info("{} 게시판 삭제 요청", boardId);

        //HTTP 상태 코드: 204 No Content
        //본문 내용X (빈 body)
        return ResponseEntity.noContent().build();
    }


}
