package gabia.hiring.domain.board.repository;

import gabia.hiring.domain.board.entity.Board;
import gabia.hiring.domain.board.entity.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 제목에 특정 문자열이 포함된 게시글 찾기
    List<Board> findByTitleContaining(String title);
    // 내용에 특정 문자열 포함된 게시글 찾기
    List<Board> findByContentContaining(String content);

    // 권한별 게시글 조회
    List<Board> findByBoardTypeIn(List<BoardType> boardTypes);

    // 특정 유저의 게시글 조회
    List<Board> findByUserId(Long userId);

    // 권한별 + 제목 검색
    List<Board> findByBoardTypeInAndTitleContaining(List<BoardType> boardTypes, String title);

    // 페이징을 위한 메서드들
    Page<Board> findByBoardTypeIn(List<BoardType> boardTypes, Pageable pageable);
    Page<Board> findByBoardTypeInAndTitleContaining(List<BoardType> boardTypes, String title, Pageable pageable);

    // 권한별 최신 게시글 조회
    @Query("SELECT b FROM Board b WHERE b.boardType IN :boardTypes ORDER BY b.createdAt DESC")
    List<Board> findLatestBoardsByTypes(@Param("boardTypes") List<BoardType> boardTypes);

    // 예시: 특정 기간 내 게시글 조회
    @Query("SELECT b FROM Board b WHERE b.boardType IN :boardTypes AND b.createdAt >= :startDate")
    List<Board> findBoardsByTypesAndDateAfter(@Param("boardTypes") List<BoardType> boardTypes,
                                              @Param("startDate") LocalDateTime startDate);


}
