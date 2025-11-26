package llp.spring.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import llp.spring.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    @Select("SELECT * FROM t_comment WHERE article_id=#{articleId} ORDER BY id DESC")
    public List<Comment> selectByArticleId(Integer articleId);

    @Select("SELECT * FROM t_comment WHERE article_id=#{articleId} ORDER BY id DESC limit #{offset}, #{size}")
    public List<Comment> getAPageCommentByArticleId(
            @Param("articleId")Integer articleId,
            @Param("offset")Integer offset,
            @Param("size")Integer size);
}
