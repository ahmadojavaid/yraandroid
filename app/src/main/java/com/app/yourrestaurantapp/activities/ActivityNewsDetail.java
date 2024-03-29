package com.app.yourrestaurantapp.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.yourrestaurantapp.Config;
import com.app.yourrestaurantapp.R;
import com.app.yourrestaurantapp.json.JsonConfig;
import com.app.yourrestaurantapp.json.JsonUtils;
import com.app.yourrestaurantapp.models.ItemNews;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityNewsDetail extends AppCompatActivity {

    String str_cid, str_cat_id, str_cat_image, str_cat_name, str_title, str_image, str_desc, str_date;
    TextView news_title, news_date;
    WebView news_desc;
    ImageView img_news, img_fav;
    LinearLayout linearLayout;
    List<ItemNews> arrayItemNews;
    ItemNews ItemNews;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ProgressBar progressBar;
    CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getResources().getString(R.string.news_detail));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        img_news = (ImageView) findViewById(R.id.image);
        linearLayout = (LinearLayout) findViewById(R.id.date_display);

        news_title = (TextView) findViewById(R.id.title);
        news_date = (TextView) findViewById(R.id.date);
        news_desc = (WebView) findViewById(R.id.desc);

        arrayItemNews = new ArrayList<ItemNews>();

        if (JsonUtils.isNetworkAvailable(ActivityNewsDetail.this)) {
            new MyTask().execute(Config.ADMIN_PANEL_URL + "/api/get-news.php?nid=" + JsonConfig.NEWS_ITEMID);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
        }

    }

    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressBar.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);

            if (null == result || result.length() == 0) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
                coordinatorLayout.setVisibility(View.GONE);
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConfig.CATEGORY_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemNews objItem = new ItemNews();
//
//                        objItem.setCId(objJson.getString(JsonConfig.CATEGORY_ITEM_CID));
//                        objItem.setCategoryName(objJson.getString(JsonConfig.CATEGORY_ITEM_NAME));
//                        objItem.setCategoryImage(objJson.getString(JsonConfig.CATEGORY_ITEM_IMAGE));
                        objItem.setCatId(objJson.getString(JsonConfig.CATEGORY_ITEM_CAT_ID));
                        objItem.setNewsImage(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSIMAGE));
                        objItem.setNewsHeading(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSHEADING));
                        objItem.setNewsDescription(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSDESCRI));
                        objItem.setNewsDate(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSDATE));

                        arrayItemNews.add(objItem);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setAdapterToRecyclerView();
            }

        }
    }

    public void setAdapterToRecyclerView() {

        if (Config.ENABLE_RTL_MODE) {

            ItemNews = arrayItemNews.get(0);
            str_cid = ItemNews.getCId();
            str_cat_id = ItemNews.getCatId();
            str_title = ItemNews.getNewsHeading();
            str_desc = ItemNews.getNewsDescription();
            str_image = ItemNews.getNewsImage();
            str_date = ItemNews.getNewsDate();

            news_title.setText(str_title);
            news_date.setText(str_date);

            news_desc.setBackgroundColor(Color.parseColor("#ffffff"));
            news_desc.setFocusableInTouchMode(false);
            news_desc.setFocusable(false);
            news_desc.getSettings().setDefaultTextEncodingName("UTF-8");

            WebSettings webSettings = news_desc.getSettings();
            Resources res = getResources();
            int fontSize = res.getInteger(R.integer.font_size);
            webSettings.setDefaultFontSize(fontSize);
            webSettings.setJavaScriptEnabled(true);

            String mimeType = "text/html; charset=UTF-8";
            String encoding = "utf-8";
            String htmlText = str_desc;

            String text = "<html dir='rtl'><head>"
                    + "<style type=\"text/css\">body{color: #525252;}"
                    + "</style></head>"
                    + "<body>"
                    + htmlText
                    + "</body></html>";

            news_desc.loadData(text, mimeType, encoding);

            Picasso.with(this).load(Config.ADMIN_PANEL_URL + "/upload/news/" + ItemNews.getNewsImage()).placeholder(R.drawable.ic_loading).into(img_news, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) img_news.getDrawable()).getBitmap();
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                        }
                    });
                }

                @Override
                public void onError() {

                }
            });

        } else {

            ItemNews = arrayItemNews.get(0);
            str_cid = ItemNews.getCId();
            str_cat_id = ItemNews.getCatId();
            str_title = ItemNews.getNewsHeading();
            str_desc = ItemNews.getNewsDescription();
            str_image = ItemNews.getNewsImage();
            str_date = ItemNews.getNewsDate();

            news_title.setText(str_title);
            news_date.setText(str_date);

            news_desc.setBackgroundColor(Color.parseColor("#ffffff"));
            news_desc.setFocusableInTouchMode(false);
            news_desc.setFocusable(false);
            news_desc.getSettings().setDefaultTextEncodingName("UTF-8");

            WebSettings webSettings = news_desc.getSettings();
            Resources res = getResources();
            int fontSize = res.getInteger(R.integer.font_size);
            webSettings.setDefaultFontSize(fontSize);
            webSettings.setJavaScriptEnabled(true);

            String mimeType = "text/html; charset=UTF-8";
            String encoding = "utf-8";
            String htmlText = str_desc;

            String text = "<html><head>"
                    + "<style type=\"text/css\">body{color: #525252;}"
                    + "</style></head>"
                    + "<body>"
                    + htmlText
                    + "</body></html>";

            news_desc.loadData(text, mimeType, encoding);

            Picasso.with(this).load(Config.ADMIN_PANEL_URL + "/upload/news/" + ItemNews.getNewsImage()).placeholder(R.drawable.ic_loading).into(img_news, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) img_news.getDrawable()).getBitmap();
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                        }
                    });
                }

                @Override
                public void onError() {

                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_share:

                String formattedString = android.text.Html.fromHtml(str_desc).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, str_title + "\n" + formattedString + "\n" + getResources().getString(R.string.share_content) + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
