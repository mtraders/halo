package run.halo.app.model.enums;

/**
 * User type.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 * @author <a href="mailto:mite@fists.cn">mite.chen</a>
 */
public enum UserType implements ValueEnum<Integer> {
    /**
     * Disable MFA auth.
     */
    ADMIN(0),

    /**
     * Time-based One-time Password (rfc6238).
     * see: https://tools.ietf.org/html/rfc6238
     */
    STATION(1);

    private final Integer value;

    UserType(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
