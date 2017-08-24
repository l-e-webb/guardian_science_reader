package com.tangledwebgames.guardiansciencereader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {

    public ArticleAdapter(Context context, List<Article> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View item = convertView;
        if (item == null) {
            item = LayoutInflater.from(getContext()).inflate(R.layout.article_item, parent, false);
        }

        Context context = getContext();

        int color;
        if ((position % 2) == 0) {
            color = context.getResources().getColor(R.color.colorPrimary);
        } else {
            color = context.getResources().getColor(R.color.colorPrimaryDark);
        }
        item.setBackgroundColor(color);

        Article article = getItem(position);
        if (article != null && !TextUtils.isEmpty(article.headline))
            ((TextView) item.findViewById(R.id.list_headline)).setText(article.headline);

        return item;
    }
}
