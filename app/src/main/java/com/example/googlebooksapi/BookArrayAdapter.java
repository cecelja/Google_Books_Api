package com.example.googlebooksapi;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.artjimlop.altex.AltexImageDownloader;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class BookArrayAdapter extends ArrayAdapter<BookBitmap> {

    private static final String LOG_TAG = BookArrayAdapter.class.getName();
    private Activity cont;
    private Bitmap mapp;
    private ArrayList<Bitmap> mapps;
    private boolean badUrl = false;

    public BookArrayAdapter(Activity context, ArrayList<BookBitmap> books){
        super(context,0, books);
        cont = context;
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //First we need to check if the existing view is being used, otherwise we inflate the view
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        //We need to find the current Book object at the position
        BookBitmap currentBook = getItem(position);

        TextView title = (TextView) listItemView.findViewById(R.id.title_view);
        Log.i(LOG_TAG, "This is the title " + currentBook.getmTitle());
        title.setText(currentBook.getmTitle());

        TextView author = (TextView) listItemView.findViewById(R.id.author_view);
        Log.i(LOG_TAG, "This is the title " + currentBook.getmAuthor());
        author.setText("By " + currentBook.getmAuthor());

        ImageView coverImage = (ImageView) listItemView.findViewById(R.id.image_view);
        Log.i(LOG_TAG, "This is the image " + currentBook.getmBitmap());

        Log.i(LOG_TAG, "Our bitmap array " + mapps);
        if(badUrl = true){
            coverImage.setImageResource(R.drawable.picture);
        }
        coverImage.setImageBitmap(currentBook.getmBitmap());

        return listItemView;
    }

    /**public class DownloadImage extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Bitmap doInBackground(String... strings) {
            String imageURLString = strings[0];

            URL imageURL = Utils.createURL(imageURLString);
            Log.i(LOG_TAG, "This is imageURL " + imageURL);

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
            return imageBitmap;

        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mapps.add(bitmap);
            Log.i(LOG_TAG, "This is bitmap " + mapp);
        }

    }**/
}

