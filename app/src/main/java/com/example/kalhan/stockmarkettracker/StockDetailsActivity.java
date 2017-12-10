package com.example.kalhan.stockmarkettracker;
import com.example.kalhan.stockmarkettracker.Model.NewsItem;
import com.example.kalhan.stockmarkettracker.Model.StockDetailsInterface;
import com.example.kalhan.stockmarkettracker.Model.StockInformation;
import android.app.Activity;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StockDetailsActivity extends AppCompatActivity implements StockDetailsInterface{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ArrayList<StockInformation> favoriteList = null;
    private String stockName, stockDisplayText, shareURL;
    private StockInformation stockInfo;
    private boolean isFavorite;

    @Override
    public void setShareableNewsURL(String url){
        shareURL = url;
    }

    @Override
    public void setStockData(StockInformation stockdata){
        stockInfo = stockdata;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_stock_details);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        stockName = intent.getStringExtra("stockName");
        stockDisplayText = intent.getStringExtra("displayName");

        // fetch favorites list
        favoriteList = fetchFavorites();
        isFavorite = checkIfFavorites(stockName);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), stockName);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.backgroundGrey));
        tabLayout.setTabTextColors(getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorPrimaryDark));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        getSupportActionBar().setTitle(stockDisplayText);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.actionBarColor)));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stock_details, menu);
        if(!isFavorite){
            menu.findItem(R.id.action_favorite).setIcon(getDrawable(R.drawable.heart_empty));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        else if(item.getItemId() == R.id.action_favorite){
            if(isFavorite){
                removeFromFavorites();
                isFavorite = false;
                item.setIcon(getDrawable(R.drawable.heart_empty));
            }
            else{
                if(stockInfo != null){
                    stockInfo.setStockDisplayName(stockDisplayText);
                    saveToFavorites();
                    isFavorite = true;
                    item.setIcon(getDrawable(R.drawable.heart_full));
                }
                // Make snackbar informing waiting for request to finish.
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<StockInformation> fetchFavorites(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("favoriteList", null);
        Type type = new TypeToken<ArrayList<StockInformation>>() {}.getType();
        return gson.fromJson(json, type);
    }
    private boolean checkIfFavorites(String stockName){
           if(favoriteList == null){
               return false;
           }
           else
           {
               for(int itr=0; itr<favoriteList.size(); itr++){
                   if(favoriteList.get(itr).getStockName().equals(stockName)){
                       return true;
                   }
               }
           }
           return false;
    }

    private void saveToFavorites(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        boolean updated = false;
        if(favoriteList == null){
            favoriteList = new ArrayList<StockInformation>();
            favoriteList.add(stockInfo);
        }
        else{
            for(int itr=0; itr<favoriteList.size(); itr++){
                if(favoriteList.get(itr).getStockName().equals(stockInfo.getStockName())){
                    favoriteList.get(itr).setChange(stockInfo.getChange());
                    favoriteList.get(itr).setChangePercentage(stockInfo.getChangePercentage());
                    updated = true;
                    break;
                }
            }

            if(!updated){
                favoriteList.add(stockInfo);
            }
        }
        String json = gson.toJson(favoriteList);
        editor.putString("favoriteList", json);
        editor.commit();
    }

    private void removeFromFavorites(){
        for(int itr=0; itr<favoriteList.size(); itr++){
            if(favoriteList.get(itr).getStockName().equals(stockName)){
                favoriteList.remove(itr);
                break;
            }
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favoriteList);
        editor.putString("favoriteList", json);
        editor.commit();
    }


    /**
     * A StockInfo fragment.
     */
    public static class StockInfoFragment extends Fragment {

        private String stockSymbol,category,stockDisplayText;
        private WebAppInterface webInterface = null;
        StockDetailsInterface mStockDetailsInterface;

        public StockInfoFragment() {
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);


            if (context instanceof Activity){
                try{
                    mStockDetailsInterface=(StockDetailsInterface) context;
                } catch (ClassCastException e) {
                    throw new ClassCastException(context.toString() + " must implement StockDetailsInterface");
                }
            }
        }

        /**
         * Returns a new instance of this fragment.
         * number.
         */
        public static StockInfoFragment newInstance(String stockName) {
            StockInfoFragment fragment = new StockInfoFragment();
            Bundle args = new Bundle();
            args.putString("stockSymbol", stockName);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            stockSymbol = getArguments().getString("stockSymbol");
            stockDisplayText = getArguments().getString("stockDisplayText");
            View rootView = displayStockInfo(inflater, container);
            return rootView;
        }

        public void updateStockTableInfo(View view){
            final View rootView = view;

                new DownloadStockInfoTask() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if(getView() == null){
                            return;
                        }
                        ProgressBar loader = (ProgressBar) rootView.findViewById(R.id.stock_loading_indicator);
                        loader.getIndeterminateDrawable().setColorFilter(0xFF000000, android.graphics.PorterDuff.Mode.MULTIPLY);
                        loader.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected void onPostExecute(StockInformation stockInfo) {
                        super.onPostExecute(stockInfo);
                        if(getView() == null){
                            return;
                        }
                        ProgressBar loader = (ProgressBar)rootView.findViewById(R.id.stock_loading_indicator);
                        loader.setVisibility(View.GONE);
                        TextView error = rootView.findViewById(R.id.error_message);
                        //CheckBox favorite = (CheckBox) rootView.findViewById(R.id.favorite_button);

                        if(stockInfo != null){
                            mStockDetailsInterface.setStockData(stockInfo);
                            DecimalFormat df = new DecimalFormat("#.##");
                            df.setRoundingMode(RoundingMode.CEILING);

                            TextView change = rootView.findViewById(R.id.stock_table_change);
                            change.setText(df.format(stockInfo.getChange())+" "+stockInfo.getChangePercentage());
                            if(stockInfo.getChange() < 0){
                                ImageView changeSign = rootView.findViewById(R.id.stock_table_change_symbol);
                                changeSign.setImageDrawable(getResources().getDrawable(R.drawable.red_arrow_down));
                            }
                            TextView symbol =rootView.findViewById(R.id.stock_table_name);
                            symbol.setText(String.valueOf(stockInfo.getStockName()));
                            TextView range = rootView.findViewById(R.id.stock_table_range);
                            range.setText(String.valueOf(stockInfo.getRangeValue()));
                            TextView lastprice = rootView.findViewById(R.id.stock_table_last_price);
                            lastprice.setText(df.format(stockInfo.getLastPrice()));

                            TextView open = rootView.findViewById(R.id.stock_table_open);
                            open.setText(String.valueOf(stockInfo.getOpenPrice()));
                            TextView close = rootView.findViewById(R.id.stock_table_close);
                            close.setText(String.valueOf(stockInfo.getClosePrice()));
                            TextView volume = (TextView) rootView.findViewById(R.id.stock_table_volume);
                            volume.setText(df.format(stockInfo.getVolume()));
                            LinearLayout table = (LinearLayout) rootView.findViewById(R.id.stock_info_table);

                            // Timestamp
                            Date d = new Date();
                            long utc = d.getTime() + (d.getTimezoneOffset() * 60000);
                            Date nycTime = new Date(utc + (3600000*-5));
                            TextView timestamp = rootView.findViewById(R.id.stock_table_timestamp);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try{
                                Date endTime = simpleDateFormat.parse(stockInfo.getTimestamp()+" 16:00:00");
                                if (endTime.after(nycTime)){
                                    timestamp.setText(simpleDateFormat.format(nycTime) + " EDT");

                                }
                                else{
                                    timestamp.setText(stockInfo.getTimestamp()+ " 16:00:00 EDT");
                                    close.setText(lastprice.getText());
                                }
                            }
                            catch (ParseException exp){
                                exp.printStackTrace();
                            }


                            table.setVisibility(View.VISIBLE);
                            error.setVisibility(View.GONE);
                            //favorite.setEnabled(true);
                        }
                        else{
                            error.setVisibility(View.VISIBLE);
                            //favorite.setEnabled(false);
                        }

                    }
                }.execute(stockSymbol);

        }
        private View displayStockInfo(LayoutInflater inflater, ViewGroup container){
            View rootView = inflater.inflate(R.layout.fragment_stock_details, container, false);
            updateStockTableInfo(rootView);

            final WebView webview = (WebView)rootView.findViewById(R.id.indicator_graph);
            Spinner spinner = (Spinner) rootView.findViewById(R.id.indicator_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.indicator_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown);
            spinner.setAdapter(adapter);
            spinner.getBackground().setColorFilter(getResources().getColor(R.color.yellowTint), PorterDuff.Mode.SRC_ATOP);

            category = spinner.getSelectedItem().toString();
            webInterface = new WebAppInterface(getContext(), stockSymbol, category);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.addJavascriptInterface(webInterface, "Android");
            webview.loadUrl("file:///android_asset/indicator.html");

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    TextView selectedText =  (TextView) parentView.getChildAt(0);
                    if (selectedText != null) {
                        selectedText.setTextColor(Color.WHITE);
                    }

                    if(!category.equals(parentView.getItemAtPosition(position))){
                        category = (String) parentView.getItemAtPosition(position);
                        webview.getSettings().setJavaScriptEnabled(true);
                        webInterface = new WebAppInterface(getContext(), stockSymbol, category);
                        webview.addJavascriptInterface(webInterface, "Android");
                        webview.setBackgroundColor(getResources().getColor(R.color.backgroundGrey));
                        webview.loadUrl("file:///android_asset/indicator.html");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }

            });
            return rootView;
        }
    }

    /**
     * A historical chart fragment.
     */
    public static class HistoricalChartFragment extends Fragment {

        private String stockSymbol;
        private WebAppInterface webInterface = null;

        public HistoricalChartFragment() {
        }

        public static HistoricalChartFragment newInstance(String stockName) {
            HistoricalChartFragment fragment = new HistoricalChartFragment();
            Bundle args = new Bundle();
            args.putString("stockSymbol", stockName);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            stockSymbol = getArguments().getString("stockSymbol");
            View rootView = displayHistoricalCharts(inflater, container);
            return rootView;
        }


        private View displayHistoricalCharts(LayoutInflater inflater, ViewGroup container){
            final WebView rootView = (WebView) inflater.inflate(R.layout.fragment_historical_chart, container, false);

            WebView webview = (WebView)rootView;
            webview.getSettings().setJavaScriptEnabled(true);
            //webview.getSettings().setSupportMultipleWindows(true);
            //webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webview.addJavascriptInterface(new WebAppInterface(getContext(), stockSymbol), "Android");
            webview.setBackgroundColor(getResources().getColor(R.color.backgroundGrey));
            //webView.loadData("", "text/html", null);
            webview.loadUrl("file:///android_asset/historical.html");
            return rootView;
        }
    }

    /**
     * A News Feed fragment.
     */
    public static class NewsFeedFragment extends Fragment {

        private String stockSymbol;
        protected ArrayList<NewsItem> newsArticles = new ArrayList<NewsItem>();
        protected NewsListAdapter newsFeedAdapter;
        ProgressBar loader;

        public NewsFeedFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static NewsFeedFragment newInstance(String stockName) {
            NewsFeedFragment fragment = new NewsFeedFragment();
            Bundle args = new Bundle();
            args.putString("stockSymbol", stockName);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

             stockSymbol = getArguments().getString("stockSymbol");
             return displayNewsFeed(inflater, container);
        }

        private View displayNewsFeed(LayoutInflater inflater, ViewGroup container){

            final LinearLayout newsFeedContainer = (LinearLayout) inflater.inflate(R.layout.fragment_news_feed, container, false);
            final ListView newsFeedView = (ListView) newsFeedContainer.findViewById(R.id.news_feed);
            loader = (ProgressBar) newsFeedContainer.findViewById(R.id.news_loading_indicator);
            newsFeedAdapter = new NewsListAdapter(getContext(), R.layout.news_row, newsArticles);
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


            DownloadNewsFeedTask newsFeedTask = new DownloadNewsFeedTask(this);
            newsFeedTask.execute(stockSymbol);
            return newsFeedContainer;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private String stockName;

        public SectionsPagerAdapter(FragmentManager fm, String stockName) {
            super(fm);
            this.stockName = stockName;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0 : return StockInfoFragment.newInstance(stockName);
                case 1: return HistoricalChartFragment.newInstance(stockName);
                case 2: return NewsFeedFragment.newInstance(stockName);
                default: return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

}
