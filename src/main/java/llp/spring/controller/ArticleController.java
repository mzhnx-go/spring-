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

    @PostMapping("/getIndexData")
    public Result getIndexData() {
        Result result = new Result();
        try {
            result = articleService.getIndexData();
        } catch (Exception e) {
            result.setErrorMessage("获取数据失败！");
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

    @PostMapping("/deleteById")
    public Result deleteById(Integer id){
        // 新增调试信息：打印接收到的参数
        System.out.println("Entering deleteById method. Received id: " + id);

        Result result=new Result();
        try{
            // 新增调试信息：打印即将执行删除操作
            System.out.println("About to call articleService.deleteById with id: " + id);

            articleService.deleteById(id);

            // 新增调试信息：打印删除操作成功完成
            System.out.println("articleService.deleteById succeeded for id: " + id);

        }catch (Exception e){
            result.setErrorMessage("查询文章失败!");
            e.printStackTrace();

            // 新增调试信息：打印异常信息
            System.out.println("Exception occurred in deleteById for id: " + id + ". Error message: " + e.getMessage());
        }

        // 新增调试信息：打印即将返回的结果对象状态
        System.out.println("Exiting deleteById method. Returning result: " + result);

        return  result;
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
