package com.kcbs.webforum.pojo;

import java.util.List;

public class PageInfoUser {


    /**
     * status : 10000
     * msg : SUCCESS
     * data : {"total":1,"list":[{"userId":4,"username":"hyx","password":null,"email":"","personalizedSignature":"你好呀","role":1,"createTime":"2020-12-31 12:09:54","updateTime":"2021-02-02 17:19:23","headSculpture":"http://47.111.9.152:8088/images/56674305-98b3-4be4-938b-c7a2747cb23b.jpeg","isBan":0,"startTime":"2021-01-30 00:55:13","endTime":"2021-01-30 00:55:18","banMessage":".."}],"pageNum":1,"pageSize":20,"size":1,"startRow":1,"endRow":1,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1}
     */

    private int status;
    private String msg;
    private DataBean data;

    @Override
    public String toString() {
        return "PageInfoUser{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

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
         * total : 1
         * list : [{"userId":4,"username":"hyx","password":null,"email":"","personalizedSignature":"你好呀","role":1,"createTime":"2020-12-31 12:09:54","updateTime":"2021-02-02 17:19:23","headSculpture":"http://47.111.9.152:8088/images/56674305-98b3-4be4-938b-c7a2747cb23b.jpeg","isBan":0,"startTime":"2021-01-30 00:55:13","endTime":"2021-01-30 00:55:18","banMessage":".."}]
         * pageNum : 1
         * pageSize : 20
         * size : 1
         * startRow : 1
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


        @Override
        public String toString() {
            return "DataBean{" +
                    "total=" + total +
                    ", pageNum=" + pageNum +
                    ", pageSize=" + pageSize +
                    ", size=" + size +
                    ", startRow=" + startRow +
                    ", endRow=" + endRow +
                    ", pages=" + pages +
                    ", prePage=" + prePage +
                    ", nextPage=" + nextPage +
                    ", isFirstPage=" + isFirstPage +
                    ", isLastPage=" + isLastPage +
                    ", hasPreviousPage=" + hasPreviousPage +
                    ", hasNextPage=" + hasNextPage +
                    ", navigatePages=" + navigatePages +
                    ", navigateFirstPage=" + navigateFirstPage +
                    ", navigateLastPage=" + navigateLastPage +
                    ", list=" + list +
                    ", navigatepageNums=" + navigatepageNums +
                    '}';
        }

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

        public static class ListBean {
            /**
             * userId : 4
             * username : hyx
             * password : null
             * email :
             * personalizedSignature : 你好呀
             * role : 1
             * createTime : 2020-12-31 12:09:54
             * updateTime : 2021-02-02 17:19:23
             * headSculpture : http://47.111.9.152:8088/images/56674305-98b3-4be4-938b-c7a2747cb23b.jpeg
             * isBan : 0
             * startTime : 2021-01-30 00:55:13
             * endTime : 2021-01-30 00:55:18
             * banMessage : ..
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

            @Override
            public String toString() {
                return "ListBean{" +
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
                        '}';
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
        }
    }
}
