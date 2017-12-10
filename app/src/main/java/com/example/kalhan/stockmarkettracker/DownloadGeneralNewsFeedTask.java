package com.example.kalhan.stockmarkettracker;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

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
 * Created by Kalhan on 12/6/2017.
 */

public class DownloadGeneralNewsFeedTask extends AsyncTask<Void, Void, ArrayList<NewsItem>> {
    private String serverBaseURL,apiKey;
    private WeakReference<StockSearchActivity> activityReference;

    public DownloadGeneralNewsFeedTask(StockSearchActivity context)
    {
        activityReference = new WeakReference<>(context);
    }

    // This is run in a background thread
    @Override
    protected ArrayList<NewsItem>  doInBackground(Void... params) {
        // get the string from params, which is an array
        //String stockSymbol = params[0];
        ArrayList<NewsItem> newsItems = new ArrayList<>();
        HttpURLConnection conn = null;
        InputStream input = null;
        try{
            URL url = new URL(serverBaseURL+"&category=business&apiKey="+ apiKey);
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
            Log.i("response", builder.toString());
            Log.i("url", url.toString());

            if(jsonResponse.getString("status").equals("ok")){
                JSONArray responseArr =  jsonResponse.getJSONArray("articles");

                //Log.i("process", responseArr.toString());
                for(int itr=0; itr<responseArr.length(); itr++){
                    JSONObject newsInfo = responseArr.getJSONObject(itr);
                    String author = (newsInfo.get("author") == null || newsInfo.getString("author").equals("null")) ? "Bloomberg": newsInfo.getString("author");
                    String timeStamp = newsInfo.getString("publishedAt");
                    NewsItem newsRow = new NewsItem(author,newsInfo.getString("url"),newsInfo.getString("title"),timeStamp.substring(0,timeStamp.indexOf("T")));
                    newsItems.add(newsRow);
                }
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return newsItems;
    }

    @Override
    protected void onPreExecute() {
        StockSearchActivity activity = activityReference.get();
        if (activity == null) return;
        activity.newsLoader.setVisibility(View.VISIBLE);
        serverBaseURL = activity.getString(R.string.news_api_endpoint);
        apiKey = activity.getString(R.string.news_api_key);
    }


    @Override
    protected void onPostExecute(ArrayList<NewsItem> result) {
        //super.onPostExecute(result);
        StockSearchActivity activity = activityReference.get();
        if (activity == null) return;

        activity.newsArticles.clear();
        activity.newsArticles.addAll(result);

        activity.newsLoader.setVisibility(View.GONE);
        activity.newsFeedAdapter.notifyDataSetChanged();
    }

}