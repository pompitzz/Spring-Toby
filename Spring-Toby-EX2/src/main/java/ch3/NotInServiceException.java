package ch3;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/22
 */
@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "서비스 일시 중지")
public class NotInServiceException extends RuntimeException {
}
