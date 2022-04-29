package run.halo.app.model.params.cern;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import run.halo.app.model.dto.base.InputConverter;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.utils.SlugUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * personnel param.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
public class PersonnelParam implements InputConverter<Personnel> {
    @NotBlank(message = "人员名称不能为空")
    @Size(max = 255, message = "人员名称长度不能超过 {max}")
    private String name;
    @Size(max = 255, message = "人员英文名称长度不能超过 {max}")
    private String englishName;
    @Size(max = 255, message = "人员别名的字符长度不能超过 {max}")
    private String slug;
    @Size(max = 127, message = "邮箱的字符长度不能超过 {max}")
    private String email;
    @Size(max = 1023, message = "人员头像的字符长度不能超过 {max}")
    private String thumbnail;

    private String buildSlug() {
        return StringUtils.isBlank(slug) ? StringUtils.isBlank(englishName) ? SlugUtils.slug(name) : SlugUtils.slug(englishName) :
            SlugUtils.slug(slug);
    }

    @Override
    public void update(Personnel personnel) {
        slug = buildSlug();
        if (null == thumbnail) {
            thumbnail = "";
        }
        InputConverter.super.update(personnel);
    }

    @Override
    public Personnel convertTo() {
        slug = buildSlug();
        if (null == thumbnail) {
            thumbnail = "";
        }
        return InputConverter.super.convertTo();
    }
}
