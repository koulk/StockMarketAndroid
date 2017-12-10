package com.example.kalhan.stockmarkettracker;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.kalhan.stockmarkettracker.Model.NewsItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Kalhan on 11/18/2017.
 */

public class DownloadNewsFeedTask extends AsyncTask<String, Integer, ArrayList<NewsItem>> {
    private String serverUrl = "http://alphavantageapi-env.us-east-2.elasticbeanstalk.com/api/news?stockName=";
    private WeakReference<StockDetailsActivity.NewsFeedFragment> fragmentReference;

    public DownloadNewsFeedTask(StockDetailsActivity.NewsFeedFragment context)
    {
        fragmentReference = new WeakReference<>(context);
    }

    // This is run in a background thread
    @Override
    protected ArrayList<NewsItem> doInBackground(String... params) {
        // get the string from params, which is an array
        String stockSymbol = params[0];
        ArrayList<NewsItem> newsItems = new ArrayList<>();
        HttpURLConnection conn = null;
        InputStream input = null;
        try{
            URL url = new URL (serverUrl+stockSymbol);
            conn = (HttpURLConnection) url.openConnection();
            input = conn.getInputStream();
            InputStreamReader reader = new InputStreamReader (input, "UTF-8");
            BufferedReader buffer = new BufferedReader (reader, 8192);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null)
            {
                builder.append (line);
            }
            JSONObject jsonResponse = new JSONObject (builder.toString());
            JSONArray responseArr =  jsonResponse.getJSONArray("news");
            Log.i("response", builder.toString());

            //Log.i("process", responseArr.toString());
            for(int itr=0; itr<responseArr.length(); itr++){
                JSONObject newsInfo = responseArr.getJSONObject(itr);
                NewsItem newsRow = new NewsItem(newsInfo.getString("author"),newsInfo.getString("url"),newsInfo.getString("title"),newsInfo.getString("pubDate"));
                newsItems.add(newsRow);
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return newsItems;
    }

    @Override
    protected void onPreExecute() {
        StockDetailsActivity.NewsFeedFragment fragment = fragmentReference.get();
        if (fragment == null || fragment.getView()== null) return;
        fragment.loader.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onPostExecute(ArrayList<NewsItem> result) {
        //super.onPostExecute(result);
        StockDetailsActivity.NewsFeedFragment fragment = fragmentReference.get();
        if (fragment == null || fragment.getView()== null) return;
        View newsFeedContainer = fragment.getView();

        fragment.newsArticles.clear();
        fragment.newsArticles.addAll(result);

        fragment.loader.setVisibility(View.GONE);
        fragment.newsFeedAdapter.notifyDataSetChanged();
        TextView error = (TextView)newsFeedContainer.findViewById(R.id.news_error_message);
        if(result.size() != 0){
            error.setVisibility(View.GONE);
        }
        else{
            error.setVisibility(View.VISIBLE);
        }

    }

}