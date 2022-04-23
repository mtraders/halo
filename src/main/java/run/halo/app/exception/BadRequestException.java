package run.halo.app.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

/**
 * Exception caused by bad request.
 *
 * @author johnniang
 */
public class BadRequestException extends AbstractHaloException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public @NotNull HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
