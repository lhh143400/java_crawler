package com.lhh.crawler.modules.jiketime.parser;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lhh.crawler.modules.common.CommonConst;
import com.lhh.crawler.modules.jiketime.po.Article;
import com.lhh.crawler.modules.jiketime.po.Articles;
import com.lhh.crawler.modules.jiketime.po.Chapters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：极客时间数据爬取
 *
 * @author lhh
 * @Date 2020/11/11 9:41
 */
public class JiKeTimeParser {
    Map<String, String> m = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(JiKeTimeParser.class);

    /**
     * 目录篇
     */
    public void parserChapter(String refererId, int cid) {
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", "_ga=GA1.2.1845252910.1600913787; LF_ID=1600913787520-2815675-4661130; GCID=b6018df-648b593-15c9163-f76f7dc; GRID=b6018df-648b593-15c9163-f76f7dc; GCESS=BQkBAQQEAC8NAAME.Ni1XwUEAAAAAAcEiHftagoEAAAAAAIE.Ni1XwsCBQAIAQMGBKqcpyoMAQEBCLUOFwAAAAAA; gksskpitn=59d943a3-6273-4225-b76c-8f38d455789c; _gid=GA1.2.342923536.1606095456; Hm_lvt_59c4ff31a9ee6263811b23eb921a5083=1605491709,1605753017,1605753086,1606095456; Hm_lvt_022f847c4e3acd44d4a2481d9187f1e6=1605753017,1605753052,1605753086,1606095456; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%221511093%22%2C%22first_id%22%3A%22175de5716e02e3-08527e39c51bc7-3323765-1327104-175de5716e135a%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E8%87%AA%E7%84%B6%E6%90%9C%E7%B4%A2%E6%B5%81%E9%87%8F%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC%22%2C%22%24latest_referrer%22%3A%22https%3A%2F%2Fwww.baidu.com%2Flink%22%2C%22%24latest_landing_page%22%3A%22https%3A%2F%2Ftime.geekbang.org%2F%22%2C%22%24latest_utm_term%22%3A%22pc_interstitial_703%22%7D%2C%22%24device_id%22%3A%22174bde5c80c2e7-085474dc682e3f-3323765-1327104-174bde5c80d840%22%7D; _gat=1; Hm_lpvt_59c4ff31a9ee6263811b23eb921a5083=1606096721; Hm_lpvt_022f847c4e3acd44d4a2481d9187f1e6=1606096721; gk_process_ev={%22count%22:9}; SERVERID=3431a294a18c59fc8f5805662e2bd51e|1606096743|160609545");
        map.put("Host", "time.geekbang.org");
        map.put("Origin", "https://time.geekbang.org");
        map.put("Referer", "https://time.geekbang.org/column/article/" + refererId);
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
        map.put("Accept", "application/json, text/plain, */*");
        map.put("Accept-Encoding", "gzip, deflate, br");
        map.put("Content-Type", "application/json");

        String url = "https://time.geekbang.org/serv/v1/chapters";
        JSONObject param = new JSONObject();
        param.put("cid", cid);
        HttpResponse resultStr = HttpRequest.post(url)
                .addHeaders(map).body(param.toString())
                .execute();

        String body = resultStr.body();
        List<Chapters> c = JSONObject.parseArray(JSONObject.parseObject(body).get("data").toString(), Chapters.class);
        m.put("目录篇", JSON.toJSONString(c));
    }

    /**
     * 文章列表
     */
    public void parserList(String refererId, int cid, String articlesCookie, String articeCookie,int start) {
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", articlesCookie);
        map.put("Host", "time.geekbang.org");
        map.put("Origin", "https://time.geekbang.org");
        map.put("Referer", "https://time.geekbang.org/column/article/" + refererId);
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
        map.put("Accept", "application/json, text/plain, */*");
        map.put("Accept-Encoding", "gzip, deflate, br");
        map.put("Content-Type", "application/json");


        String url = "https://time.geekbang.org/serv/v1/column/articles";
        JSONObject param = new JSONObject();
        // param.put("chapter_ids",Arrays.asList("1220","1221", "1222", "1223", "1228"));
        param.put("cid", cid);
        param.put("order", "earliest");
        param.put("prev", "0");
        param.put("sample", "false");
        param.put("size", 100);

        HttpResponse resultStr = HttpRequest.post(url)
                .addHeaders(map).body(param.toString())
                .execute();

        String body = resultStr.body();
        JSONObject jsonObject = JSONObject.parseObject(body);
        List<Articles> articlesList = JSONObject.parseArray(jsonObject.getJSONObject("data").get("list").toString(), Articles.class);
        m.put("文章列表", jsonObject.toJSONString(articlesList));

        List<Article> list = new ArrayList<>();
        for (int i=start;i<articlesList.size();i++) {
            Articles articles = articlesList.get(i);
            HttpResponse parser = parser(articles.getId(), articeCookie);
            if (parser.getStatus() != 200) {
                continue;
            }
            JSONObject a1 = JSONObject.parseObject(parser.body());
            Article jsonObject1 = JSON.parseObject(a1.getString("data"), Article.class);
            list.add(jsonObject1);
            //作延时 1-3秒
            ThreadUtil.safeSleep(RandomUtil.randomInt(1, 3) * 1000);
            logger.warn(String.format("正在执行详情接口数据，【%s】，当前内容【%s】",i, jsonObject1.getArticle_title()));
        }
        m.put("文章数据", JSON.toJSONString(list));
    }


    /**
     * 具体文章数据
     */
    public HttpResponse parser(String id, String articeCookie) {
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", articeCookie);
        map.put("Host", "time.geekbang.org");
        map.put("Origin", "https://time.geekbang.org");
        map.put("Referer", "https://time.geekbang.org/column/article/" + id);
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
        map.put("Accept", "application/json, text/plain, */*");
        map.put("Accept-Encoding", "gzip, deflate, br");
        map.put("Content-Type", "application/json");

        JSONObject param = new JSONObject();
        param.put("id", id);
        param.put("include_neighbors", true);
        param.put("is_freelyread", true);

        HttpResponse resultStr = HttpRequest.post("https://time.geekbang.org/serv/v1/article")
                .addHeaders(map).body(param.toString())
                .execute();
        return resultStr;
    }


    /**
     * 调用者
     */
    public void call() {
        FileWriter r = new FileWriter(CommonConst.JIKE_TIME_FILE);
        String refererId="8369?utm_term=pc_interstitial_703";
        int cid=82;
        String articlesCookie = "_ga=GA1.2.1845252910.1600913787; LF_ID=1600913787520-2815675-4661130; GCID=b6018df-648b593-15c9163-f76f7dc; GRID=b6018df-648b593-15c9163-f76f7dc; GCESS=BQkBAQQEAC8NAAME.Ni1XwUEAAAAAAcEiHftagoEAAAAAAIE.Ni1XwsCBQAIAQMGBKqcpyoMAQEBCLUOFwAAAAAA; gksskpitn=59d943a3-6273-4225-b76c-8f38d455789c; _gid=GA1.2.342923536.1606095456; Hm_lvt_59c4ff31a9ee6263811b23eb921a5083=1605491709,1605753017,1605753086,1606095456; Hm_lvt_022f847c4e3acd44d4a2481d9187f1e6=1605753017,1605753052,1605753086,1606095456; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%221511093%22%2C%22first_id%22%3A%22175de5716e02e3-08527e39c51bc7-3323765-1327104-175de5716e135a%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E8%87%AA%E7%84%B6%E6%90%9C%E7%B4%A2%E6%B5%81%E9%87%8F%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC%22%2C%22%24latest_referrer%22%3A%22https%3A%2F%2Fwww.baidu.com%2Flink%22%2C%22%24latest_landing_page%22%3A%22https%3A%2F%2Ftime.geekbang.org%2F%22%2C%22%24latest_utm_term%22%3A%22pc_interstitial_703%22%7D%2C%22%24device_id%22%3A%22174bde5c80c2e7-085474dc682e3f-3323765-1327104-174bde5c80d840%22%7D; Hm_lpvt_022f847c4e3acd44d4a2481d9187f1e6=1606098671; Hm_lpvt_59c4ff31a9ee6263811b23eb921a5083=1606098671; _gat=1; gk_process_ev={%22count%22:11}; SERVERID=3431a294a18c59fc8f5805662e2bd51e|1606098672|1606095452";
        String articeCookie = "_ga=GA1.2.1845252910.1600913787; LF_ID=1600913787520-2815675-4661130; GCID=b6018df-648b593-15c9163-f76f7dc; GRID=b6018df-648b593-15c9163-f76f7dc; GCESS=BQkBAQQEAC8NAAME.Ni1XwUEAAAAAAcEiHftagoEAAAAAAIE.Ni1XwsCBQAIAQMGBKqcpyoMAQEBCLUOFwAAAAAA; gksskpitn=59d943a3-6273-4225-b76c-8f38d455789c; _gid=GA1.2.342923536.1606095456; Hm_lvt_59c4ff31a9ee6263811b23eb921a5083=1605491709,1605753017,1605753086,1606095456; Hm_lvt_022f847c4e3acd44d4a2481d9187f1e6=1605753017,1605753052,1605753086,1606095456; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%221511093%22%2C%22first_id%22%3A%22175de5716e02e3-08527e39c51bc7-3323765-1327104-175de5716e135a%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E8%87%AA%E7%84%B6%E6%90%9C%E7%B4%A2%E6%B5%81%E9%87%8F%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC%22%2C%22%24latest_referrer%22%3A%22https%3A%2F%2Fwww.baidu.com%2Flink%22%2C%22%24latest_landing_page%22%3A%22https%3A%2F%2Ftime.geekbang.org%2F%22%2C%22%24latest_utm_term%22%3A%22pc_interstitial_703%22%7D%2C%22%24device_id%22%3A%22174bde5c80c2e7-085474dc682e3f-3323765-1327104-174bde5c80d840%22%7D; SERVERID=3431a294a18c59fc8f5805662e2bd51e|1606098668|1606095452; Hm_lpvt_022f847c4e3acd44d4a2481d9187f1e6=1606098671; Hm_lpvt_59c4ff31a9ee6263811b23eb921a5083=1606098671; _gat=1; gk_process_ev={%22count%22:11}";
      //  parserChapter(refererId, cid);
        parserList(refererId, cid, articlesCookie, articeCookie,0);
        r.write(JSON.toJSONString(m));
    }


    public static void main(String[] args) {
        JiKeTimeParser g = new JiKeTimeParser();
        g.call();
    }

}
