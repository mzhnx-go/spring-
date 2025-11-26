package llp.spring.controller;
import llp.spring.entity.Article;
import llp.spring.service.ArticleService;
import llp.spring.tools.PageParams;
import llp.spring.tools.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping("/getAPageOfArticleVO")
    public Result getAPageOfArticleVO(@RequestBody PageParams pageParams) {
        Result result = new Result();
        try{
            result=articleService.getAPageOfArticleVO(pageParams);
        }catch (Exception e) {
            result.setErrorMessage("查询文章失败！");
            e.printStackTrace();
        }
        return result;
    }
    @PostMapping("/getArticleAndCommentByArticleId")
    public Result getArticleAndCommentByArticleId (Integer articleId, @RequestBody PageParams pageParams) {
       Result result=new Result();
       try {
           result=articleService.getArticleAndCommentByArticleId(articleId, pageParams);
       } catch (Exception e) {
           result.setErrorMessage("查询文章失败！");
           e.printStackTrace();
       }
       return result;
    }

    @PostMapping("/upload")
    public Result upLoad(MultipartFile file) {
        Result result= new Result();
        try{
            String url= articleService.upload(file);
            result.getMap().put("url",url);
        }catch (Exception e){
            result.setErrorMessage("上传失败！");
            e.printStackTrace();
        }
        return result;
    }
    @RequestMapping("/publishArticle")
    public String publishArticle(String type, @RequestBody Article article) {
        try{
            if("add".equals(type))
            articleService.publish(article);
            else if("edit".equals(type))
                articleService.update(article);
            return "添加成功!";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "添加失败！";
        }

    @RequestMapping("/selectById")
    public Result selectById(Integer id) {
        System.out.println("查询文章ID: " + id);
        Result result = new Result();
        try {
            Article article = articleService.selectById(id);
            if (article == null) {
                result.setSuccess(false);
                result.setErrorMessage("指定ID的文章不存在");
                return result;
            }
            result.getMap().put("article", article);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("查询失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    @PostMapping("/postHello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/getHello")
    public int[] hello1() {
        return new int[]{10,20,30};
    }


}
