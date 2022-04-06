package run.halo.app.model.properties;

public enum CernProperties implements PropertyEnum {

    /**
     * personnel page prefix.
     */
    PERSONNELS_PREFIX("personnels_prefix", String.class, "personnels");

    private final String value;

    private final Class<?> type;

    private final String defaultValue;

    CernProperties(String value, Class<?> type, String defaultValue) {
        this.value = value;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String defaultValue() {
        return defaultValue;
    }

    @Override
    public String getValue() {
        return value;
    }

}
