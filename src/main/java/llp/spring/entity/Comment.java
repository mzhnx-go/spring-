package llp.spring.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;

@TableName("t_comment")
public class Comment {
    private Integer id;
    private Integer articleId;
    private String content;
    private String author;
    private LocalDate created;


    // Constructors
    public Comment() {}

    public Comment(Integer articleId, String content, String author) {
        this.articleId = articleId;
        this.content = content;
        this.author = author;
        this.created = LocalDate.now();

    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    
}
