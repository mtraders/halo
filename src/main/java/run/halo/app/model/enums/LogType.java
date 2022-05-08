package run.halo.app.model.enums;

/**
 * Log type.
 *
 * @author johnniang
 */
public enum LogType implements ValueEnum<Integer> {

    /**
     * Blog initialization.
     */
    BLOG_INITIALIZED(0),

    /**
     * Post published.
     */
    POST_PUBLISHED(5),

    /**
     * Post edited.
     */
    POST_EDITED(15),

    /**
     * Post deleted.
     */
    POST_DELETED(20),

    /**
     * Logged in.
     */
    LOGGED_IN(25),

    /**
     * Logged out.
     */
    LOGGED_OUT(30),

    /**
     * Logged failed.
     */
    LOGIN_FAILED(35),

    /**
     * Updated the blogger password.
     */
    PASSWORD_UPDATED(40),

    /**
     * Updated the blogger profile.
     */
    PROFILE_UPDATED(45),

    /**
     * Sheet published.
     */
    SHEET_PUBLISHED(50),

    /**
     * Sheet edited.
     */
    SHEET_EDITED(55),

    /**
     * Sheet deleted.
     */
    SHEET_DELETED(60),

    /**
     * MFA Updated.
     */
    MFA_UPDATED(65),

    /**
     * Logged pre check.
     */
    LOGGED_PRE_CHECK(70),

    /**
     * News create.
     */
    NEWS_PUBLISHED(100),

    /**
     * News edited.
     */
    NEWS_EDITED(101),
    /**
     * News deleted.
     */
    NEWS_DELETED(102),

    /**
     * Notification create.
     */
    NOTIFICATION_PUBLISHED(103),

    /**
     * Notification edited.
     */
    NOTIFICATION_EDITED(104),
    /**
     * Notification deleted.
     */
    NOTIFICATION_DELETED(105),
    ;


    private final Integer value;

    LogType(Integer value) {
        this.value = value;
    }


    @Override
    public Integer getValue() {
        return value;
    }
}
