package com.thq.pat.sina;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;

import android.util.Log;

import com.thq.pat.contentfactory.ContentFactory;
import com.thq.pat.sina.provider.ParseXml;

/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * @filename Crawler.java
 * @version  1.0
 * @note     Main Class: Crawl html pages from sina_tweet, and save to local file,
 *                       then finally trans to txt and xml files
 * @author   DianaCody
 * @since    2014-09-27 15:23:28
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
 
public class SinaWeiboCrawler implements Runnable {
	
	/** 1.搜索页面是否存在 */
	public boolean isExistResult(String html) {
		boolean isExist = true;
		Pattern pExist = Pattern.compile("\\\\u6ca1\\\\u6709\\\\u627e\\\\u5230\\\\u76f8"
				+ "\\\\u5173\\\\u7684\\\\u5fae\\\\u535a\\\\u5462\\\\uff0c\\\\u6362\\\\u4e2a"
				+ "\\\\u5173\\\\u952e\\\\u8bcd\\\\u8bd5\\\\u5427\\\\uff01");//没有找到相关的微博呢，换个关键词试试吧！
		Matcher mExist = pExist.matcher(html);
		if(mExist.find()) {
			isExist = false;
		}
		return isExist;
	}

	public void doCrawl() throws ClientProtocolException, URISyntaxException, IOException, InterruptedException {
	    synchronized (ContentFactory.sinaTweet) {
	        long starttime = System.currentTimeMillis();

	        //no need to Crawl,if downloaded in a hour.
	        ArrayList<File> fileList = new ArrayList<File>();
	        ParseXml.getFiles(fileList, Utils.myPath + "/tweetxml/");
	        Log.i("THQ starttime", "starttime = " + starttime 
	                + " lasttime = " + (fileList.size() > 0?(fileList.get(0).lastModified()):"0")
	                );
	        if (fileList.size() > 0 && (fileList.get(0).lastModified() + 3600000) > starttime) { 
                ContentFactory.sinaTweet.notifyAll();//notify Tweets to load XML.
	            return;
	        }

	        String[] searchwords = {"冷笑话", "头条", "娱乐", "科技新闻", "秒拍视频"
	                /*"samsung", "iPhone6", "htc", "huawei", "xiaomi", "zte", "lenovo", "mx", "coolpad",
				"google", "IBM", "Microsoft", "Amazon", "Intel", "Apple"*/};
	        File dirGetTweetSub = new File(Utils.myPath + "/tweethtml/");
	        dirGetTweetSub.mkdirs();
	        File dirGetTweetTxtSub = new File(Utils.myPath + "/tweettxt/");
	        dirGetTweetTxtSub.mkdirs();
	        File dirGetTweetXmlSub = new File(Utils.myPath + "/tweetxml/");
	        dirGetTweetXmlSub.mkdirs();
	        for(int n=0; n<searchwords.length; n++) {
	            String searchword = searchwords[n];
	            String dirPath = Utils.myPath + "/tweethtml/" + searchword;
	            File f = new File(dirPath);
	            f.mkdirs();
	            int totalPage = 1;
	            System.out.println("Start to download html pages of the topic: " + searchword);
	            String html;
	            for(int i=totalPage; i>0; i--) {
	                html = new LoadHTML().downloadPage("http://s.weibo.com/weibo/" + searchword + "&Refer=STopic_box");
	                int iReconn = 0;
	                while("null".equals(html)) {
	                    html = new LoadHTML().downloadPage("http://s.weibo.com/weibo/" + searchword + "&Refer=STopic_box");
	                    iReconn ++;
	                    //					System.out.println(ip.get(iIP) + " reconnected" + iReconn + " times.");
	                    //connnect over 4 times, then break.
	                    if(iReconn == 4) {
	                        break;
	                    }
	                }
	                if("null".equals(html)) {
	                    /*					System.out.println("Failed 3 times, now trying a new IP from IPrepo...");
					if(iIP == ipNum-1) {
						System.out.println("All valid proxy IPs have been tried, still cannot get all data, now trying a valid proxy IP list again...");
						iIP = 0;
						System.out.println("IP: " + ip.get(iIP) + ", start connecting...");
					}
					else {
						iIP ++;
						System.out.println("IP: " + ip.get(iIP) + ", start connecting...");
					}*/
	                    i ++;
	                }

	                //				Log.e("THQ html", html);
	                //				FileWR.writeString(html, dirPath + "/" + searchword + i + ".html");
	                if (!isExistResult(html)) break;
	                FileWR.writeFile(html, dirPath + "/" + searchword + i + ".html");
	            }
	            //			System.out.println("topic \"" + searchword + "\"crawling has been done!****");
	            //			System.out.println("Begin writing the tweets to local files: txt & xml");
	            			Log.i("THQ", "topic \"" + searchword + "\"crawling has been done!****");
	            			Log.i("THQ", "Begin writing the tweets to local files: txt & xml");
	            String saveTXTPath = Utils.myPath + "/tweettxt/" + searchword + ".txt";
	            HTMLParser htmlParser = new HTMLParser();
	            Vector<String> tweets = htmlParser.write2txt(searchword, dirPath, saveTXTPath);

	            String saveXMLPath = Utils.myPath + "/tweetxml/" + "/" + searchword + ".xml";
	            htmlParser.writeVector2xml(tweets, saveXMLPath);
	            //			htmlParser.createSinaXML(tweets, saveXMLPath);
//	            System.out.println("Save to txt & xml files succeed.");
	            Log.i("THQ", "Save to txt & xml files succeed.");

	            long endtime = System.currentTimeMillis();
	            //			System.out.println((double)(endtime-starttime)/60000 + "mins");
	            Log.i("THQ",(double)(endtime-starttime)/60000 + "mins");
	            
	            ContentFactory.sinaTweet.notifyAll();//notify Tweets to load XML.
	        }
	    }
//		System.out.println("----end------");
	}

    @Override
    public void run() {
        try {
            doCrawl();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}