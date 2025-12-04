package llp.spring.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import llp.spring.entity.Article;
import llp.spring.entity.vo.ArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Wrapper;
import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article>{
    @Select("SELECT t_article.id,title,created,categories,t_statistic.hits " +
            "FROM t_article, t_statistic " +
            "${ew.customSqlSegment}")
    IPage<ArticleVO> getAPageOfArticleVO(IPage<ArticleVO> page, @Param("ew") QueryWrapper<ArticleVO> wrapper);

    // 对应ArticleMapper.xml文件中的SQL语句，需要指出方法名、参数和返回值类型
    public List<Article> getAPage(
            @Param("offset") Long offset, // offset表示从符合条件的第几条记录开始返回，从0开始计数
            @Param("size") Long size     // 返回几条数据
    );

    @Select("SELECT * FROM t_article${ew.customSqlSegment}")
    IPage< Article> getAPageOfArticle(IPage< Article> page, @Param("ew")  QueryWrapper<ArticleVO> wrapper);
}
