package run.halo.app.model.entity.cern;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.entity.BasePost;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity(name = "Notification")
@DiscriminatorValue("5")
public class Notification extends BasePost{
}
