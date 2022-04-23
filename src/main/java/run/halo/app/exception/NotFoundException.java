package run.halo.app.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

/**
 * Exception of entity not found.
 *
 * @author johnniang
 */
public class NotFoundException extends AbstractHaloException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public @NotNull HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
