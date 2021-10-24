package com.curtin.mathtest.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.curtin.mathtest.Database.UserHandler;
import com.curtin.mathtest.Database.UserImageHandler;
import com.curtin.mathtest.Model.AppAlerter;
import com.curtin.mathtest.Model.User;
import com.curtin.mathtest.R;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SearchPhotoActivity extends AppCompatActivity implements ListItemClickListener{

    private SearchView searchView;
    private Button moreButton;
    private RecyclerView rv;
    private AppAlerter alert;
    private Context ctx;
    private ArrayList<Bitmap> images;
    private UserImageHandler userImageHandler;
    private String contact;
    private ProgressBar downloadBar;
    private UserHandler userHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_photo);

        Intent intent = getIntent();
        contact = intent.getStringExtra("CONTACT");

        ctx = getApplicationContext();
        userHandler = new UserHandler(ctx);
        alert = new AppAlerter(ctx);
        images = new ArrayList<>();
        userImageHandler = new UserImageHandler(ctx);
        searchView = (SearchView) findViewById(R.id.searchBar);
        downloadBar = (ProgressBar) findViewById(R.id.downloadProgressBar);
        downloadBar.setVisibility(View.INVISIBLE);
        rv = (RecyclerView) findViewById(R.id.photoList);
        ImageAdapter adapter = new ImageAdapter(this);
        rv.setAdapter(adapter);
        LinearLayoutManager rvLayout  = new LinearLayoutManager(ctx);
        rv.setLayoutManager(rvLayout);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String imageToSearch) {
                if(imageToSearch.trim().equals("")) {
                    alert.error("Empty search query.");
                    return false;
                }
                images = new ArrayList<>();
                loadImagesOnSearch(imageToSearch);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }


    private void loadImagesOnSearch(String searchKey) {
        DownloadImages downloader = new DownloadImages(searchKey);
        downloader.start(this);
    }

    @Override
    public void onListItemClick(int position) {
        if(userImageHandler.hasCustomProfileImage(contact)) {
            userImageHandler.updateUserProfile(contact,contact,images.get(position));
        }
        else {
            userImageHandler.setUserImage(contact,images.get(position));
        }
    }


    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        private final ListItemClickListener mOnClickListener;

        public ImageAdapter(ListItemClickListener mOnClickListener) {
            this.mOnClickListener = mOnClickListener;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageViewHolder vHolder = new ImageViewHolder(LayoutInflater.from(ctx), parent );
            return vHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            holder.bind(images.get(position));
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        private class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            private ImageView image;
            private Bitmap imageBitmap;
            public ImageViewHolder(LayoutInflater li, ViewGroup parent) {
                super(li.inflate(R.layout.image_cell,parent,false));
                image = (ImageView) itemView.findViewById(R.id.queryImage);
            }

            public void bind(Bitmap imageBitmap) {
                this.imageBitmap = imageBitmap;
                image.setImageBitmap(imageBitmap);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(userImageHandler.hasCustomProfileImage(contact)) {
                            userImageHandler.updateUserProfile(contact,contact,imageBitmap);
                        }
                        else {
                            userImageHandler.setUserImage(contact,imageBitmap);
                        }
                        User user = userHandler.getUser(contact);
                        alert.displayMessage("Set profile image for " + user.getFullName());
                    }
                });
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                mOnClickListener.onListItemClick(position);
            }
        }
    }



    private class DownloadImages {
        private Thread downloadThread;
        private String searchDownload;
        public static final int LIMIT = 50;
        public DownloadImages(String searchKey) {
            this.searchDownload = searchKey;
        }

        public void start(ListItemClickListener mOnListItemClick) {
            Runnable downloadTask = () -> {
                pictureRetrievalTask(searchDownload);
            };
            downloadThread = new Thread(downloadTask,"Download image thread");
            downloadThread.start();
            Runnable waitForThread = () -> {
                while(downloadThread.isAlive()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadBar.setVisibility(View.VISIBLE);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        downloadBar.setVisibility(View.INVISIBLE);
                        ImageAdapter adapter = new ImageAdapter(mOnListItemClick);
                        rv.setAdapter(adapter);
                        LinearLayoutManager rvLayout  = new LinearLayoutManager(ctx);
                        rv.setLayoutManager(rvLayout);
                    }
                });

            };
            Thread t = new Thread(waitForThread,"Loading screen");
            t.start();

        }


        public void interrupt() {
            downloadThread.interrupt();
        }

        private String searchRemoteAPI(String searchKey){
            String data = null;
            Uri.Builder url = Uri.parse("https://pixabay.com/api/").buildUpon();
            url.appendQueryParameter("key","23319229-94b52a4727158e1dc3fd5f2db");
            url.appendQueryParameter("q",searchKey);
            String urlString = url.build().toString();
            Log.d("Hello", "pictureRetrievalTask: "+urlString);

            HttpURLConnection connection = openConnection(urlString);
            if(connection == null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alert.error("Check if your internet connection good and try again");
                    }
                });

            }

            else if (isConnectionOkay(connection) == false){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alert.error("Problem downloading images");
                    }
                });

            }

            else{
                data = downloadToString(connection);
                if(data !=null) {
                    Log.d("Hello", data);
                }
                else{
                    Log.d("Hello", "Nothing returned");
                }
                connection.disconnect();
            }

            return data;
        }

        private void pictureRetrievalTask(String searchKey) {

            String data = searchRemoteAPI(searchKey);
            if(data != null){
                List<String> imageUrlList = getImageLargeUrl(data);
                if(imageUrlList.size() > 0) {
                    for(String imageURL : imageUrlList) {
                        Bitmap image = getImageFromUrl(imageURL);
                        if (image!=null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    images.add(image);
                                }
                            });
                        }
                        else {
                            System.out.println("Image retrieved is null");
                        }
                    }
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alert.displayMessage("No search results");
                        }
                    });
                }

            }

        }


        private Bitmap getImageFromUrl(String imageUrl){

            Bitmap image = null;

            Uri.Builder url = Uri.parse(imageUrl).buildUpon();
            String urlString = url.build().toString();
            Log.d("Hello", "ImageUrl: "+urlString);

            HttpURLConnection connection = openConnection(urlString);
            if(connection == null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alert.error("Check your internet connection is stable and try again");
                    }
                });

            }
            else if (isConnectionOkay(connection) == false){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alert.error("Problem loading images");
                    }
                });

            }
            else{
                image = downloadToBitmap(connection);
                if(image !=null) {
                    // Log.d("Hello", image.toString());
                }
                else{
                    Log.d("Hello", "Nothing returned");
                }
                connection.disconnect();
            }

            return image;
        }


        private List<String> getImageLargeUrl(String data){
            List<String> imageUrlList = new ArrayList<>();
            try {
                JSONObject jBase = new JSONObject(data);
                JSONArray jHits = jBase.getJSONArray("hits");
                if(jHits.length()>0){
                    int lim = LIMIT;
                    if(jHits.length() <= LIMIT) {
                        lim = jHits.length();
                    }
                    for(int i=0;i<lim;i++) {
                        JSONObject jHitsItem = jHits.getJSONObject(i);
                        String imageUrl = jHitsItem.getString("largeImageURL");
                        imageUrlList.add(imageUrl);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return imageUrlList;
        }

        private Bitmap downloadToBitmap(HttpURLConnection conn){

            Bitmap data = null;
            try {
                InputStream inputStream = conn.getInputStream();
                byte[] byteData = getByteArrayFromInputStream(inputStream);
                Log.d("Hello", String.valueOf(byteData.length));
                data = BitmapFactory.decodeByteArray(byteData,0,byteData.length);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return data;
        }


        private HttpURLConnection openConnection(String urlString)  {

            HttpURLConnection conn = null;
            try {
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return conn;
        }

        private boolean isConnectionOkay(HttpURLConnection conn){
            try {
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        private String downloadToString(HttpURLConnection conn){
            String data = null;
            try {
                InputStream inputStream = conn.getInputStream();
                byte[] byteData = IOUtils.toByteArray(inputStream);
                data = new String(byteData, StandardCharsets.UTF_8);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return data;
        }


        private byte[] getByteArrayFromInputStream(InputStream inputStream) throws IOException {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4096];
            int progress = 0;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
                progress = progress+nRead;
            }

            return buffer.toByteArray();
        }




    }
}