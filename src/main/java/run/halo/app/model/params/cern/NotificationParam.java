package run.halo.app.model.params.cern;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.model.dto.base.InputConverter;
import run.halo.app.model.entity.cern.Notification;

/**
 * notification param.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationParam extends CernPostParam implements InputConverter<Notification> {
    /**
     * Convert to domain.(shallow)
     *
     * @return new domain with same value(not null)
     */
    @Override
    public Notification convertTo() {
        checkFormat();
        Notification notification = InputConverter.super.convertTo();
        populateContent(notification);
        return notification;
    }

    /**
     * Update a domain by dto.(shallow)
     *
     * @param domain updated domain
     */
    @Override
    public void update(Notification domain) {
        checkFormat();
        populateContent(domain);
        InputConverter.super.update(domain);
    }
}
