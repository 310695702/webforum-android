package com.kcbs.webforum.pojo;

import java.io.Serializable;
import java.util.List;

public class Page{

    /**
     * status : 10000
     * msg : SUCCESS
     * data : {"total":2,"list":[{"userId":1,"username":"测试","password":null,"email":"","personalizedSignature":"1","role":1,"createTime":"2020-12-26T12:43:58.000+0000","updateTime":"2020-12-26T12:44:45.000+0000","headSculpture":null},{"userId":2,"username":"admin","password":null,"email":"","personalizedSignature":"","role":2,"createTime":"2020-12-26T12:47:00.000+0000","updateTime":"2020-12-26T12:47:20.000+0000","headSculpture":null}],"pageNum":1,"pageSize":2,"size":2,"startRow":0,"endRow":1,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1}
     */

    private int status;
    private String msg;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * total : 2
         * list : [{"userId":1,"username":"测试","password":null,"email":"","personalizedSignature":"1","role":1,"createTime":"2020-12-26T12:43:58.000+0000","updateTime":"2020-12-26T12:44:45.000+0000","headSculpture":null},{"userId":2,"username":"admin","password":null,"email":"","personalizedSignature":"","role":2,"createTime":"2020-12-26T12:47:00.000+0000","updateTime":"2020-12-26T12:47:20.000+0000","headSculpture":null}]
         * pageNum : 1
         * pageSize : 2
         * size : 2
         * startRow : 0
         * endRow : 1
         * pages : 1
         * prePage : 0
         * nextPage : 0
         * isFirstPage : true
         * isLastPage : true
         * hasPreviousPage : false
         * hasNextPage : false
         * navigatePages : 8
         * navigatepageNums : [1]
         * navigateFirstPage : 1
         * navigateLastPage : 1
         */

        private int total;
        private int pageNum;
        private int pageSize;
        private int size;
        private int startRow;
        private int endRow;
        private int pages;
        private int prePage;
        private int nextPage;
        private boolean isFirstPage;
        private boolean isLastPage;
        private boolean hasPreviousPage;
        private boolean hasNextPage;
        private int navigatePages;
        private int navigateFirstPage;
        private int navigateLastPage;
        private List<ListBean> list;
        private List<Integer> navigatepageNums;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getStartRow() {
            return startRow;
        }

        public void setStartRow(int startRow) {
            this.startRow = startRow;
        }

        public int getEndRow() {
            return endRow;
        }

        public void setEndRow(int endRow) {
            this.endRow = endRow;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getPrePage() {
            return prePage;
        }

        public void setPrePage(int prePage) {
            this.prePage = prePage;
        }

        public int getNextPage() {
            return nextPage;
        }

        public void setNextPage(int nextPage) {
            this.nextPage = nextPage;
        }

        public boolean isIsFirstPage() {
            return isFirstPage;
        }

        public void setIsFirstPage(boolean isFirstPage) {
            this.isFirstPage = isFirstPage;
        }

        public boolean isIsLastPage() {
            return isLastPage;
        }

        public void setIsLastPage(boolean isLastPage) {
            this.isLastPage = isLastPage;
        }

        public boolean isHasPreviousPage() {
            return hasPreviousPage;
        }

        public void setHasPreviousPage(boolean hasPreviousPage) {
            this.hasPreviousPage = hasPreviousPage;
        }

        public boolean isHasNextPage() {
            return hasNextPage;
        }

        public void setHasNextPage(boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
        }

        public int getNavigatePages() {
            return navigatePages;
        }

        public void setNavigatePages(int navigatePages) {
            this.navigatePages = navigatePages;
        }

        public int getNavigateFirstPage() {
            return navigateFirstPage;
        }

        public void setNavigateFirstPage(int navigateFirstPage) {
            this.navigateFirstPage = navigateFirstPage;
        }

        public int getNavigateLastPage() {
            return navigateLastPage;
        }

        public void setNavigateLastPage(int navigateLastPage) {
            this.navigateLastPage = navigateLastPage;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public List<Integer> getNavigatepageNums() {
            return navigatepageNums;
        }

        public void setNavigatepageNums(List<Integer> navigatepageNums) {
            this.navigatepageNums = navigatepageNums;
        }

        public static class ListBean implements Serializable {
            /**
             * userId : 1
             * username : 测试
             * password : null
             * email :
             * personalizedSignature : 1
             * role : 1
             * createTime : 2020-12-26T12:43:58.000+0000
             * updateTime : 2020-12-26T12:44:45.000+0000
             * headSculpture : null
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
            private String school;
            private int sex;
            private String wechat;
            private String qq;
            private int wordNumber;

            public int getSubscribeStatus() {
                return subscribeStatus;
            }

            public void setSubscribeStatus(int subscribeStatus) {
                this.subscribeStatus = subscribeStatus;
            }

            public String getSchool() {
                return school;
            }

            public void setSchool(String school) {
                this.school = school;
            }

            public int getSex() {
                return sex;
            }

            public void setSex(int sex) {
                this.sex = sex;
            }

            public String getWechat() {
                return wechat;
            }

            public void setWechat(String wechat) {
                this.wechat = wechat;
            }

            public String getQq() {
                return qq;
            }

            public void setQq(String qq) {
                this.qq = qq;
            }

            public int getWordNumber() {
                return wordNumber;
            }

            public void setWordNumber(int wordNumber) {
                this.wordNumber = wordNumber;
            }

            public ListBean(){

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

            public ListBean(int userId, String username, String personalizedSignature, String headSculpture) {
                this.userId = userId;
                this.username = username;
                this.personalizedSignature = personalizedSignature;
                this.headSculpture = headSculpture;
            }

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
        }
    }
}