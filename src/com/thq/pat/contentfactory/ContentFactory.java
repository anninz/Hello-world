package com.thq.pat.contentfactory;

import java.util.ArrayList;

import com.thq.pat.qiushi.QiuShiBaiKe;
import com.thq.pat.sina.SinaWeiboCrawler;
import com.thq.pat.sina.provider.Tweet;
import com.thq.pat.sina.provider.Tweets;

public class ContentFactory {
    private static ContentFactory instance;
    private QiuShiBaiKe qiuShiBaiKe;
    private SinaWeiboCrawler sinaWeiboCrawler;
    public static String sinaTweet = "";
    
    public static ContentFactory getInstance() {
        if(null == instance) {
            instance = new ContentFactory();
        }
        return instance;             
    }
    
    public void downloadContent() {
       qiuShiBaiKe = new QiuShiBaiKe();
       sinaWeiboCrawler = new SinaWeiboCrawler();
       new Thread(qiuShiBaiKe).start();
       new Thread(sinaWeiboCrawler).start();
       new Thread() {
           public void run() {
               synchronized (sinaTweet) {
                   Tweets.getInstance().loadFromXml();
               }
           };
       }.start();
    }

    public ArrayList<String> getQiuShiList() {
        return qiuShiBaiKe.getResult();
    }

    public ArrayList<Tweet> getSinaList() {
        return Tweets.getInstance().getTweets();
    }
}
