package run.halo.app.model.params;

import lombok.Data;
import run.halo.app.model.enums.UserType;

/**
 * @author mite.chen mite@mtrading.io
 */
@Data
public class UserQuery {
    private String username;

    private UserType userType;
}
