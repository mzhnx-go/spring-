package llp.spring.service;

import llp.spring.entity.Article;
import llp.spring.tools.PageParams;
import llp.spring.tools.Result;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ArticleService {
    public void deleteById(Integer id);
    public void update(Article article);
    public String upload(MultipartFile file)throws IOException;
    public Result getArticleAndCommentByArticleId (Integer articleId, PageParams pageParams);
    public Result getAPageOfArticleVO(PageParams pageParams);
    public void publish(Article article);
    public Article selectById(Integer id);

}
