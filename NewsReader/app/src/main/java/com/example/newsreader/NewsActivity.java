package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        WebView newsWebView = findViewById(R.id.newsWebView);
        newsWebView.getSettings().setJavaScriptEnabled(true);
        newsWebView.setWebViewClient(new WebViewClient());

        Intent intent = getIntent();
        int id = intent.getIntExtra("newsID", -1);
        if (id != -1) {
            newsWebView.loadUrl(MainActivity.newsUrlList.get(id));
        }
    }
}