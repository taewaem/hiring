package gabia.hiring.global.exception;

import lombok.Getter;

@Getter
public class HiringException extends RuntimeException {

    private final ErrorCode errorCode;

    public HiringException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
