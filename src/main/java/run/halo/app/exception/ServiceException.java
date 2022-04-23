package run.halo.app.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

/**
 * Exception caused by service.
 *
 * @author johnniang
 */
public class ServiceException extends AbstractHaloException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public @NotNull HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
