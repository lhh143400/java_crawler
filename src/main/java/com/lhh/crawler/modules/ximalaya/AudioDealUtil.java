package com.lhh.crawler.modules.ximalaya;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * @author temt
 * 音频处理工具类
 */
public class AudioDealUtil {

    /**
     * 初始化音频列表
     * @param url
     * @param param
     * @return
     */
    public static AudioBean initBean(String url,String param){
        AudioBean audioBean = new AudioBean();

        //获取数据 
        String result = HttpUtil.sendGet(url,param);
        JSONObject resultJson = JSONObject.parseObject(result);
        JSONObject dataJson = resultJson.getJSONObject("data");
        String albumId = dataJson.getString("albumId");
        JSONObject mainInfoJson = dataJson.getJSONObject("mainInfo");

        //获取专辑信息
        JSONObject tracksInfoJson = dataJson.getJSONObject("tracksInfo");
        int pageNum = tracksInfoJson.getInteger("pageNum");
        int pageSize = tracksInfoJson.getInteger("pageSize");
        int trackTotalCount = tracksInfoJson.getInteger("trackTotalCount");

        //专辑名称
        String albumTitle = mainInfoJson.getString("albumTitle");

        //总共页码
        int totalPage = trackTotalCount/pageSize + 1;

        //封装对象
        audioBean.setTrackTotalCount(trackTotalCount);
        audioBean.setAlbumId(albumId);
        audioBean.setPageNum(pageNum);
        audioBean.setTotalPage(totalPage);
        audioBean.setPageSize(pageSize);
        audioBean.setAlbumTitle(albumTitle);

        return audioBean;
    }

    /**
     * 初始化音频下载列表数据
     * @param url
     * @param audioBean
     * @throws InterruptedException 
     */
    public static void initDownloadAudio(String url,AudioBean audioBean,String targetUrl) throws InterruptedException{

        int trackTotalCount = audioBean.getTrackTotalCount();
        int count = 0;
        for(int j = 0 ; j < audioBean.getTotalPage() ; j++){
            //参数
            String param = "albumId="+audioBean.getAlbumId()+"&pageNum="+(j+1)+"&sort=-1";
            String result = HttpUtil.sendGet(url,param);

            JSONObject resultJson = JSONObject.parseObject(result);
            JSONObject dataJson = resultJson.getJSONObject("data");

            JSONArray tracksAudioPlayJSONArray = dataJson.getJSONArray("tracks");
            for(int i = 0 ; i < tracksAudioPlayJSONArray.size() ; i++){
                JSONObject object = tracksAudioPlayJSONArray.getJSONObject(i);
                String trackName = object.getString("title");
                String trackId = object.getString("trackId");
                String audioFinal = HttpUtil.sendGet("https://www.ximalaya.com/revision/play/v1/audio","id="+trackId+"&ptype=1");
                JSONObject audioFinalJson = JSONObject.parseObject(audioFinal).getJSONObject("data");
                String downloadUrl = audioFinalJson.getString("src");

//				Thread.sleep(100L);

                downloadAudio(downloadUrl,targetUrl,trackName,audioBean.getAlbumTitle());
                count++;
                System.out.println("还剩"+(trackTotalCount-count)+"条");
            }
        }

    }

    /**
     * 下载音频
     * @param downloadUrl
     * @param targetUrl
     * @param fileName
     */
    public static void downloadAudio(String downloadUrl,String targetUrl,String fileName,String albumTitle){

        try {
        	fileName = dealFileName(fileName);
            String str = targetUrl+albumTitle+"//"+fileName+".mp3";

            File file = new File(targetUrl+albumTitle);
            if(!file.exists()){
                file.mkdirs();
            }
            // 创建连接、输入流
            InputStream in = new URL(downloadUrl).openConnection().getInputStream();

            FileOutputStream f = new FileOutputStream(str);
            byte[] bufferByte = new byte[1024]; // 接收缓存
            int len;
            while ((len = in.read(bufferByte)) > 0) {
                f.write(bufferByte, 0, len);
            }
            f.close();
            in.close();
            System.out.println(str + "****** success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 特殊字符处理
     * @time 2019年12月29日14:26:32
     * @param fileName
     * @return
     */
    private static String dealFileName(String fileName){
    	//\/:*?<>|
    	String regEx="[/\\/:*?<>|]";
    	
    	fileName = fileName.replaceAll(regEx,"");//不想保留原来的字符串可以直接写成 “str = str.replaceAll(regEX,aa);”
    	return fileName;
    }
}
