package com.example.googlebooksapi;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class BookLoader extends AsyncTaskLoader<ArrayList<BookBitmap>> {
    private static final String LOG_TAG = BookLoader.class.getName();

    public String urls = null;
    private ArrayList<Bitmap> mapps = new ArrayList<Bitmap>();
    private boolean badUrl = false;
    private Context cont;
    private ArrayList<BookBitmap> bokic = new ArrayList<BookBitmap>();

    public BookLoader (Context context, String url){
        super(context);
        urls = url;
        cont = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<BookBitmap> loadInBackground() {
        //Dont perform a network request if there are no urls
        if (urls == null){
            return null;
        }

        //Fetch the data from the given query
        ArrayList<Book> books = Utils.fetchBookData(urls);

        //If there is no retrievd data return null
        if(books == null){
            return null;
        }

        //Loop until every image has been downloaded and stored as a bitmap
        for(int i = 0; i < books.size(); i++)
        {
            Log.i(LOG_TAG, "Size " + books.size());
            //Run an asynctask to download the image from the urls we recieved previously
            new DownloadImage().execute(books.get(i).getmImg());
            try{
                //Sleep the thread because there are certain problems with multiple asynctasks at once
            Thread.sleep(350);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            Log.i(LOG_TAG, "This should be printed");

        }
        Log.i(LOG_TAG, "Books KA " + books);
        Log.i(LOG_TAG, "Mapps KA " + mapps);

        //Populate an ArrayList with new BookBitmap objects
        for(int k = 0; k < books.size(); k++){
            Log.i(LOG_TAG, "Why is it wrongg " + books.get(k));
            bokic.add(new BookBitmap(books.get(k), mapps.get(k)));
            Log.i(LOG_TAG, "The other array " + bokic);
        }


        return bokic;
    }
    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Bitmap doInBackground(String... strings) {
            String imageURLString = strings[0];

            //Check if there is a url for the image, if not set a key String "nourl"
            if(imageURLString == "nourl"){
                //Set the bitmap to a bitmap that displays if no cover picture available
                Bitmap bmp = BitmapFactory.decodeResource(cont.getResources(), R.drawable.picture);
                mapps.add(bmp);
                return bmp;
            }

            //Turn a String to a URL
            URL imageURL = Utils.createURL(imageURLString);
            Log.i(LOG_TAG, "This is imageURL " + imageURL);

            //If there are issues with the url return null
            if(imageURL == null || imageURL.equals("")){
                badUrl = true;
                Log.i(LOG_TAG, "No it does not hit");
                return null;
            }
            Log.i(LOG_TAG, "Does it hit it?");
            Bitmap imageBitmap = null;
            HttpURLConnection imageConnection = null;
            InputStream imageStream = null;
            try {
                //Connect and download the image
                Log.i(LOG_TAG, "Connection start");
                imageConnection = (HttpURLConnection) imageURL.openConnection();
                Log.i(LOG_TAG, "Connection resume");
                imageConnection.connect();
                Log.i(LOG_TAG, "Failed connection");
                Log.i(LOG_TAG, "Response code " + imageConnection.getResponseCode());
                imageStream = imageConnection.getInputStream();
                Log.i(LOG_TAG, "This is bitmap stream " + imageStream);
                imageBitmap = BitmapFactory.decodeStream(imageStream);
            } catch (IOException e){
                Log.e(LOG_TAG, "Problems with the connection", e);
            }
            Log.i(LOG_TAG, "This is the image bitmap " + imageBitmap);
            mapps.add(imageBitmap);
            return imageBitmap;

        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.i(LOG_TAG, "This is bitmap " + bitmap);
            Log.i(LOG_TAG, "This is mapps " + mapps);
        }
    }
}
