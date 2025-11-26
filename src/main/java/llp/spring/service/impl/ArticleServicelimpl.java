package llp.spring.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import llp.spring.entity.Statistic;
import llp.spring.entity.vo.ArticleVO;
import llp.spring.mapper.ArticleMapper;
import llp.spring.entity.Article;
import llp.spring.mapper.CommentMapper;
import llp.spring.mapper.StatisticMapper;
import llp.spring.service.ArticleService;
import llp.spring.tools.PageParams;
import llp.spring.tools.Result;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.UUID;

@Service

@Transactional
public class ArticleServicelimpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private StatisticMapper statisticMapper;

    public Result getAPageOfArticleVO(PageParams pageParams) {
        QueryWrapper<ArticleVO> wrapper = new QueryWrapper<>();
        wrapper.orderBy(true, false, "t_article.id");
        wrapper.apply("t_article.id = t_statistic.article_id");
        String s=wrapper.getCustomSqlSegment();
        Page<ArticleVO> page = new Page<ArticleVO>(pageParams.getPage(),pageParams.getRows());
        IPage<ArticleVO> aPage = articleMapper.getAPageOfArticleVO(page, wrapper);
        Result result= new Result();
        pageParams.setTotal(aPage.getTotal());
        result.getMap().put("articleVOs",aPage.getRecords());
        result.getMap().put("pageParams",pageParams);
        return result;
    }


    public Result getArticleAndCommentByArticleId(Integer articleId, PageParams pageParams) {
        Result result= new Result();
        result.getMap().put("article",articleMapper.selectById(articleId));
        result.getMap().put("comments",
                commentMapper.getAPageCommentByArticleId(
                articleId, Math.toIntExact((pageParams.getPage() - 1) * pageParams.getRows()), Math.toIntExact(pageParams.getRows())));
        return result;
    }
    @SneakyThrows
    @Override
    public String upload(MultipartFile file) {
        final File folder = new File("D:\\vue3\\image\\"); //图片位置
        if(!folder.exists()) {
            folder.mkdirs();
        }
        String type = file.getOriginalFilename().substring(
                file.getOriginalFilename().lastIndexOf(".")
        );
        //为文件随机取名
        final String newName = UUID.randomUUID() + type;
        file.transferTo(new File(folder, newName)); //保存文件
        String url = "api/images/" + newName;
        return url;
    }
    //发布文章
    @Transactional
    public void publish(Article article){
        article.setCreated(new Date());
        articleMapper.insert(article);
        //添加统计记录
        Statistic statistic=new Statistic();
        statistic.setArticleId(article.getId());
        statistic.setHits(0);
        statistic.setCommentsNum(0);
        statisticMapper.insert(statistic);
    }



    public Result getArticleAndFirstPageCommentByArticleId(Integer articleId, PageParams pageParams) {
        Result result= new Result();
        result.getMap().put("article",articleMapper.selectById(articleId));
        result.getMap().put("comments",
                commentMapper.getAPageCommentByArticleId(
                        articleId, Math.toIntExact((pageParams.getPage() - 1) * pageParams.getRows()), Math.toIntExact(pageParams.getRows())));
        //文章的点击次数+1
        Statistic statistc=statisticMapper.selectByArticleId(articleId);
        statistc.setHits(statistc.getHits() + 1);
        statisticMapper.updateById(statistc);
        return result;
    }
    public void update(Article article){
        Article newArticle=articleMapper.selectById(article.getId());
        newArticle.setModified(new Date());
        newArticle.setTags(article.getTags());
        newArticle.setContent(article.getContent());
        newArticle.setTitle(article.getTitle());
        articleMapper.updateById(newArticle);
    }
    public void deleteById(Integer id){
        articleMapper.deleteById(id);
    }

    public Article selectById(Integer id){
        return articleMapper.selectById(id);
    }

}
