package com.tangledwebgames.guardiansciencereader;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class NewsDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news_detail_fragment, container, false);
    }

    void setArticle(Article article) {
        Activity activity = getActivity();
        TextView headlineView = activity.findViewById(R.id.headline);
        TextView trailTextView = activity.findViewById(R.id.trail_text);
        TextView authorsView = activity.findViewById(R.id.author);
        TextView dateView = activity.findViewById(R.id.publish_date);
        Button readArticleButton = activity.findViewById(R.id.read_article_button);

        if (!TextUtils.isEmpty(article.headline)) {
            headlineView.setText(article.headline);
        } else {
            headlineView.setText(R.string.no_headline_info);
        }
        if (!TextUtils.isEmpty(article.trailText)) {
            trailTextView.setText(article.trailText);
        } else {
            trailTextView.setText("");
        };
        if (!TextUtils.isEmpty(article.authors)) {
            authorsView.setText(article.authors);
        } else {
            authorsView.setText(R.string.no_author_info);
        }
        if (!TextUtils.isEmpty(article.publicationDate)) {
            dateView.setText(article.publicationDate);
        } else {
            dateView.setText(R.string.no_publish_date);
        }


        if (!TextUtils.isEmpty(article.articleUrl)) {
            final Uri articleUrl = Uri.parse(article.articleUrl);
            readArticleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, articleUrl);
                    startActivity(intent);
                }
            });
        } else {
            readArticleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), R.string.no_article_url, Toast.LENGTH_SHORT).show();
                }
            });
        }

        new ThumbnailLoadTask((ViewGroup) activity.findViewById(R.id.thumbnail_container))
                .execute(article.thumbnailUrl);
    }

}
