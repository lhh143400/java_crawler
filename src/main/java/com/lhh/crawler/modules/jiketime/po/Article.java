package com.lhh.crawler.modules.jiketime.po;

import lombok.Data;

/**
 * 描述：具体文章数据
 *
 * @author lhh
 * @Date 2020/11/11 10:35
 */
@Data
public class Article {
    private String id;
    private String article_title;
    private String article_content;
    private String article_poster_wxlite;
}
