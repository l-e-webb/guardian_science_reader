package com.tangledwebgames.guardiansciencereader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements NewsSelector {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String ARTICLE_KEY = "article";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void select(Article article) {
        Log.i(LOG_TAG, "Select callback firing.");
        NewsDetailFragment detailFragment = (NewsDetailFragment)
                getFragmentManager().findFragmentById(R.id.news_detail_fragment);
        if (detailFragment == null) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(ARTICLE_KEY, article);
            startActivity(intent);
        } else {
            detailFragment.setArticle(article);
        }
    }

    @Override
    public void clearSelection() {
        NewsDetailFragment detailFragment = (NewsDetailFragment)
                getFragmentManager().findFragmentById(R.id.news_detail_fragment);
        if (detailFragment != null) {
            //Supplying a blank article with clear all the fields.
            detailFragment.setArticle(new Article());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            NewsListFragment listFragment = (NewsListFragment) getFragmentManager()
                    .findFragmentById(R.id.news_list_fragment);
            if (listFragment != null) {
                listFragment.refreshData();
                return true;
            }
        }
        return false;
    }
}
