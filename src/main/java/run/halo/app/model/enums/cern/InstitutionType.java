package run.halo.app.model.enums.cern;

import run.halo.app.model.enums.ValueEnum;

public enum InstitutionType implements ValueEnum<Integer> {

    CENTER(0), STATION(1);

    private final Integer value;

    InstitutionType(Integer value) {
        this.value = value;
    }

    /**
     * Get enum value.
     *
     * @return enum value
     */
    @Override
    public Integer getValue() {
        return value;
    }

}
