package run.halo.app.model.params.cern;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.model.dto.base.InputConverter;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.params.BasePostParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
public class NewsParam extends BasePostParam implements InputConverter<News> {

    @Size(max = 255, message = "新闻来源长度不能超过 {max}")
    private String source;

    @Size(max = 255, message = "新闻链接长度不能超过 {max}")
    private String link;

    @Override
    @Size(max = 255, message = "新闻别名的字符长度不能超过 {max}")
    public String getSlug() {
        return super.getSlug();
    }

    @Override
    @NotBlank(message = "新闻标题不能为空")
    @Size(max = 100, message = "新闻标题的字符长度不能超过 {max}")
    public String getTitle() {
        return super.getTitle();
    }
}
