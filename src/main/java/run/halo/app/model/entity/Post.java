package run.halo.app.model.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static run.halo.app.model.support.CernConst.BASE_POST_TYPE;

/**
 * Post entity.
 *
 * @author johnniang
 */
@Entity(name = "Post")
@DiscriminatorValue(value = BASE_POST_TYPE)
public class Post extends BasePost {

}
