package com.lhh.crawler.modules.lovewords.parser;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 描述：土味情话
 * https://works.yangerxiao.com/honeyed-words-generator/
 *
 * @author lhh
 * @Date 2020/9/30 15:58
 */
public class LoveWordsParser {
    private Logger logger = LoggerFactory.getLogger(LoveWordsParser.class);

    /**
     * 获取土味情话数据
     */
    public void parser() {
        Map<String, String> headers = Maps.newHashMap();

        headers.put("authority", "g.yangerxiao.com");
        headers.put("path", "/v1/graphql");
        headers.put("scheme", "https");
        headers.put("accept", "*/*");
        headers.put("accept-encoding", "gzip, deflate, br");
        headers.put("accept-language", "zh-CN,zh;q=0.9");
        headers.put("content-length", "177");
        headers.put("content-type", "application/json");
        headers.put("origin", "https://works.yangerxiao.com");
        headers.put("sec-fetch-dest", "empty");
        headers.put("sec-fetch-mode", "cors");
        headers.put("sec-fetch-site", "same-site");
        headers.put("x-hasura-admin-secret", "yanggc@h");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");

        JSONObject param = new JSONObject();
        param.put("operationName", "LoveWords");
        param.put("query", "query LoveWords{  love_words(where: {draft: {_eq: false}}) {id content remark __typename }}");

        TimeInterval timer = DateUtil.timer();
        String url = null;
        HttpResponse resultStr = null;
        try {
            url = String.format("https://g.yangerxiao.com/v1/graphql");
            resultStr = HttpRequest.post(url)
                    .addHeaders(headers)
                    .body(param.toString())
                    .execute();

            System.out.println(resultStr.body());


        } catch (Exception e) {
            logger.error(String.format("【获取土味情话失败，URL：【%s】 ||| 返回值：【%s】】", url, resultStr), e);
        }
        logger.warn(String.format("当前进度：【%s】，耗时【%s】", url, timer.interval()));

    }

    public static void main(String[] args) {
        LoveWordsParser t = new LoveWordsParser();
        t.parser();
    }
}
