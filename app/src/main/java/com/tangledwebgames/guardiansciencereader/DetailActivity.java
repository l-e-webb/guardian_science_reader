package com.tangledwebgames.guardiansciencereader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        Article article = intent.getParcelableExtra(MainActivity.ARTICLE_KEY);
        NewsDetailFragment fragment = (NewsDetailFragment)
                getFragmentManager().findFragmentById(R.id.news_detail_fragment);
        if (fragment != null) {
            fragment.setArticle(article);
        } else {
            Log.e(LOG_TAG, "Unable to create detail fragment in detail activity.");
        }
    }
}
