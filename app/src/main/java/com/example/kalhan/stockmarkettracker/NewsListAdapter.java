package com.example.kalhan.stockmarkettracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kalhan.stockmarkettracker.Model.NewsItem;

import java.util.ArrayList;

/**
 * Created by Kalhan on 11/18/2017.
 */

public class NewsListAdapter extends ArrayAdapter<NewsItem> {
    private Context context;

    public NewsListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public NewsListAdapter(Context context, int resource, ArrayList<NewsItem> items) {
        super(context, resource, items);
        this.context = context;
    }
    public void updateNewsList(ArrayList<NewsItem> newslist) {
        super.clear();
        super.addAll(newslist);
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.news_row, null);
        }

        NewsItem item = getItem(position);
        final String url = item.getUrl();

        if (item != null) {
            TextView heading = (TextView) v.findViewById(R.id.news_heading);
            TextView author = (TextView) v.findViewById(R.id.news_author);
            TextView date = (TextView) v.findViewById(R.id.news_date);
            heading.setPaintFlags(heading.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            heading.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(browserIntent);
                }
            });

            heading.setText(item.getTitle());
            author.setText("Author: "+item.getAuthor());
            date.setText("Date: "+item.getPubDate());
        }

        return v;
    }

}
