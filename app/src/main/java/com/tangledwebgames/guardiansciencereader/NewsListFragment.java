package com.tangledwebgames.guardiansciencereader;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Article>> {

    private static final String LOG_TAG = NewsListFragment.class.getSimpleName();

    NewsSelector selector;
    ArticleAdapter adapter;
    ListView listView;
    TextView emptyListTextView;
    View loadingIndicator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View layout =  inflater.inflate(R.layout.news_list_fragment, container, false);

        Context context = getActivity();
        selector = (MainActivity) context;
        listView = layout.findViewById(R.id.news_list);
        adapter = new ArticleAdapter(context, new ArrayList<Article>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selector.select(adapter.getItem(i));
            }
        });
        emptyListTextView = layout.findViewById(R.id.empty_list_text_view);
        listView.setEmptyView(emptyListTextView);
        loadingIndicator = layout.findViewById(R.id.list_loading_indicator);

        refreshData();

        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
        emptyListTextView = null;
        loadingIndicator = null;
        listView = null;
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        showLoader();
        return new NewsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
        hideLoader();
        if (data != null && data.size() > 0) {
            setArticleArray(data);
            getLoaderManager().destroyLoader(NewsLoader.NEWS_LOADER_ID);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        adapter.clear();
        hideLoader();
    }

    void refreshData() {
        NetworkInfo networkInfo = ((ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            clear();
            emptyListTextView.setText(R.string.no_internet);
            return;
        }
        if (NewsQueryUtil.apiKey == null) {
            try {
                NewsQueryUtil.loadApiKey(getActivity());
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error loading API key from file.", e);
                return;
            }
        }
        getLoaderManager().initLoader(NewsLoader.NEWS_LOADER_ID, null, this);
    }

    void showLoader() {
        clear();
        emptyListTextView.setText("");
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    void hideLoader() {
        emptyListTextView.setText(R.string.error_loading_stories);
        loadingIndicator.setVisibility(View.GONE);
    }

    void clear() {
        adapter.clear();
    }

    void setArticleArray(List<Article> articles) {
        adapter.clear();
        adapter.addAll(articles);
        ((ListView) getActivity().findViewById(R.id.news_list)).setSelection(0);
    }

}
