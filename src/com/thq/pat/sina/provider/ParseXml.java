package com.thq.pat.sina.provider;   
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.util.Log;

/*import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;*/

/**  
 *   
 * @author hongliang.dinghl  
 * Dom4j generater and parse XML  
 */  
public class ParseXml {   

    
    public static void parserSinaXml(ArrayList<Tweet> tweets, String fileName) {   
        File inputXml=new File(fileName);   
//        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        SAXReader saxReader = new SAXReader();      
        Element root;
        try {
            Document document = saxReader.read(inputXml);
            root = document.getRootElement();   
            for(Iterator i = root .elementIterator(); i.hasNext();){   
                Element tweet = (Element) i.next();
                String userName = tweet.attributeValue("userName");
                String userid = tweet.attributeValue("userid");
                String date = tweet.attributeValue("date");
                String tweetid = tweet.attributeValue("tweetid");
                String forwardNum = tweet.attributeValue("forwardNum");
                String commentNum = tweet.attributeValue("commentNum");
                String picLink = tweet.attributeValue("picLink");
                String miaopaiLink = tweet.attributeValue("miaopaiLink");
                String tweetContent = tweet.getText();
                Tweet tweet2 = new Tweet(userName, userid, date, tweetid, forwardNum, commentNum, miaopaiLink, picLink, tweetContent);
                tweets.add(tweet2);
            }   
        } catch (DocumentException e) {   
            Log.e("THQ",e.getMessage());   
        }
//        return tweets;
    }
    
    public static void parserSinaXmls(ArrayList<Tweet> tweets, String xmlDir) {

//        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        try {
            ArrayList<File> xmlFiles = new ArrayList<File>();
            getFiles(xmlFiles, xmlDir);
            for (int i = 0; i < xmlFiles.size(); i++) {
                File nextFile = xmlFiles.get(i);
                parserSinaXml(tweets, nextFile.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tweets.size() > 0) {
            Log.i("THQ", "parserSinaXmls  success!");
        } else {
            Log.i("THQ", "parserSinaXmls  fail!");
        }
//        return tweets;
    }

    public static void getFiles(ArrayList<File> fileList, String path) {
        File dir = new File(path);
        if (!dir.exists()) return;
        File[] allFiles = dir.listFiles();
        for (int i = 0; i < allFiles.length; i++) {
            File file = allFiles[i];
            if (file.isFile()) {
                fileList.add(file);
            } else if (!file.getAbsolutePath().contains(".thumnail")) {
                getFiles(fileList, file.getAbsolutePath());
            }
        }
    }

    //获取全部河流数据 


    /*    *//**   
     * 参数fileName：为xml文档路径
     *//*
    public static List<Tweet> parserSinaXml(String filename) {
        List<Tweet> tweets = new ArrayList<Tweet>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  //取得DocumentBuilderFactory实例
        DocumentBuilder builder;
        Document doc = null;

        try {
            InputStream inputStream = new FileInputStream(filename);
            builder = factory.newDocumentBuilder();
            doc = builder.parse(inputStream);   //解析输入流 得到Document实例
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (doc == null) {
            return null;
        }

        //从factory获取DocumentBuilder实例
        Element rootElement = doc.getDocumentElement();
        NodeList items = rootElement.getElementsByTagName("tweets");
        for (int i = 0; i < items.getLength(); i++) {
            Node item = items.item(i);
            NamedNodeMap attr = item.getAttributes();
            String userName = attr.getNamedItem("name").getNodeValue();
            String userid = attr.getNamedItem("userid").getNodeValue();
            String date = attr.getNamedItem("date").getNodeValue();
            String tweetid = attr.getNamedItem("tweetid").getNodeValue();
            String forwardNum = attr.getNamedItem("forwardNum").getNodeValue();
            String commentNum = attr.getNamedItem("commentNum").getNodeValue();
            String miaopaiLink = attr.getNamedItem("picLink").getNodeValue();
            String picLink = attr.getNamedItem("miaopaiLink").getNodeValue();
            String tweetContent = item.getNodeValue();
            Tweet tweet = new Tweet(userName, userid, date, tweetid, forwardNum, commentNum, miaopaiLink, picLink, tweetContent);
            tweets.add(tweet);
        }
        return tweets;
    }


    public boolean createSinaXML(List<Tweet> data, String localDir) {
        boolean bFlag = false;
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String strTmpName = sDateFormat.format(new java.util.Date()) + ".xml";
        FileOutputStream fileos = null;

        File newXmlFile = new File(localDir + strTmpName);
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
                    serializer.startTag(null, "root");
                    serializer.startTag(null, "Test");
                    for (Tweet ad : data) {
                        serializer.startTag(null, "TestObject");
                        serializer.attribute(null, "ADID", ad.);
                        serializer.attribute(null, "Name", ad.getName());
                        serializer.attribute(null, "URL", ad.getUrl());
                        serializer.endTag(null, "TestObject");                      
                    }
                    serializer.endTag(null, "Test");
                    serializer.endTag(null, "root");
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