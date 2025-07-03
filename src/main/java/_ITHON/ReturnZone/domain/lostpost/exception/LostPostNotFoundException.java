package _ITHON.ReturnZone.domain.lostpost.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 이 예외 발생 시 HTTP 404 Not Found 응답
public class LostPostNotFoundException extends RuntimeException {
    public LostPostNotFoundException(String message) {
        super(message);
    }
}