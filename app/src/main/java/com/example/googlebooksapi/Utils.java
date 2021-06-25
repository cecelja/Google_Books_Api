package com.example.googlebooksapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public final class Utils {
    private static Bitmap imgBitmap = null;
    private static final String LOG_TAG = Utils.class.getName();
    //The constructor is private because this is a helper method class
    //No one should ever need a Utils object.
    private Utils(){
    }

    /*
    * Parse the JSON response data that we recieve from the server
    * @param query is the String json response
    * */
    public static ArrayList<Book> extractBookData(String query){

        //Create a new arraylist which we populate with queried results
        ArrayList<Book> books = new ArrayList<>();
        //Create a new book object which will serve for populating the books array
        Book book;

        try {
            //Check if our query is empty or something is wrong with it and terminate the process.
            if(TextUtils.isEmpty(query)){
                return null;
            }

            //Create a new JSONObject which is the root object of our query
            JSONObject root = new JSONObject(query);
            Log.i(LOG_TAG, "Filip, Root Json object " + query);
            //Navigate the root object and store the JSONArray which has our information
            JSONArray rootArray = root.optJSONArray("items");
            Log.i(LOG_TAG, "Filip, Root array object " + rootArray);
            //Navigate the array in order to populte the arraylist of objects
            for(int i = 0; i < rootArray.length(); i++){
                //Create the JSONObject for the current object in the array
                JSONObject current = rootArray.getJSONObject(i);
                //Navigate the current object and find the object which contains volume info
                //for our book objects
                JSONObject bookObject = current.optJSONObject("volumeInfo");
                Log.i(LOG_TAG, "Filip, Volume infro jsonboject " + bookObject);

                //Store the title information in a string
                String title = bookObject.getString("title");
                Log.i(LOG_TAG, "Filip, TItle of the book " + title);
                //Store the author informtion in a string, however since we are accessing multiple
                //authors we need to turn it into a string
                JSONArray authorsArray = bookObject.getJSONArray("authors");
                Log.i(LOG_TAG, "Filip, These are the authors " + authorsArray);
                String author = "";
                for(int j = 0; j < authorsArray.length(); j++){
                    author = author + authorsArray.getString(j) + " ";
                }

                Log.i(LOG_TAG, "Filip, TItle of the book " + author);
                String imageURL;
                //Create a JSONOBject that contains image url properties of the object
                JSONObject images = bookObject.optJSONObject("imageLinks");
                //Store the url of the given image
                imageURL = images.getString("smallThumbnail");

                Log.i(LOG_TAG, "Filip, The image of the book " + imageURL);

                //Create the new Book object and add it to the array, repeat the loop
                book = new Book(title, author, imageURL);
                Log.i(LOG_TAG, "Filip, What book is it " + book.getmTitle() + book.getmAuthor());

                books.add(book);
            }
        } catch (JSONException e){
        }
        Log.i(LOG_TAG, "Filip, THe books " + books);
        return books;
    }

    public static URL createURL(String urlString){
        URL urls = null;

        try {
            urls = new URL(urlString);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.i(LOG_TAG, "Filip, This is our url string " + urls);
        return urls;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        //Create a new String where to store the jsonresponse
        String jsonResponse = "";
        //Create a new HttpURLCOnnection object with which to connect
        HttpsURLConnection connectionURL = null;
        //Create a new InputStream with which to recieve the bytes sdata
        InputStream stream = null;

        Log.i(LOG_TAG, "Filip, State of the connection " + connectionURL);

        //Checking if there is a valid url.
        //If not, return an empty String.
        if(url == null){
            return jsonResponse;
        }

        try {
            connectionURL = (HttpsURLConnection) url.openConnection();
            connectionURL.setRequestMethod("GET");
            connectionURL.setReadTimeout(10000);
            connectionURL.setConnectTimeout(15000);
            connectionURL.connect();
            Log.i(LOG_TAG, "Filip, State of the connection 2  " + connectionURL);
            int responseCode = connectionURL.getResponseCode();
            Log.i(LOG_TAG, "Filip, Response code is " + connectionURL.getResponseCode());
            if(responseCode == 200){
                stream = connectionURL.getInputStream();
                jsonResponse = readFromStream(stream);
            } else {
                Log.e(LOG_TAG, "Filip, Error with response code" + connectionURL.getResponseCode());
            }
        } catch (IOException e){
            Log.e(LOG_TAG, "Filip, There was a problem with connection.", e);
        } finally {
            if(connectionURL != null) {
                connectionURL.disconnect();
            }
            if(stream != null){
                stream.close();
            }
        }
        Log.i(LOG_TAG, "Filip, Json response is " + jsonResponse);
        return jsonResponse;

    }

    public static String readFromStream(InputStream st) throws IOException{
        StringBuilder output = new StringBuilder();

        if(st != null){
            InputStreamReader reader = new InputStreamReader(st);
            BufferedReader buffer = new BufferedReader(reader);
            String line = buffer.readLine();
            while(line != null){
                output.append(line);
                line = buffer.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<Book> fetchBookData(String urlString){
        URL url = createURL(urlString);
        Log.i(LOG_TAG, "Filip, This is the url " + url);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e){
            Log.e(LOG_TAG, "Filip, Problem with the network request", e);
        }
        ArrayList<Book> books = extractBookData(jsonResponse);
        Log.i(LOG_TAG, "Filip, These are the books" + books);
        return books;
    }
}
