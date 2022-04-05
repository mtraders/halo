package run.halo.app.service.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import run.halo.app.cache.lock.CacheLock;
import run.halo.app.event.logger.LogEvent;
import run.halo.app.event.user.UserUpdatedEvent;
import run.halo.app.exception.BadRequestException;
import run.halo.app.exception.ForbiddenException;
import run.halo.app.exception.NotFoundException;
import run.halo.app.exception.ServiceException;
import run.halo.app.model.entity.Journal;
import run.halo.app.model.entity.User;
import run.halo.app.model.enums.LogType;
import run.halo.app.model.enums.MFAType;
import run.halo.app.model.enums.UserType;
import run.halo.app.model.params.JournalQuery;
import run.halo.app.model.params.UserParam;
import run.halo.app.model.params.UserQuery;
import run.halo.app.repository.UserRepository;
import run.halo.app.service.UserService;
import run.halo.app.service.base.AbstractCrudService;
import run.halo.app.utils.BCrypt;
import run.halo.app.utils.DateUtils;
import run.halo.app.utils.HaloUtils;
import javax.persistence.criteria.Predicate;

/**
 * UserService implementation class.
 *
 * @author ryanwang
 * @author johnniang
 * @date 2019-03-14
 */
@Service
public class UserServiceImpl extends AbstractCrudService<User, Integer> implements UserService {

    private final UserRepository userRepository;

    private final ApplicationEventPublisher eventPublisher;

    public UserServiceImpl(UserRepository userRepository,
        ApplicationEventPublisher eventPublisher) {
        super(userRepository);
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<User> getCurrentUser() {
        // Find all users
        List<User> users = listAll();

        if (CollectionUtils.isEmpty(users)) {
            // Return empty user
            return Optional.empty();
        }

        // Return the first user
        return Optional.of(users.get(0));
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getByUsernameOfNonNull(String username) {
        return getByUsername(username).orElseThrow(
            () -> new NotFoundException("The username does not exist").setErrorData(username));
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getByEmailOfNonNull(String email) {
        return getByEmail(email).orElseThrow(
            () -> new NotFoundException("The email does not exist").setErrorData(email));
    }

    @Override
    public User updatePassword(String oldPassword, String newPassword, Integer userId) {
        Assert.hasText(oldPassword, "Old password must not be blank");
        Assert.hasText(newPassword, "New password must not be blank");
        Assert.notNull(userId, "User id must not be blank");

        if (oldPassword.equals(newPassword)) {
            throw new BadRequestException("新密码和旧密码不能相同");
        }

        // Get the user
        User user = getById(userId);

        // Check the user old password
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BadRequestException("旧密码错误").setErrorData(oldPassword);
        }

        // Set new password
        setPassword(user, newPassword);

        // Update this user
        User updatedUser = update(user);

        // Log it
        eventPublisher.publishEvent(
            new LogEvent(this, updatedUser.getId().toString(), LogType.PASSWORD_UPDATED,
                HaloUtils.desensitize(oldPassword, 2, 1)));

        return updatedUser;
    }

    @Override
    public User createBy(UserParam userParam) {
        Assert.notNull(userParam, "User param must not be null");

        User user = userParam.convertTo();

        setPassword(user, userParam.getPassword());
        user.setUserType(UserType.ADMIN);
        return create(user);
    }

    @Override
    public void mustNotExpire(User user) {
        Assert.notNull(user, "User must not be null");

        Date now = DateUtils.now();
        if (user.getExpireTime() != null && user.getExpireTime().after(now)) {
            long seconds =
                TimeUnit.MILLISECONDS.toSeconds(user.getExpireTime().getTime() - now.getTime());
            // If expired
            throw new ForbiddenException("账号已被停用，请 " + HaloUtils.timeFormat(seconds) + " 后重试")
                .setErrorData(seconds);
        }
    }

    @Override
    public boolean passwordMatch(User user, String plainPassword) {
        Assert.notNull(user, "User must not be null");

        return !StringUtils.isBlank(plainPassword)
            && BCrypt.checkpw(plainPassword, user.getPassword());
    }

    @Override
    @CacheLock
    public User create(User user) {
        // Check user
        if (count() != 0) {
            throw new BadRequestException("当前博客已有用户");
        }

        User createdUser = super.create(user);

        eventPublisher.publishEvent(new UserUpdatedEvent(this, createdUser.getId()));

        return createdUser;
    }

    @Override
    public User update(User user) {
        User updatedUser = super.update(user);

        // Log it
        eventPublisher.publishEvent(
            new LogEvent(this, user.getId().toString(), LogType.PROFILE_UPDATED,
                user.getUsername()));
        eventPublisher.publishEvent(new UserUpdatedEvent(this, user.getId()));

        return updatedUser;
    }

    @Override
    public void setPassword(@NonNull User user, @NonNull String plainPassword) {
        Assert.notNull(user, "User must not be null");
        Assert.hasText(plainPassword, "Plain password must not be blank");

        user.setPassword(BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
        user.setMfaType(MFAType.NONE);
        user.setMfaKey(null);
    }

    @Override
    public void setUserType(@NonNull User user, @NonNull UserType userType) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(userType, "userType must not be blank");
        user.setUserType(userType);
    }

    @Override
    public boolean verifyUser(String username, String password) {
        User user = getCurrentUser().orElseThrow(() -> new ServiceException("未查询到博主信息"));
        return user.getUsername().equals(username) && user.getEmail().equals(password);
    }

    @Override
    @NonNull
    public User updateMFA(@NonNull MFAType mfaType, String mfaKey, @NonNull Integer userId) {
        Assert.notNull(mfaType, "MFA Type must not be null");

        // get User
        User user = getById(userId);
        // set MFA
        user.setMfaType(mfaType);
        user.setMfaKey((MFAType.NONE == mfaType) ? null : mfaKey);
        // Update this user
        User updatedUser = update(user);
        // Log it
        eventPublisher.publishEvent(
            new LogEvent(this, updatedUser.getId().toString(), LogType.MFA_UPDATED,
                "MFA Type:" + mfaType));

        return updatedUser;

    }

    @Override
    public Page<User> pageBy(UserQuery userQuery, Pageable pageable) {
        Assert.notNull(userQuery, "user query must not be null");
        Assert.notNull(pageable, "Page info must not be null");
        return userRepository.findAll(buildSpecByQuery(userQuery), pageable);
    }

    @NonNull
    private Specification<User> buildSpecByQuery(@NonNull UserQuery userQuery) {
        Assert.notNull(userQuery, "user query must not be null");

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

            if (userQuery.getUserType() != null) {
                predicates.add(
                    criteriaBuilder.equal(root.get("userType"), userQuery.getUserType()));
            }

            if (userQuery.getUsername() != null) {
                // Format like condition
                String likeCondition =
                    String.format("%%%s%%", StringUtils.strip(userQuery.getUsername()));

                // Build like predicate
                Predicate contentLike = criteriaBuilder.like(root.get("content"), likeCondition);

                predicates.add(criteriaBuilder.or(contentLike));
            }

            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
    }
}
