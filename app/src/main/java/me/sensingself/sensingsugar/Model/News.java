package me.sensingself.sensingsugar.Model;

/**
 * Created by liujie on 1/12/18.
 */

public class News {
    private String imageUrl;
    private String newsContent;
    private String newsWebsite;
    public News() {
        this.imageUrl = "";
        this.newsContent = "";
        this.newsWebsite = "";
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public String getNewsContent() {
        return this.newsContent;
    }
    public String getNewsWebsite() {
        return this.newsWebsite;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }
    public void setNewsWebsite(String newsWebsite) {
        this.newsWebsite = newsWebsite;
    }
    @Override
    public String toString() {
        return "News{" +
                "imageUrl=" + this.imageUrl +
                ", newsContent='" + this.newsContent + '\'' +
                ", newsWebsite='" + this.newsWebsite +
                '}';
    }
}
