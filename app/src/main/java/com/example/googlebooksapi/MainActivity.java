package com.example.googlebooksapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
    private Runnable runnable;

    private static final int BOOK_LOADER_ID = 1;
    private static String GOOGLE_BOOKS_URL =
            "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=6&orderBy=newest";
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

        //Create a new adapter and populate it with a null arrayList
        mAdapter = new BookArrayAdapter(this, new ArrayList<BookBitmap>());
        //set the adapter to the listview

        //We create a onClickListener for our button which controls and sends the query to the server
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting the text that was entered from the EditText button to a seperate variable
                query = searchBox.getText().toString();
                //Controlling if there is no text entered that it prints a message for a invalid query
                if(query.equals("")){
                    Toast.makeText(getApplicationContext(),"Insert a valid query.",Toast. LENGTH_SHORT).show();
                }
                //Checking if a valid query has been entered
                if(countSpaces(query) == 0){
                    //Modifyying the url with the given query
                    GOOGLE_BOOKS_URL =  "https://www.googleapis.com/books/v1/volumes?q="+query+"&maxResults=6&orderBy=newest";
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
        BookLoader loader = new BookLoader(this, GOOGLE_BOOKS_URL);
        return loader;
    }


    @Override
    public void onLoadFinished(Loader<ArrayList<BookBitmap>> loader, ArrayList<BookBitmap> books) {
        //Here we clear the adapter with anydata it previously had
        mAdapter.clear();
        Log.i(LOG_TAG, "Filip, Here we are in the onLoadFinished method, before populating the dapter");
        //We need to check if we have the requested data, and after that we populate the adapter with it
        if(books != null && !books.isEmpty()){
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

}