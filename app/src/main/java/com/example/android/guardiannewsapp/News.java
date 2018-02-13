package com.example.android.guardiannewsapp;



public class News {

    private String sectionName;
    private String webPublicationDate;
    private String webTitle;  //result/webtitle: title of the article
    private String webUrl;
    private String authorsName; //result/tags/webTitle: the author's name

    public News(String sectionName, String webPublicationDate, String webTitle, String webUrl, String authorsName) {
        this.sectionName = sectionName;
        this.webPublicationDate = webPublicationDate;
        this.webTitle = webTitle;
        this.webUrl = webUrl;
        this.authorsName = authorsName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getWebPublicationDate() {
        return webPublicationDate;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getAuthorsName() {
        return authorsName;
    }
}
