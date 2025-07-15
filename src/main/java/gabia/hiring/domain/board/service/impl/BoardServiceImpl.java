package gabia.hiring.domain.board.service.impl;

import gabia.hiring.domain.board.dto.BoardRequest;
import gabia.hiring.domain.board.dto.BoardResponse;
import gabia.hiring.domain.board.dto.BoardUpdateRequest;
import gabia.hiring.domain.board.entity.Board;
import gabia.hiring.domain.board.entity.BoardType;
import gabia.hiring.domain.board.repository.BoardRepository;
import gabia.hiring.domain.user.entity.Role;
import gabia.hiring.domain.user.entity.User;
import gabia.hiring.domain.user.repository.UserRepository;
import gabia.hiring.global.exception.ErrorCode;
import gabia.hiring.global.exception.HiringException;
import jakarta.persistence.EnumType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * 게시판 생성
     */
    @Transactional
    public BoardResponse createBoard(BoardRequest boardRequest,BoardType boardType, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HiringException(ErrorCode.NOT_FOUND_USER));

        if (!canCreateBoard(boardType, user.getRole())) {
            throw new HiringException(ErrorCode.INVALID_AUTHORITY);
        }

        Board board = Board.builder()
                .title(boardRequest.getTitle())
                .content(boardRequest.getContent())
                .user(user)
                .boardType(boardType)
                .build();

        Board savedBoard = boardRepository.save(board);
        log.info("게시판 생성 성공 - ID: {}, 제목: {}", savedBoard.getId(), savedBoard.getTitle());


        return new BoardResponse(savedBoard);
    }

    /**
     * 게시판 검색
     */
//    @Override
    public List<BoardResponse> searchBoards(String title) {
        List<Board> searchBoardList = boardRepository.findByTitleContaining(title);

        if (searchBoardList.isEmpty()) {
            log.info("검색 결과가 없습니다 - 검색어: {}", title);
        } else {
            log.info("검색 완료 - 검색어: {}, 결과: {}개", title, searchBoardList.size());
        }

        return searchBoardList.stream()
                .map(board -> new BoardResponse(board))        // .map(BoardResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 게시판 수정
     */
//    @Override
    @Transactional
    public BoardResponse updateBoard(Long boardId, BoardUpdateRequest boardUpdateRequest) {
        Board board = findBoardById(boardId);


        board.updateBoard(boardUpdateRequest.getTitle(), boardUpdateRequest.getContent());

        log.info("게시판 수정 성공 - ID: {}, 제목: {}", boardId, boardUpdateRequest.getTitle());

        // from 메서드를 사용하여 DTO 변환
        return BoardResponse(board);
    }

    /**
     * 게시판 삭제
     */
//    @Override
    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = findBoardById(boardId);

        log.info("게시판 삭제 - ID: {}, 제목: {}", boardId, board.getTitle());
        boardRepository.delete(board);
    }

    /**
     * 게시판 단건 조회
     */
//    @Override
    public BoardResponse getBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new HiringException(ErrorCode.NOT_FOUND_BOARD));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HiringException(ErrorCode.NOT_FOUND_USER));

        if (!board.canAccess(user.getRole())) {
            throw new HiringException(ErrorCode.INVALID_AUTHORITY);
        }

        log.info("게시판 조회 - ID: {}", boardId);

        // from 메서드를 사용하여 DTO 변환
        return new BoardResponse(board);
    }

    /**
     * 권한별 게시판 목록 조회
     */
    public Page<BoardResponse> getAllBoardByType(BoardType boardType, Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HiringException(ErrorCode.NOT_FOUND_USER));

        List<BoardType> accessibleTypes = getAccessibleBoardTypes(user.getRole());

        if (!canAccessBoardType(boardType, user.getRole())) {
            throw new HiringException(ErrorCode.INVALID_AUTHORITY);
        }

        Page<Board> boards = boardRepository.findByBoardTypeIn(accessibleTypes, pageable);

        return boards.map(board -> new BoardResponse(board));
    }
    /**
     * 전체 게시판 조회
     */
//    @Override
    public List<BoardResponse> getAllBoards() {
        List<Board> boards = boardRepository.findAll();
        log.info("전체 게시판 조회 - 총 {}개", boards.size());

        return boards.stream()
                .map(board -> new BoardResponse(board))
                .collect(Collectors.toList());
    }

    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new HiringException(ErrorCode.NOT_FOUND_BOARD));
    }


    /**
     * 사용자 조회 및 검증
     */
    private User validateAndGetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new HiringException(ErrorCode.NOT_FOUND_USER));
    }

    /**
     * 권한별 접근 가능한 게시판 타입 조회
     */
    private List<BoardType> getAccessibleBoardTypes(Role userRole) {
        List<BoardType> accessibleTypes = new ArrayList<>();

        switch (userRole) {
            case ADMIN:
                accessibleTypes.addAll(Arrays.asList(BoardType.values()));
                break;
            case COMPANY:   
                accessibleTypes.add(BoardType.BOARD_PUBLIC);
                accessibleTypes.add(BoardType.BOARD_COMPANY);
                break;
            case USER:
                accessibleTypes.add(BoardType.BOARD_PUBLIC);
                break;
        }

        return accessibleTypes;
    }

    /**
     * 게시판 타입별 작성 권한 체크
     */
    private boolean canCreateBoard(BoardType boardType, Role userRole) {
        switch (boardType) {
            case BOARD_PUBLIC:
                return true; // 모든 사용자가 공개 게시판에 작성 가능
            case BOARD_COMPANY:
                return userRole == Role.COMPANY || userRole == Role.ADMIN;
            case BOARD_ADMIN:
                return userRole == Role.ADMIN;
            default:
                return false;
        }
    }

    // 게시판 타입별 접근 권한 체크
    private boolean canAccessBoardType(BoardType boardType, Role userRole) {
        switch (boardType) {
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
}
