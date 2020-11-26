package com.lhh.crawler.modules.jiketime.parser;

import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.lhh.crawler.modules.common.CommonConst;
import com.lhh.crawler.modules.jiketime.po.Article;
import com.lhh.crawler.modules.jiketime.po.Articles;
import com.lhh.crawler.modules.jiketime.po.Chapters;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述：转换有道云笔记格式
 *
 * @author lhh
 * @Date 2020/11/11 11:01
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cheats {
    FileReader r = new FileReader(CommonConst.JIKE_TIME_FILE);

    private String title;
    private String twoTitle;
    private String content;


    public void parser() {
        String s = r.readString();
        Map<String,String> map= JSON.parseObject(s,Map.class);
        List<Chapters> chapters=new ArrayList<>();
        if(map.containsKey("目录篇")){
            String a = map.get("目录篇");
            chapters = JSON.parseArray(a, Chapters.class);
        }

        String b = map.get("文章列表");
        String c = map.get("文章数据");

        List<Articles> articlesList = JSON.parseArray(b, Articles.class);
        List<Article> list = JSON.parseArray(c, Article.class);
        Multimap<String, String> multimap = ArrayListMultimap.create();

        List<String> keys=new ArrayList<>();
        String data="";
        for (Articles article:articlesList){
            String id = article.getId();
            List<Article> boys = list.stream().filter(s1->s1.getId().equals(id)).collect(Collectors.toList());
            if(boys.size()<=0){
                continue;
            }
            Article article1 = boys.get(0);


            if(!CollectionUtils.isEmpty(chapters)){
                List<Chapters> collect = chapters.stream().filter(s2 -> s2.getId().equals(article.getChapter_id())).collect(Collectors.toList());
                Chapters chapters1 = collect.get(0);

                String s2="";
                s2+="## " +article1.getArticle_title()+"\n";
                s2+="<html>\n" + article1.getArticle_content()+"\n"+
                        "</html>"+"\n";
                multimap.put("# "+chapters1.getTitle(),s2);
                keys.add("# "+chapters1.getTitle());

            }else{
                //没有一级目录情况
                data+="\n" + "# " +article1.getArticle_title()+"\n";
                data+="<html>\n" + article1.getArticle_content()+"\n"+
                        "</html>"+"\n"+"\n";
            }
        }

        if(StringUtils.isNotBlank(data)){
            System.out.println(data);
        }else {
            List<String> strings = duplicateRemovalList(keys);
            String da="";
            for (String s3:strings){
                da+="\n"+s3+"\n";
                List<String> strings1 = (List<String>) multimap.get(s3);
                for (String s4:strings1){
                    da+=s4+"\n";
                }
            }
            System.out.println(da);
        }

    }
    /**
     * list去重复
     * @param <T>
     * @return
     */
    public static <T> List<T> duplicateRemovalList(List<T> ioList)
    {
        LinkedHashSet<T> tmpSet = new LinkedHashSet<T>(ioList.size());
        tmpSet.addAll(ioList);
        ioList.clear();
        ioList.addAll(tmpSet);
        return ioList;
    }

    public static void main(String[] args) {
        Cheats r=new Cheats();
        r.parser();
    }
}
