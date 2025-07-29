package gabia.hiring.global.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_FOUND_USER("U001", "존재하지 않는 유저입니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_USER("U002", "중복된 유저가 존재합니다.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("U003", "비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED ),
    DUPLICATE_EMAIL("U004", "중복된 이메일이 존재합니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_USERNAME("U005", "중복된 닉네임이 존재합니다.", HttpStatus.BAD_REQUEST),

    NOT_FOUND_BOARD("B001", "존재하지 않는 게시판입니다.", HttpStatus.NOT_FOUND),


    INVALID_TOKEN("T001", "유효하지 않은 토큰입니다.", HttpStatus.BAD_REQUEST),

    // 공통 에러
    INVALID_AUTHORITY("C001", "유효하지 않은 권한입니다.", HttpStatus.BAD_REQUEST),
    INVALID_INPUT_VALUE("C002", "입력값이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("C999", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);


    private final String code;
    private final String message;
    private final HttpStatus status;
}
