package com.sfsoft.newsreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ArticleActivity extends AppCompatActivity {

    // Define view elements
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        // Create webView
        webView = findViewById(R.id.webView);

        // Initialise web client
        webView.setWebViewClient(new WebViewClient());

        // Initialise intent
        Intent intent = getIntent();
        // Read extra information and pass it to the webView
        webView.loadUrl(intent.getStringExtra("arturl"));

    }
}