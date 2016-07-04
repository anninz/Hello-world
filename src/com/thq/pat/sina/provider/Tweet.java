package com.thq.pat.sina.provider;

public class Tweet {

    private String userName;
    private String userid;
    private String date;
    private String tweetid;
    private String forwardNum;
    private String commentNum;
    private String miaopaiLink;
    private String picLink;
    private String tweetContent;

    public Tweet(String userName, String userid, String date, String tweetid, String forwardNum, String commentNum,
            String miaopaiLink, String picLink, String tweetContent) {
        super();
        this.userName = userName;
        this.userid = userid;
        this.date = date;
        this.tweetid = tweetid;
        this.forwardNum = forwardNum;
        this.commentNum = commentNum;
        this.miaopaiLink = miaopaiLink;
        this.picLink = picLink;
        this.tweetContent = tweetContent;
    }

    public Tweet() {
        super();
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTweetid() {
        return tweetid;
    }
    public void setTweetid(String tweetid) {
        this.tweetid = tweetid;
    }
    public String getForwardNum() {
        return forwardNum;
    }
    public void setForwardNum(String forwardNum) {
        this.forwardNum = forwardNum;
    }
    public String getCommentNum() {
        return commentNum;
    }
    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }
    public String getMiaopaiLink() {
        return miaopaiLink;
    }
    public void setMiaopaiLink(String miaopaiLink) {
        this.miaopaiLink = miaopaiLink;
    }
    public String getPicLink() {
        return picLink;
    }
    public void setPicLink(String picLink) {
        this.picLink = picLink;
    }
    public String getTweetContent() {
        return tweetContent;
    }
    public void setTweetContent(String tweetContent) {
        this.tweetContent = tweetContent;
    }


}
