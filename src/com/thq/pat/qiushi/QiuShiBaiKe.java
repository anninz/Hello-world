package com.thq.pat.qiushi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

//搜索Web爬行者
public class QiuShiBaiKe implements Runnable{

    /* disallowListCache缓存robot不允许搜索的URL。 Robot协议在Web站点的根目录下设置一个robots.txt文件,
     *规定站点上的哪些页面是限制搜索的。 搜索程序应该在搜索过程中跳过这些区域,下面是robots.txt的一个例子:
# robots.txt for http://somehost.com/
User-agent: *
Disallow: /cgi-bin/
Disallow: /registration # /Disallow robots on registration page
Disallow: /login
     */


    private HashMap< String,ArrayList< String>> disallowListCache = new HashMap< String,ArrayList< String>>(); 
    ArrayList< String> errorList = new ArrayList< String>();//错误信息 
    ArrayList< String> result = new ArrayList< String>(); //搜索到的结果 
    String startUrl;//开始搜索的起点
    int mPage;//开始搜索的页面
    String searchString;//要搜索的字符串(英文)
    boolean caseSensitive=false;//是否区分大小写
    boolean limitHost=false;//是否在限制的主机内搜索

    public QiuShiBaiKe(String url,int page){
        this.startUrl=url;
    }
    public QiuShiBaiKe(){
        this.startUrl="http://www.qiushibaike.com/hot/page/";
    }

    public ArrayList< String> getResult(){
        return result;
    }

    public void run(){//启动搜索线程
        crawl(startUrl, mPage);
    }


    //检测URL格式
    private URL verifyUrl(String url) {
        // 只处理HTTP URLs.
        if (!url.toLowerCase().startsWith("http://"))
            return null;

        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(url);
        } catch (Exception e) {
            return null;
        }

        return verifiedUrl;
    }

    private String downloadPage(URL pageUrl) {
        try {

            //      URL newUrl = new URL(pageUrl);
            HttpURLConnection hConnect = (HttpURLConnection) pageUrl.openConnection();
            //      hConnect.setDefaultRequestProperty("http.agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT");
            hConnect.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT");

            // 读取内容

            InputStreamReader mInputStreamReader = new InputStreamReader(
                    hConnect.getInputStream());
            BufferedReader reader = new BufferedReader(mInputStreamReader);

            // Read page into buffer.
            String line;
            StringBuffer pageBuffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                pageBuffer.append(line);
            }

            reader.close();
            mInputStreamReader.close();
            hConnect.disconnect();

            return pageBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 从URL中去掉"www"
    private String removeWwwFromUrl(String url) {
        int index = url.indexOf("://www.");
        if (index != -1) {
            return url.substring(0, index + 3) +
                    url.substring(index + 7);
        }

        return (url);
    }

    // 解析页面并找出链接
    private ArrayList< String> retrieveContents(String pageContents) {
        // 用正则表达式编译链接的匹配模式。
        Pattern p =Pattern.compile("<div.*?author clearfix\">.*?<a.*?<img.*?<h2>(.*?)</h2>.*?<div.*?"+
                "content\">(.*?)</div>(.*?)<div class=\"stats.*?class=\"number\">(.*?)</i>",Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(pageContents);

        // System.out.println(pageContents);//hongqi

        ArrayList< String> linkList = new ArrayList< String>();
        while (m.find()) {
            String link1 = m.group(1).trim();
            String link2 = m.group(2).trim();
            String link3 = m.group(3).trim();
            String link4 = m.group(4).trim();
            String content = " 作者： " + link1 + " \n\n " + link2 + " \n 赞：" + link4 + "\n\n" + " 来源：糗事百科";
//            System.out.println(content);

            linkList.add(content);
        }
        return linkList;
    }

/*    // 搜索下载Web页面的内容，判断在该页面内有没有指定的搜索字符串
    private boolean searchStringMatches(String pageContents, String searchString, boolean caseSensitive) {
        String searchContents = pageContents; 
        if (!caseSensitive) {//如果不区分大小写
            searchContents = pageContents.toLowerCase();
        }


        Pattern p = Pattern.compile("[\\s]+");
        String[] terms = p.split(searchString);
        for (int i = 0; i < terms.length; i++) {
            if (caseSensitive) {
                if (searchContents.indexOf(terms[i]) == -1) {
                    return false;
                }
            } else {
                if (searchContents.indexOf(terms[i].toLowerCase()) == -1) {
                    return false;
                }
            }     }

        return true;
    }*/


    //执行实际的搜索操作
    public ArrayList< String> crawl(String startUrl, int page) { 

        int pages = 1;
        System.out.println("searchString="+searchString);
        HashSet< String> crawledList = new HashSet< String>();

        for (int i = 0; i < pages; i++) {
            // 从开始URL中移出www
            startUrl = removeWwwFromUrl(startUrl + page + 1);
            // Convert string url to URL object.
            URL verifiedUrl = verifyUrl(startUrl);
            
            String pageContents = downloadPage(verifiedUrl);
           
            if (pageContents != null && pageContents.length() > 0){
                Log.d("THQ", "" + pageContents.length());
                // 从页面中获取有效的链接
                ArrayList< String> links =retrieveContents(pageContents);
                result.addAll(links);
            }
        }
        return result;
    }
}
