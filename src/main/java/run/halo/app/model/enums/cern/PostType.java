package run.halo.app.model.enums.cern;

import run.halo.app.model.enums.ValueEnum;
import run.halo.app.model.support.CernConst;

public enum PostType implements ValueEnum<Integer> {
    BASE(Integer.valueOf(CernConst.BASE_POST_TYPE)),
    SHEET(Integer.valueOf(CernConst.SHEET_POST_TYPE)),
    NEWS(Integer.valueOf(CernConst.NEWS_POST_TYPE)),
    NOTIFICATION(Integer.valueOf(CernConst.NOTIFICATION_POST_TYPE)),
    PAPER(Integer.valueOf(CernConst.PAPER_POST_TYPE)),
    PROJECT(Integer.valueOf(CernConst.PROJECT_POST_TYPE)),
    ;

    private final Integer value;

    PostType(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
