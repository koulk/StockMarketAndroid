package com.example.kalhan.stockmarkettracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kalhan.stockmarkettracker.Model.NewsItem;
import com.example.kalhan.stockmarkettracker.Model.SpinnerItem;
import com.example.kalhan.stockmarkettracker.Model.StockInformation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.kalhan.stockmarkettracker.Model.StockInformation.COMPARE_BY_CHANGE_ASCENDING;
import static com.example.kalhan.stockmarkettracker.Model.StockInformation.COMPARE_BY_CHANGE_DESCENDING;
import static com.example.kalhan.stockmarkettracker.Model.StockInformation.COMPARE_BY_LASTPRICE_ASCENDING;
import static com.example.kalhan.stockmarkettracker.Model.StockInformation.COMPARE_BY_LASTPRICE_DESCENDING;
import static com.example.kalhan.stockmarkettracker.Model.StockInformation.COMPARE_BY_STOCKNAME_ASCENDING;
import static com.example.kalhan.stockmarkettracker.Model.StockInformation.COMPARE_BY_STOCKNAME_DESCENDING;

public class StockSearchActivity extends AppCompatActivity {
    private Button getQuote, clearButton;
    private String stockName, stockDisplayName;
    protected int sortCategoryIndex=0, sortTypeIndex=0;
    protected ArrayList<StockInformation> favoriteList = new ArrayList<>();
    protected FavoriteListAdaptor favoriteListAdaptor = null;
    protected ArrayList<NewsItem> newsArticles = new ArrayList();
    protected NewsListAdapter newsFeedAdapter;
    ProgressBar newsLoader;
    Timer autorefreshTimer = null;
    ImageView refreshButton;
    static int requestPendingCount;
    DelayAutoComplete autocomplete = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.actionBarColor)));
        setContentView(R.layout.activity_stock_search);

        getQuote =  findViewById(R.id.get_quote);
        clearButton = findViewById(R.id.clear);
        refreshButton = findViewById(R.id.refresh_button);
        autocomplete = (DelayAutoComplete) findViewById(R.id.autoCompleteTextView);
        SwitchCompat autorefresh = findViewById(R.id.autorefresh);
        TextView newsAttribution = (TextView) findViewById(R.id.news_attribution);
        newsAttribution.setMovementMethod(LinkMovementMethod.getInstance());
        final ListView favoriteListView = (ListView) findViewById(R.id.favorite_list);


        // AutoComplete Adaptor Settings
        StockAutoCompleteAdapter adapter = new StockAutoCompleteAdapter
                (this,android.R.layout.select_dialog_item);
        autocomplete.setAdapter(adapter);
        autocomplete.setLoadingIndicator((ProgressBar)findViewById(R.id.loading_indicator));
        autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                StockSuggestions selected = (StockSuggestions) arg0.getAdapter().getItem(position);
                autocomplete.setText(selected.getStockName() + " - " + selected.getCompanyName() + " ("+selected.getStockExchange()+")");
                stockName = selected.getStockName();
                stockDisplayName = selected.getCompanyName();
            }
        });

        // Favorite List Adaptor Settings
        favoriteListAdaptor = new FavoriteListAdaptor(this, R.layout.favorite_row, favoriteList);
        favoriteListView.setAdapter(favoriteListAdaptor);
        favoriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                StockInformation stockInfo = (StockInformation) favoriteListView.getItemAtPosition(position);
                Intent myIntent = new Intent(v.getContext(), StockDetailsActivity.class);
                myIntent.putExtra("stockName", stockInfo.getStockName());
                myIntent.putExtra("displayName", stockInfo.getStockDisplayName());
                startActivity(myIntent);
            }
        });

        // News Feed Adaptor Settings
        final ListView newsFeedView = findViewById(R.id.general_news_list);
        newsLoader = (ProgressBar) findViewById(R.id.news_loading_indicator);
        newsFeedAdapter = new NewsListAdapter(this, R.layout.news_row, newsArticles);
        newsFeedView.setAdapter(newsFeedAdapter);
        newsFeedView.setLongClickable(true);

        newsFeedView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                NewsItem item = (NewsItem) newsFeedView.getItemAtPosition(position);
                sendIntent.putExtra(Intent.EXTRA_TEXT, item.getUrl());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            }});


        DownloadGeneralNewsFeedTask newsFeedTask = new DownloadGeneralNewsFeedTask(this);
        newsFeedTask.execute();

        // Context Menu for favorite list
        favoriteListView.setLongClickable(true);
        favoriteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
                LayoutInflater inflater = getLayoutInflater();
                View headerView = inflater.inflate(R.layout.favorite_context_header, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setCustomTitle(headerView);
                builder.setItems(R.array.favorites_context_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 1){
                                    for(int itr=0; itr<favoriteList.size(); itr++){
                                        if(favoriteList.get(itr).getStockName().equals(favoriteList.get(position).getStockName())){
                                            favoriteList.remove(itr);
                                            break;
                                        }
                                    }
                                    favoriteListAdaptor.notifyDataSetChanged();
                                    updateFavoritePlaceholderMessage();
                                    updateFavorites();
                                    Toast toast = Toast.makeText(getBaseContext(), "Selected Yes", Toast.LENGTH_SHORT);
                                    toast.show();

                                }
                            }
                        });
                builder.create();
                builder.show();

               return true;
            }
        });



        Spinner sortCategory = findViewById(R.id.spinner_sort_category);
        final ArrayList<SpinnerItem> orderCategoryArray = new ArrayList<SpinnerItem>();
        ArrayList<String> orderCategoriesValue =  new ArrayList<String>
                (Arrays.asList(getResources().getStringArray(R.array.favorites_sort_categories)));
        for(int itr=0; itr< orderCategoriesValue.size(); itr++){
            if(itr == 0){
                orderCategoryArray.add(new SpinnerItem(orderCategoriesValue.get(itr),false));
            }
            else{
                orderCategoryArray.add(new SpinnerItem(orderCategoriesValue.get(itr),true));
            }
        }

        final SpinnerAdaptor sortCategoriesAdapter =  new SpinnerAdaptor(this,
                android.R.layout.simple_spinner_item, orderCategoryArray);
        sortCategory.setAdapter(sortCategoriesAdapter);
        sortCategory.setSelection(0);

        Spinner sortType = findViewById(R.id.spinner_sort_type);
        final ArrayList<SpinnerItem> orderTypeArray = new ArrayList<SpinnerItem>();
        ArrayList<String> orderTypeValue =  new ArrayList<String>
                (Arrays.asList(getResources().getStringArray(R.array.favorites_sort_types)));
        for(int itr=0; itr< orderTypeValue.size(); itr++){
           if(itr == 0){
               orderTypeArray.add(new SpinnerItem(orderTypeValue.get(itr),false));
           }
           else{
               orderTypeArray.add(new SpinnerItem(orderTypeValue.get(itr),true));
           }
        }

        final SpinnerAdaptor sortTypeAdapter =  new SpinnerAdaptor(this,
                android.R.layout.simple_spinner_item, orderTypeArray);
        sortType.setAdapter(sortTypeAdapter);
        sortType.setSelection(0);

        // Capture button clicks
        getQuote.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if(autocomplete.getText().toString().trim().length() <=0){
                    Snackbar mSnackbar = Snackbar.make(findViewById(R.id.containerLayout), "Please enter a stock name or symbol", Snackbar.LENGTH_SHORT);
                    mSnackbar.show();
                    return;
                }
                if(stockName == null || !(autocomplete.getText().toString().contains(stockName))){
                    Snackbar mSnackbar = Snackbar.make(findViewById(R.id.containerLayout), "Please select stock name from suggestions", Snackbar.LENGTH_SHORT);
                    mSnackbar.show();
                    return;
                }

                // Start StockDetails.class
                Intent myIntent = new Intent(view.getContext(), StockDetailsActivity.class);
                myIntent.putExtra("stockName", stockName);
                myIntent.putExtra("displayName", stockDisplayName);
                //myIntent.putExtra("stockDisplay", displayText);
                startActivity(myIntent);
                //finish();
            }
        });

        clearButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                autocomplete.setText("");
            }
        });

        sortCategory.getBackground().setColorFilter(getResources().getColor(R.color.yellowTint), PorterDuff.Mode.SRC_ATOP);
        sortCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                 sortCategoryIndex = position;
                 sortFavoriteList(sortCategoryIndex, sortTypeIndex);
                 for(int itr=1; itr< orderCategoryArray.size(); itr++){
                    if(position == itr){
                        orderCategoryArray.get(itr).setEnabled(false);
                    }
                    else{
                        orderCategoryArray.get(itr).setEnabled(true);
                    }
                 }
                sortCategoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        sortType.getBackground().setColorFilter(getResources().getColor(R.color.yellowTint), PorterDuff.Mode.SRC_ATOP);
        sortType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                sortTypeIndex = position;
                sortFavoriteList(sortCategoryIndex, sortTypeIndex);
                for(int itr=1; itr< orderTypeArray.size(); itr++){
                    if(position == itr){
                        orderTypeArray.get(itr).setEnabled(false);
                    }
                    else{
                        orderTypeArray.get(itr).setEnabled(true);
                    }
                }
                sortTypeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(favoriteList.size() == 0){
                    return;
                }
                Animation animation = new RotateAnimation(0.0f, 360.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
                animation.setRepeatCount(-1);
                animation.setDuration(1000);
                ((ImageView)findViewById(R.id.refresh_button)).startAnimation(animation);
                refreshFavoriteList();
            }
        });

        autorefresh.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            autorefreshFavoriteList(isChecked);
                        }
                    }
        );

    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<StockInformation> tempFavorites = fetchFavorites();
        favoriteList.clear();
        favoriteList.addAll(tempFavorites);
        favoriteListAdaptor.notifyDataSetChanged();
        updateFavoritePlaceholderMessage();
        //refreshFavoriteList();

        // cancel autorefresh requests if state of app changes.
        if(autorefreshTimer != null){
            autorefreshTimer.cancel();
        }
    }

    public void sortFavoriteList(int sortCategoryIndex, int sortTypeIndex){
        if(sortCategoryIndex * sortTypeIndex == 0){
            return;
        }
        switch (sortCategoryIndex){
            case 1:
            case 2:
                if(sortTypeIndex == 1){
                    Collections.sort(favoriteList, COMPARE_BY_STOCKNAME_ASCENDING );
                }
                else{
                    Collections.sort(favoriteList, COMPARE_BY_STOCKNAME_DESCENDING );
                }
                break;
            case 3:
                if(sortTypeIndex == 1){
                    Collections.sort(favoriteList, COMPARE_BY_LASTPRICE_ASCENDING);
                }
                else{
                    Collections.sort(favoriteList, COMPARE_BY_LASTPRICE_DESCENDING);
                }
                break;
            case 4:
                if(sortTypeIndex == 1){
                    Collections.sort(favoriteList, COMPARE_BY_CHANGE_ASCENDING);
                }
                else{
                    Collections.sort(favoriteList, COMPARE_BY_CHANGE_DESCENDING);
                }
                break;
        }

        favoriteListAdaptor.notifyDataSetChanged();
    }

    public void updateFavorites(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favoriteList);
        editor.putString("favoriteList", json);
        editor.commit();
    }

    private void updateFavoritePlaceholderMessage(){
        TextView placeholderMessage = (TextView) findViewById(R.id.no_favorite_placeholder);
        if(favoriteList.size() != 0){
            placeholderMessage.setVisibility(View.INVISIBLE);
        }
        else{
           placeholderMessage.setVisibility(View.VISIBLE);
        }
    }

    private void refreshFavoriteList(){
        if(favoriteList.size() == 0){
            return;
        }
        Timer timer = new Timer();
        final FavoriteStockRefreshTask[] refreshTasks = new FavoriteStockRefreshTask [favoriteList.size()];
        requestPendingCount = favoriteList.size();
        refreshButton.setClickable(false);
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                        for(int itr=0; itr<favoriteList.size(); itr++){
                            refreshTasks[itr] = new FavoriteStockRefreshTask(StockSearchActivity.this, favoriteList.get(itr).getStockName(), favoriteList.get(itr).getStockDisplayName());
                            refreshTasks[itr].executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }
        };
        timer.schedule(doAsynchronousTask, 0);
    }

    private void autorefreshFavoriteList(boolean state){
        if(favoriteList.size() == 0){
            return;
        }

        if(!state){
            autorefreshTimer.cancel();
        }
        else
        {
            autorefreshTimer = new Timer();
            final FavoriteStockRefreshTask[] refreshTasks = new FavoriteStockRefreshTask [favoriteList.size()];

            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    for(int itr=0; itr<favoriteList.size(); itr++){
                        refreshTasks[itr] = new FavoriteStockRefreshTask(StockSearchActivity.this, favoriteList.get(itr).getStockName(), favoriteList.get(itr).getStockDisplayName());
                        refreshTasks[itr].executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            };
            autorefreshTimer.schedule(doAsynchronousTask, 0, 5000);
        }
    }

    private ArrayList<StockInformation> fetchFavorites(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("favoriteList", null);
        Type type = new TypeToken<ArrayList<StockInformation>>() {}.getType();
        ArrayList<StockInformation> favorites = gson.fromJson(json, type);
        if(favorites == null){
            return new ArrayList<StockInformation>();
        }
        return favorites;
    }

}
