package llp.spring.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleVO {
    private Integer id;
    private String title;
    private Date created;
    private String categories;
    private Integer hits;
}
