package com.tangledwebgames.guardiansciencereader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<Article>> {

    static final int NEWS_LOADER_ID = 0;

    public NewsLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Article> loadInBackground() {
        return NewsQueryUtil.getNewsData();
    }
}
