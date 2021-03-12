package com.kcbs.webforum.pojo;

import java.io.Serializable;
import java.util.List;

public class CommentVo {
    /**
     * total : 15
     * list : [{"commentId":14,"userId":4,"parentId":1,"content":"123","commentTime":"2021-01-13T03:22:58.000+0000","visibility":1,"username":"hyx","headSculpture":"/root/webforumimg/6d057aec-0e73-4b5b-980c-42588bd4a3e9.jpg"},{"commentId":15,"userId":4,"parentId":1,"content":"123","commentTime":"2021-01-13T03:22:58.000+0000","visibility":1,"username":"hyx","headSculpture":"/root/webforumimg/6d057aec-0e73-4b5b-980c-42588bd4a3e9.jpg"},{"commentId":12,"userId":4,"parentId":1,"content":"123","commentTime":"2021-01-13T03:22:57.000+0000","visibility":1,"username":"hyx","headSculpture":"/root/webforumimg/6d057aec-0e73-4b5b-980c-42588bd4a3e9.jpg"},{"commentId":13,"userId":4,"parentId":1,"content":"123","commentTime":"2021-01-13T03:22:57.000+0000","visibility":1,"username":"hyx","headSculpture":"/root/webforumimg/6d057aec-0e73-4b5b-980c-42588bd4a3e9.jpg"},{"commentId":11,"userId":4,"parentId":1,"content":"123","commentTime":"2021-01-13T03:22:56.000+0000","visibility":1,"username":"hyx","headSculpture":"/root/webforumimg/6d057aec-0e73-4b5b-980c-42588bd4a3e9.jpg"},{"commentId":9,"userId":4,"parentId":1,"content":"123","commentTime":"2021-01-13T03:22:55.000+0000","visibility":1,"username":"hyx","headSculpture":"/root/webforumimg/6d057aec-0e73-4b5b-980c-42588bd4a3e9.jpg"},{"commentId":10,"userId":4,"parentId":1,"content":"123","commentTime":"2021-01-13T03:22:55.000+0000","visibility":1,"username":"hyx","headSculpture":"/root/webforumimg/6d057aec-0e73-4b5b-980c-42588bd4a3e9.jpg"},{"commentId":8,"userId":4,"parentId":1,"content":"123","commentTime":"2021-01-13T03:22:54.000+0000","visibility":1,"username":"hyx","headSculpture":"/root/webforumimg/6d057aec-0e73-4b5b-980c-42588bd4a3e9.jpg"},{"commentId":6,"userId":4,"parentId":1,"content":"123","commentTime":"2021-01-13T03:22:53.000+0000","visibility":1,"username":"hyx","headSculpture":"/root/webforumimg/6d057aec-0e73-4b5b-980c-42588bd4a3e9.jpg"},{"commentId":7,"userId":4,"parentId":1,"content":"123","commentTime":"2021-01-13T03:22:53.000+0000","visibility":1,"username":"hyx","headSculpture":"/root/webforumimg/6d057aec-0e73-4b5b-980c-42588bd4a3e9.jpg"}]
     * pageNum : 1
     * pageSize : 10
     * size : 10
     * startRow : 1
     * endRow : 10
     * pages : 2
     * prePage : 0
     * nextPage : 2
     * isFirstPage : true
     * isLastPage : false
     * hasPreviousPage : false
     * hasNextPage : true
     * navigatePages : 8
     * navigatepageNums : [1,2]
     * navigateFirstPage : 1
     * navigateLastPage : 2
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
         * commentId : 14
         * userId : 4
         * parentId : 1
         * content : 123
         * commentTime : 2021-01-13T03:22:58.000+0000
         * visibility : 1
         * username : hyx
         * headSculpture : /root/webforumimg/6d057aec-0e73-4b5b-980c-42588bd4a3e9.jpg
         */

        private int commentId;
        private int userId;
        private int parentId;
        private String content;
        private String commentTime;
        private int visibility;
        private String username;
        private String headSculpture;
        private String personalizedSignature;
        private String categoryName;
        private String title;

        public String getCategoryName() {
            return categoryName;
        }

        public String getTitle() {
            return title;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPersonalizedSignature() {
            return personalizedSignature;
        }

        public void setPersonalizedSignature(String personalizedSignature) {
            this.personalizedSignature = personalizedSignature;
        }

        public int getCommentId() {
            return commentId;
        }

        public void setCommentId(int commentId) {
            this.commentId = commentId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCommentTime() {
            return commentTime;
        }

        public void setCommentTime(String commentTime) {
            this.commentTime = commentTime;
        }

        public int getVisibility() {
            return visibility;
        }

        public void setVisibility(int visibility) {
            this.visibility = visibility;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getHeadSculpture() {
            return headSculpture;
        }

        public void setHeadSculpture(String headSculpture) {
            this.headSculpture = headSculpture;
        }
    }
}
