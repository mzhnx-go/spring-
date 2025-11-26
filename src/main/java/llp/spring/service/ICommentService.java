package llp.spring.service;

import llp.spring.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import llp.spring.tools.PageParams;
import llp.spring.tools.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author baomidou
 * @since 2025-10-27
 */
public interface ICommentService extends IService<Comment> {
    public Result getAPageCommentByArticleId(Integer articleId, PageParams pageParams);
    public  Comment insert(Comment comment);
}
