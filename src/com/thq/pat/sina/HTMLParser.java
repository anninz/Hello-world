package com.thq.pat.sina;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * @filename HTMLParser.java
 * @version  1.0
 * @note     Parse HTML pages, and write result to txt file and xml file with dom4j
 * @author   DianaCody
 * @since    2014-09-27 15:23:28
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
public class HTMLParser {
    public Vector<String> splitHTML(String html) {
        Vector<String> pieces = new Vector<String>();
//      Pattern p = Pattern.compile("<dl class=\"feed_list\".+?<dd class=\"clear\">");
//      Pattern p = Pattern.compile("<dl class=\\\\\"feed_list.+?class=\\\\\"WB_feed_repeat S_bg1\\\\\"");
        Pattern p = Pattern.compile(">举报<.+?<p class=\"comment_txt.+?<div class=\"WB_screen W_fr");
        Matcher m = p.matcher(html);
//      System.out.println(m.find());
        while(m.find()) {
/*          try {
                String s = m.group();
                String s1 = URLDecoder.decode(s, "utf-8");
                String s2 =  new String(s.getBytes("iso-8859-1"),"gbk");;
                System.out.println(s2);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/
//          System.out.println(m.group());
            pieces.add(m.group());
        }
        return pieces;
    }
    
    public String parse(String html) {
        String s = "";
        Document doc = Jsoup.parse(html);
//      Elements userNames = doc.select("dt[class].face > a");
        Elements userids = doc.select("span > a[action-data]");
        Elements dates = doc.select("a[date]");
//      Elements tweetids = doc.select("dl[mid]");
        Elements tweetids = doc.select("[action-type=feed_list_item]");
//      Elements tweets = doc.select("p > em");
        Elements tweets = doc.select("p.comment_txt");
//      Elements forwardNums = doc.select("a:contains(转发)");
        Elements forwardNums = doc.select("span:contains(转发)");
//      Elements commentNums = doc.select("a:contains(评论)");
        Elements commentNums = doc.select("span:contains(评论)");
        Elements miaopaiLink = doc.select("li.WB_video");//added by hongqi
        Elements picLink = doc.select("img.bigcursor");//added by hongqi
//      for(Element userName : userNames) {
        for(Element userName : tweets) {
            String attr = userName.attr("nick-name");
            s += "<userName> " + attr + " </userName>";
        }
        if (!s.contains("<userName>")) {
            s += "<userName> " + "null" + " </userName>";
        }
        for(Element userid : userids) {
            String attr = userid.attr("action-data");
            int index = attr.indexOf("uid=");
            attr = attr.substring(index>0?index:0);
            Pattern p = Pattern.compile("[0-9]+");
            Matcher m = p.matcher(attr);
            if(m.find()) {
                attr = m.group();
            }
            s += "<userid> " + attr + " </userid>";
        }
        if (!s.contains("<userid>")) {
            s += "<userid> " + "null" + " </userid>";
        }
        for(Element date : dates) {
            String attr = date.text();
            s += "<date> " + attr + " </date>";
        }
        if (!s.contains("<date>")) {
            s += "<date> " + "null" + " </date>";
        }
        for(Element tweetid : tweetids) {
            String attr = tweetid.attr("mid");
            s += "<tweetid> " + attr + " </tweetid>";
        }
        if (!s.contains("<tweetid>")) {
            s += "<tweetid> " + "null" + " </tweetid>";
        }
        for(Element tweet : tweets) {
            String attr = tweet.text();
            s += "<tweetContent> " + attr + " </tweetContent>";
        }
        if (!s.contains("<tweetContent>")) {
            s += "<tweetContent> " + "null" + " </tweetContent>";
        }
        for(Element forwardNum : forwardNums) {
            String attr = forwardNum.text();
            if(attr.equals("转发")) {
                attr = "0";
            }
            else {
/*              if(!attr.contains("转发(")) {
                    attr = "0";
                }
                else {
                    attr = attr.substring(attr.indexOf("转发(")+3, attr.indexOf(")"));
                }*/
                attr = attr.substring(attr.indexOf("转发")+2);
            }
//          System.out.println(attr);
            s += "<forwardNum> " + attr + " </forwardNum>";
        }
        if (!s.contains("<forwardNum>")) {
            s += "<forwardNum> " + "null" + " </forwardNum>";
        }
        for(Element commentNum : commentNums) {
            String attr = commentNum.text();
            if(attr.equals("评论")) {
                attr = "0";
            }
            else {
/*              if(!attr.contains("评论(")) {
                    attr = "0";
                }
                else {
                    attr = attr.substring(attr.indexOf("评论(")+3, attr.indexOf(""));
                }*/
                attr = attr.substring(attr.indexOf("评论")+2);
            }
//          System.out.println(attr);
            s += "<commentNum> " + attr + " </commentNum>";
        }
        if (!s.contains("<commentNum>")) {
            s += "<commentNum> " + "null" + " </commentNum>";
        }
        
        //add by hongqi begin
        for(Element link : miaopaiLink) {
            String attr = link.attr("action-data");
            if (attr != null) {
                attr = attr.substring(attr.indexOf("short_url")+10, attr.lastIndexOf("&"));
//              System.out.println("--" + attr);
            }
            s += "<miaopaiLink> " + attr + " </miaopaiLink>";
        }
        if (!s.contains("<miaopaiLink>")) {
            s += "<miaopaiLink> " + "null" + " </miaopaiLink>";
        }
        
        //add by hongqi begin
        for(Element link : picLink) {
            String attr = link.attr("src");
            s += "<picLink> " + attr + " </picLink>";
        }
        if (!s.contains("<picLink>")) {
            s += "<picLink> " + "null" + " </picLink>";
        }
        
//        Log.e("THQ", s);
//        System.out.println("--" + s);
        return s;
    }
    
    public Vector<String> write2txt(String searchword, String dirPath, String saveTXTPath) throws IOException {
        Vector<String> tweets = new Vector<String>();
        String onePiece;
        File f = new File(saveTXTPath); //建立一个空文件
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        //dirPath = "e:/tweet/tweettxt/";
        for(int page=0; page<50; page++) {
            String path = dirPath + "/" + searchword + page + ".html";
            File ff = new File(path);
            if(ff.exists()) {
                String html = FileWR.html2String(path);
                Vector<String> pieces = new HTMLParser().splitHTML(html);
                for(int i=0; i<pieces.size(); i++) {
                    onePiece = pieces.get(i);
                    if(onePiece.contains("feed_list_forwardContent")) {
                        Pattern p = Pattern.compile("feed_list_forwadContent.+?<p class=\"info W_linkb W_textb");
                        Matcher m = p.matcher(onePiece);
                        if(m.find()) {
                            onePiece = onePiece.replace(m.group(), "");
                        }
                    }
                    String s = new HTMLParser().parse(onePiece);
                    tweets.add(s);
                    bw.write(s+"\r\n"); //每次写完一条之后要换行!
                }
            }
        }
        bw.close();
        return tweets;
    }
    
    public void writeVector2xml(Vector<String> vector, String saveXMLPath) throws IOException {
        int vectorSize = vector.size();
        String oneIniTweet;
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("GB2312"); //xml被识别格式仅为gb2312,默认utf8不被识别
        File f = new File(saveXMLPath);
        f.createNewFile(); //建立一个空xml文件
        FileWriter fw = new FileWriter(f);
        org.dom4j.Document document = DocumentHelper.createDocument();
        org.dom4j.Element rootElement = document.addElement("tweets"); //根节点tweets
        rootElement.addAttribute("totalNumber", String.valueOf(vectorSize)); //设置属性:总条目数
        for(int j=0; j<vectorSize; j++) {
            oneIniTweet = vector.get(j);
            String userName = oneIniTweet.substring(oneIniTweet.indexOf("<userName> ")+11, oneIniTweet.indexOf(" </userName>"));
            String userid = oneIniTweet.substring(oneIniTweet.indexOf("<userid> ")+9, oneIniTweet.indexOf(" </userid>"));
            String date = oneIniTweet.substring(oneIniTweet.indexOf("<date> ")+7, oneIniTweet.indexOf(" </date>"));
            String tweetid = oneIniTweet.substring(oneIniTweet.indexOf("<tweetid> ")+10, oneIniTweet.indexOf(" </tweetid>"));
            String forwardNum = oneIniTweet.substring(oneIniTweet.indexOf("<forwardNum> ")+13, oneIniTweet.indexOf(" </forwardNum>"));
            String commentNum = oneIniTweet.substring(oneIniTweet.indexOf("<commentNum> ")+13, oneIniTweet.indexOf(" </commentNum>"));
            String miaopaiLink = oneIniTweet.substring(oneIniTweet.indexOf("<miaopaiLink> ")+14, oneIniTweet.indexOf(" </miaopaiLink>"));
            String picLink = oneIniTweet.substring(oneIniTweet.indexOf("<picLink> ")+10, oneIniTweet.indexOf(" </picLink>"));
            String tweetContent = oneIniTweet.substring(oneIniTweet.indexOf("<tweetContent> ")+15, oneIniTweet.indexOf(" </tweetContent>"));
            org.dom4j.Element tweetElement = rootElement.addElement("tweet");
            tweetElement.addAttribute("userName", userName);
            tweetElement.addAttribute("userid", userid);
            tweetElement.addAttribute("date", date);
            tweetElement.addAttribute("tweetid", tweetid);
            tweetElement.addAttribute("forwardNum", forwardNum);
            tweetElement.addAttribute("commentNum", commentNum);
            tweetElement.addAttribute("picLink", picLink);
            tweetElement.addAttribute("miaopaiLink", miaopaiLink);
            tweetElement.setText(tweetContent); // 设置节点文本内容
        }
        XMLWriter xw = new XMLWriter(fw/*, format*/);
        xw.write(document);
        xw.close();
    }
    
/*    public boolean createSinaXML(Vector<String> vector, String saveXMLPath) {
        int vectorSize = vector.size();
        String oneIniTweet;
        
        boolean bFlag = false;
        FileOutputStream fileos = null;

        File newXmlFile = new File(saveXMLPath);
        try {
            if (newXmlFile.exists()) {
                bFlag = newXmlFile.delete();
            } else {
                bFlag = true;
            }

            if (bFlag) {

                if (newXmlFile.createNewFile()) {
                    fileos = new FileOutputStream(newXmlFile);

                    // we create a XmlSerializer in order to write xml data
                    XmlSerializer serializer = Xml.newSerializer();

                    // we set the FileOutputStream as output for the serializer,
                    // using UTF-8 encoding
                    serializer.setOutput(fileos, "UTF-8");

                    // <?xml version=”1.0″ encoding=”UTF-8″>
                    // Write <?xml declaration with encoding (if encoding not
                    // null) and standalone flag (if stan dalone not null)
                    // This method can only be called just after setOutput.
                    serializer.startDocument("UTF-8", null);

                    // start a tag called "root"
                    serializer.startTag(null, "tweets");
                    for (int j = 0; j< vectorSize; j++) {
                        oneIniTweet = vector.get(j);
                        String userName = oneIniTweet.substring(oneIniTweet.indexOf("<userName> ")+11, oneIniTweet.indexOf(" </userName>"));
                        String userid = oneIniTweet.substring(oneIniTweet.indexOf("<userid> ")+9, oneIniTweet.indexOf(" </userid>"));
                        String date = oneIniTweet.substring(oneIniTweet.indexOf("<date> ")+7, oneIniTweet.indexOf(" </date>"));
                        String tweetid = oneIniTweet.substring(oneIniTweet.indexOf("<tweetid> ")+10, oneIniTweet.indexOf(" </tweetid>"));
                        String forwardNum = oneIniTweet.substring(oneIniTweet.indexOf("<forwardNum> ")+13, oneIniTweet.indexOf(" </forwardNum>"));
                        String commentNum = oneIniTweet.substring(oneIniTweet.indexOf("<commentNum> ")+13, oneIniTweet.indexOf(" </commentNum>"));
                        String miaopaiLink = oneIniTweet.substring(oneIniTweet.indexOf("<miaopaiLink> ")+14, oneIniTweet.indexOf(" </miaopaiLink>"));
                        String picLink = oneIniTweet.substring(oneIniTweet.indexOf("<picLink> ")+10, oneIniTweet.indexOf(" </picLink>"));
                        String tweetContent = oneIniTweet.substring(oneIniTweet.indexOf("<tweetContent> ")+15, oneIniTweet.indexOf(" </tweetContent>"));
                        serializer.startTag(null, "tweet");
                        serializer.attribute(null, "userName", userName);
                        serializer.attribute(null, "userid", userid);
                        serializer.attribute(null, "date", date);
                        serializer.attribute(null, "tweetid", tweetid);
                        serializer.attribute(null, "forwardNum", forwardNum);
                        serializer.attribute(null, "commentNum", commentNum);
                        serializer.attribute(null, "picLink", picLink);
                        serializer.attribute(null, "miaopaiLink", miaopaiLink);
                        serializer.comment(tweetContent); // 设置节点文本内容
                    }
                    serializer.endTag(null, "tweets");
                    serializer.endDocument();

                    // write xml data into the FileOutputStream
                    serializer.flush();
                    // finally we close the file stream
                    fileos.close();
                }
            }
        } catch (Exception e) {
        }
        return bFlag;
    }*/
    
}