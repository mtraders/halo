package run.halo.app.service.assembler;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import run.halo.app.model.dto.UserDTO;
import run.halo.app.model.entity.User;

/**
 * @author mite.chen mite@mtrading.io
 */
@Component
public class UserAssembler {

    @NonNull
    public UserDTO convertToMinimal(User user) {
        Assert.notNull(user, "User must not be null");
        UserDTO userDTO = new UserDTO().convertFrom(user);

        return userDTO;
    }

    @NonNull
    public Page<UserDTO> convertToWithUserVo(Page<User> userPage) {
        Assert.notNull(userPage, "User page must not be null");
        return userPage.map(user -> {
            UserDTO userDTO = new UserDTO().convertFrom(user);
            return userDTO;
        });
    }

}
