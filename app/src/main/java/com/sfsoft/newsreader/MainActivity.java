package com.sfsoft.newsreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // View elements
    ListView newsListView;

    // Array variables
    ArrayList<String> titleList = new ArrayList<String>();
    ArrayList<String> contentList = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    // SQLite database to store content
    SQLiteDatabase articlesDB;


    /**
     * Class that downloads data in the background from the API
     */
    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            // String with data download result
            String result = "";
            // url and httpurlconnection variables
            URL url;
            HttpURLConnection httpURLConnection = null;

            // Get the JSON raw data from the URL
            try{
                // Get the first element of the url string
                url = new URL(urls[0]);
                // Open and establish the connection
                httpURLConnection = (HttpURLConnection) url.openConnection();
                // Define the input stream
                InputStream inputStream = httpURLConnection.getInputStream();
                // Define the input stream reader
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                // Read the data
                int data = inputStreamReader.read();
                while (data != -1){
                    // Cast the data into characters
                    char currentData = (char) data;
                    // Add the characters to the result String
                    result += currentData;

                    // Read the next data
                    data = inputStreamReader.read();
                }

                // Clear the DB to obtain new information
                articlesDB.execSQL("DELETE FROM articles");

                // Create the JSON Array to read the data
                JSONArray jsonArray = new JSONArray(result);
                // Maximum number of items to read
                int maxItems = 2;

                // If there are less than 20 items, then define new max number
                if(jsonArray.length() < 2){
                    maxItems = jsonArray.length();
                }

                // Iterate over the items and get the data
                for (int i=0; i < maxItems; i++){
                    // Obtain the article ID
                    String articleID = jsonArray.getString(i);
                    // Read the new information
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/" + articleID +".json?print=pretty");

                    // Open and establish the connection
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    // Define the input stream
                    inputStream = httpURLConnection.getInputStream();
                    // Define the input stream reader
                    inputStreamReader = new InputStreamReader(inputStream);

                    // String with information
                    String articleInfo = "";
                    // Read the data
                    data = inputStreamReader.read();
                    while (data != -1){
                        // Cast the data into characters
                        char currentData = (char) data;
                        // Add the characters to the result String
                        articleInfo += currentData;

                        // Read the next data
                        data = inputStreamReader.read();
                    }

                    // Create a JSON Object to retrieve the data
                    JSONObject jsonObject = new JSONObject(articleInfo);
                    // Check if the object has a title and url
                    if (!jsonObject.isNull("title") || jsonObject.isNull("url") ){
                        // Get the title and url strings
                        String articleTitle = jsonObject.getString("title");
                        String articleUrl = jsonObject.getString("url");

                        // Download the information from the urls
                        url = new URL(articleUrl);
                        // Establish the connection
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        // Initiate input stream and stream reader
                        inputStream = httpURLConnection.getInputStream();
                        inputStreamReader = new InputStreamReader(inputStream);

                        // Read the data
                        data = inputStreamReader.read();
                        // Make string with articles content
                        String articleContent = "";
                        // Download the data
                        while (data != -1){
                            char current = (char) data;
                            articleContent += current;
                            data = inputStreamReader.read();
                        }

                        Log.i("html content", articleContent);

                        // String to insert the information into the DB
                        String sqlData = "INSERT INTO articles (articleId, title, content) VALUES (?, ?, ?)";
                        // SQL statement to send the downloaded information
                        SQLiteStatement sqLiteStatement = articlesDB.compileStatement(sqlData);
                        // Pass information to the DB
                        sqLiteStatement.bindString(1, articleID);
                        sqLiteStatement.bindString(2, articleTitle);
                        sqLiteStatement.bindString(3, articleContent);

                        // Execute the statement
                        sqLiteStatement.execute();

                    }
                }

                // Return the result String
                Log.i("Top stories result", result);
                return result;

            }
            catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Method that updates the view after downloading the information
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Update the ListView
            updateListView();
        }
    }

    /**
     * Method that updates the listview information
     */
    public void updateListView(){

        // Create the cursor to access the database
        Cursor cursor = articlesDB.rawQuery("SELECT * FROM articles", null);

        int titleIndex = cursor.getColumnIndex("title");
        int contentIndex = cursor.getColumnIndex("content");

        cursor.moveToFirst();
        try{
            // Move cursor to the first position
            if (cursor.moveToFirst()){
                // Clear arrayList content
                titleList.clear();
                contentList.clear();

                do{
                    titleList.add(cursor.getString(titleIndex));
                    contentList.add(cursor.getString(contentIndex));
                }while (cursor.moveToNext());

                // Notify ArrayAdapter of the changes
                arrayAdapter.notifyDataSetChanged();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            cursor.close();
        }

    }

    /**
     * Main method that initialises elements on the Main Activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create view elements
        newsListView = findViewById(R.id.newsListView);
        // Create array adapter and apply to listView
        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, titleList);
        newsListView.setAdapter(arrayAdapter);

        // Create or open DB
        articlesDB = this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);
        // Create the table articles with" id, articleId, title, and content
        articlesDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY, articleId INTEGER, title VARCHAR, content VARCHAR)");

        // Create downloadTask
        DownloadTask downloadTask = new DownloadTask();
        try{
            downloadTask.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Enable click listener in ListView
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create a new intent to open Article webView activity
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                // Provide content to open in the webView
                intent.putExtra("content", contentList.get(position));
                // Start new activity
                startActivity(intent);
            }
        });

        // Update listView content
        updateListView();

    }
}
