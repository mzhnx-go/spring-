package llp.spring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import llp.spring.entity.Comment;
import llp.spring.mapper.CommentMapper;
import llp.spring.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import llp.spring.tools.PageParams;
import llp.spring.tools.Result;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-10-27
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public Comment insert(Comment comment){
        commentMapper.insert(comment);
        return comment;
    }

    public Result getAPageCommentByArticleId(Integer articleId, PageParams pageParams) {
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.eq("article_id", articleId);
        wrapper.orderBy(true, false,"id");
        Page<Comment> page = new Page<>(pageParams.getPage(),pageParams.getRows());
        Page<Comment> aPage = commentMapper.selectPage(page, wrapper);
        Result result = new Result();
        result.getMap().put("comment",aPage.getRecords());
        return result;
    }
}

