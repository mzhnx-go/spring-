package llp.spring.controller;

import llp.spring.entity.Comment;
import llp.spring.service.ICommentService;
import llp.spring.tools.PageParams;
import llp.spring.tools.Result;
import llp.spring.tools.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private ICommentService commentService;

    @PostMapping("/insert")
    public Result insert(@RequestBody Comment comment) {
        Result result=new Result();
        try{
            System.out.println("准备插入评论: " + comment.getContent());
            comment.setAuthor("李四");
            comment.setCreated(Tools.dataToLocalDate(new Date()));
            Comment comment1=commentService.insert(comment);
            System.out.println("评论插入成功，ID为: " + comment1.getId());
            result.getMap().put("comment",comment1);
            result.setMsg("添加评论成功！");
            result.setSuccess(true);
        }catch (Exception e) {
            System.err.println("评论插入失败: " + e.getMessage());
            result.setErrorMessage("添加评论失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    // 修复1：修正方法名（去掉多余的d）
    // 修复2：添加@RequestParam接收URL中的articleId参数
    @PostMapping("/getAPageCommentByArticleId")
    public Result getAPageCommentByArticleId(
            @RequestParam Integer articleId,  // 关键：接收?articleId=13参数
            @RequestBody PageParams pageParams  // 接收请求体中的分页参数
    ) {
        Result result=new Result();
        try{
            result=commentService.getAPageCommentByArticleId(articleId, pageParams);

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}