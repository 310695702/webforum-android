package com.kcbs.webforum.pojo;

import java.io.Serializable;

public class User implements Serializable {


    /**
     * userId : 4
     * username : hyx
     * password : null
     * email :
     * personalizedSignature : 你好呀
     * role : 1
     * createTime : 2020-12-31 12:09:54
     * updateTime : 2021-02-28 10:45:50
     * headSculpture : http://47.111.9.152:8088/images/56674305-98b3-4be4-938b-c7a2747cb23b.jpeg
     * isBan : 0
     * startTime : 2021-01-30 00:55:13
     * endTime : 2021-01-30 00:55:18
     * banMessage : ..
     * subscribeStatus : 1
     * school : null
     * sex : 0
     * personalWebsite : null
     * wechat : null
     * qq : null
     * wordNumber : 534
     */

    private int userId;
    private String username;
    private Object password;
    private String email;
    private String personalizedSignature;
    private int role;
    private String createTime;
    private String updateTime;
    private String headSculpture;
    private int isBan;
    private String startTime;
    private String endTime;
    private String banMessage;
    private int subscribeStatus;
    private Object school;
    private int sex;
    private Object personalWebsite;
    private Object wechat;
    private Object qq;
    private int wordNumber;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Object getPassword() {
        return password;
    }

    public void setPassword(Object password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonalizedSignature() {
        return personalizedSignature;
    }

    public void setPersonalizedSignature(String personalizedSignature) {
        this.personalizedSignature = personalizedSignature;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getHeadSculpture() {
        return headSculpture;
    }

    public void setHeadSculpture(String headSculpture) {
        this.headSculpture = headSculpture;
    }

    public int getIsBan() {
        return isBan;
    }

    public void setIsBan(int isBan) {
        this.isBan = isBan;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBanMessage() {
        return banMessage;
    }

    public void setBanMessage(String banMessage) {
        this.banMessage = banMessage;
    }

    public int getSubscribeStatus() {
        return subscribeStatus;
    }

    public void setSubscribeStatus(int subscribeStatus) {
        this.subscribeStatus = subscribeStatus;
    }

    public Object getSchool() {
        return school;
    }

    public void setSchool(Object school) {
        this.school = school;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public Object getPersonalWebsite() {
        return personalWebsite;
    }

    public void setPersonalWebsite(Object personalWebsite) {
        this.personalWebsite = personalWebsite;
    }

    public Object getWechat() {
        return wechat;
    }

    public void setWechat(Object wechat) {
        this.wechat = wechat;
    }

    public Object getQq() {
        return qq;
    }

    public void setQq(Object qq) {
        this.qq = qq;
    }

    public int getWordNumber() {
        return wordNumber;
    }

    public void setWordNumber(int wordNumber) {
        this.wordNumber = wordNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password=" + password +
                ", email='" + email + '\'' +
                ", personalizedSignature='" + personalizedSignature + '\'' +
                ", role=" + role +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", headSculpture='" + headSculpture + '\'' +
                ", isBan=" + isBan +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", banMessage='" + banMessage + '\'' +
                ", subscribeStatus=" + subscribeStatus +
                ", school=" + school +
                ", sex=" + sex +
                ", personalWebsite=" + personalWebsite +
                ", wechat=" + wechat +
                ", qq=" + qq +
                ", wordNumber=" + wordNumber +
                '}';
    }
}