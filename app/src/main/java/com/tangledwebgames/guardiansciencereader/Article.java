package com.tangledwebgames.guardiansciencereader;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {

    public static Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel parcel) {
            return new Article(parcel);
        }

        @Override
        public Article[] newArray(int i) {
            return new Article[i];
        }
    };

    private static final String AUTHOR_SEPERATOR = ", ";

    public Article() {}

    private Article(Parcel parcel) {
        headline = parcel.readString();
        trailText = parcel.readString();
        authors = parcel.readString();
        publicationDate = parcel.readString();
        articleUrl = parcel.readString();
        thumbnailUrl = parcel.readString();
    }

    String headline;
    String trailText;
    String authors;
    String publicationDate;
    String articleUrl;
    String thumbnailUrl;

    static String getAuthorList(String[] authors) {
        if (authors == null || authors.length == 0) {
            return null;
        }

        String authorList = authors[0];
        for (int i = 1; i < authors.length; i++) {
            authorList += AUTHOR_SEPERATOR + authors[i];
        }
        return authorList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(headline);
        parcel.writeString(trailText);
        parcel.writeString(authors);
        parcel.writeString(publicationDate);
        parcel.writeString(articleUrl);
        parcel.writeString(thumbnailUrl);
    }
}
