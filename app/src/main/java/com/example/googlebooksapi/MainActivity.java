package com.example.googlebooksapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<BookBitmap>> {

    private BookArrayAdapter mAdapter;
    private EditText searchBox;
    public String query = "";
    private Button search;
    private TextView textView;
    public boolean flag_loading = false;
    private Runnable runnable;
    private ArrayList<BookBitmap> hi = new ArrayList<BookBitmap>();
    private ProgressBar progressBar;
    private static final int BOOK_LOADER_ID = 1;
    private static String GOOGLE_BOOKS_URL =
            "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=10&orderBy=newest";
    private static final String LOG_TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler handler = new Handler();
        LoaderManager.LoaderCallbacks callbacks = this;
        Log.i(LOG_TAG, "Filip, The application has started");
        //Find the list view on activity_main
        ListView bookListView = (ListView) findViewById(R.id.list);
        //Find the edittext view in the layout
        searchBox = (EditText) findViewById(R.id.search_box);
        //Get the query that was typed by the user
        search = (Button) findViewById(R.id.button);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        textView = (TextView) findViewById(R.id.textview);
        bookListView.setEmptyView(textView);

        //Create a new adapter and populate it with a null arrayList
        mAdapter = new BookArrayAdapter(this, new ArrayList<BookBitmap>());


        //Implement the scrolling feature where when a user hits an end it scrolling
        //The app loads more entries in it
        bookListView.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(flag_loading == false)
                    {
                        flag_loading = true;
                    }
                }
            }
        });

        //We create a onClickListener for our button which controls and sends the query to the server
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Setting the text that was entered from the EditText button to a seperate variable
                String q = searchBox.getText().toString();

                //Parse the query
                query = parseQuery(q);

                //Controlling if there is no text entered that it prints a message for a invalid query
                if(query.equals("")){
                    Toast.makeText(getApplicationContext(),"Insert a valid query.",Toast. LENGTH_SHORT).show();
                    return;
                }
                //Checking if a valid query has been entered
                if(countSpaces(query) == 0){
                    //Modifyying the url with the given query
                    GOOGLE_BOOKS_URL =  "https://www.googleapis.com/books/v1/volumes?q="+query+"&maxResults=10";
                    Log.i(LOG_TAG, "Filip, WHERE IS THE URL " + GOOGLE_BOOKS_URL);
                    Log.i(LOG_TAG, "Filip, THE QUERY IS " + query);
                    //After the modification clear the focus from the query
                    searchBox.clearFocus();
                    //Run the handler thread which fetches the data from the api and displays it
                    handler.post(runnable);
                } else {
                    //If multiple words are entered clear the focus, clear the edittext and print a toast sending a invalid query message
                    Toast.makeText(getApplicationContext(),"Insert a valid query.",Toast. LENGTH_SHORT).show();
                    searchBox.clearFocus();
                    searchBox.setText("");
                }
            }
        });
        //Define a new runnable thread
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.i(LOG_TAG, "Filip, Before the loader manger");
                //Set the addapter to the listview
                bookListView.setAdapter(mAdapter);
                //Create a new loadermanger
                LoaderManager loaderManager = getSupportLoaderManager();
                //Restart the loderManager with a new url after the button has been pressed
                loaderManager.restartLoader(BOOK_LOADER_ID, null, callbacks);
                //Set the text to nothing
                searchBox.setText("");
            }
        };

    }


    @NonNull
    @Override
    public Loader<ArrayList<BookBitmap>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "Filip, Here we are in the onCreateLoader method");
        Log.i(LOG_TAG, "Filip, WHERE IS ONCREATE " + GOOGLE_BOOKS_URL);
        //Clear the adapter of previous data
        mAdapter.clear();
        //Set the progress bar to be visible while the data is being loaded
        progressBar.setVisibility(View.VISIBLE);
        BookLoader loader = new BookLoader(this, GOOGLE_BOOKS_URL);
        return loader;
    }


    @Override
    public void onLoadFinished(Loader<ArrayList<BookBitmap>> loader, ArrayList<BookBitmap> books) {
        //Here we clear the adapter with anydata it previously had
        mAdapter.clear();
        //If the loader returns null or there is no internet create a simple message
        textView.setText("No books found.");
        //After the data has loaded hide the progress bar
        progressBar.setVisibility(View.GONE);
        Log.i(LOG_TAG, "Filip, Here we are in the onLoadFinished method, before populating the dapter");
        //We need to check if we have the requested data, and after that we populate the adapter with it
        if(books != null && !books.isEmpty()){
            hi = books;
            mAdapter.addAll(books);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<BookBitmap>> loader) {
        mAdapter.clear();
    }

    //With countSpaces function we count if the query String has multiple words
    //Since we are sending a query to the server we need to enter only 1 word.
    //THis function helps us identify that so we can print a invalid query response
    private static int countSpaces(String queryStr){
        int spaces = queryStr.length() - queryStr.replaceAll(" ", "").length();
        return  spaces;
    }

    private int onEnd(Context cont){
        Toast.makeText(cont,"You reached the end.",Toast. LENGTH_SHORT).show();
        flag_loading = false;
        int position = 0;
        return position;
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    //Function that parses the query string correctly for the query api call
    private String parseQuery(String q){
        String parsedQ = q.replaceAll(" ", "+");
        return parsedQ;
    }
}