package com.taas.petsconnect.Model;

public class ArticleModel {
    String blogtitle,blogcontent,blogimage;
    private String articleBy;
    private long articleAt;

    public ArticleModel() {
    }

    public ArticleModel(String blogtitle, String blogcontent, String blogimage, String articleBy, long articleAt) {
        this.blogtitle = blogtitle;
        this.blogcontent = blogcontent;
        this.blogimage = blogimage;
        this.articleBy = articleBy;
        this.articleAt = articleAt;
    }

    public String getArticleBy() {
        return articleBy;
    }

    public void setArticleBy(String articleBy) {
        this.articleBy = articleBy;
    }

    public long getArticleAt() {
        return articleAt;
    }

    public void setArticleAt(long articleAt) {
        this.articleAt = articleAt;
    }

    public String getBlogtitle() {
        return blogtitle;
    }

    public void setBlogtitle(String blogtitle) {
        this.blogtitle = blogtitle;
    }

    public String getBlogcontent() {
        return blogcontent;
    }

    public void setBlogcontent(String blogcontent) {
        this.blogcontent = blogcontent;
    }

    public String getBlogimage() {
        return blogimage;
    }

    public void setBlogimage(String blogimage) {
        this.blogimage = blogimage;
    }
}
