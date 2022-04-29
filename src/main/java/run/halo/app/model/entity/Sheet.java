package run.halo.app.model.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static run.halo.app.model.support.CernConst.SHEET_POST_TYPE;

/**
 * Page entity.
 *
 * @author johnniang
 * @date 3/22/19
 */
@Entity(name = "Sheet")
@DiscriminatorValue(SHEET_POST_TYPE)
public class Sheet extends BasePost {

}
