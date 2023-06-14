package com.curtin.remotedata;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.SynchronousQueue;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity implements Runnable{
    public static final String API_KEY = "01189998819991197253";
    private Button downloadButton;
    private TextView resultView;
    private Thread downloadThread;



    private boolean downloadDone = false;
    private Thread progressBarThread;
    private ProgressBar downloadBar;
    private SynchronousQueue<Integer> queue = new SynchronousQueue<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadButton = (Button) findViewById(R.id.downloadBtn);
        resultView = (TextView)  findViewById(R.id.resultView);
        downloadBar = (ProgressBar) findViewById(R.id.downloadBar);
        downloadBar.setVisibility(View.INVISIBLE);

        Runnable runProgressBar = () -> {
            while(!downloadDone) {
                try {
                    queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadBar.setVisibility(View.VISIBLE);
                progressBarThread = new Thread(runProgressBar, "Progress bar thread");
                downloadThread = new Thread(MainActivity.this, "Download thread");
                downloadThread.start();
            }


        });
    }




    @Override
    public void run() {
        try {
            String urlStr = Uri.parse("https://169.254.123.204:8000/testwebservice/rest")
                    .buildUpon()
                    .appendQueryParameter("method","thedata.getit")
                    .appendQueryParameter("api_key",API_KEY)
                    .appendQueryParameter("format","json")
                    .toString();
            URL url = new URL(urlStr);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            addCertificate(conn);
            if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                conn.disconnect();
                throw new IllegalArgumentException("HTTP_NOT_OK. Connection closed.");
            }


            InputStream inputStream = conn.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            while(bytesRead > 0) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                bytesRead = inputStream.read(buffer);
            }
            String data = byteArrayOutputStream.toString("UTF-8");
            byteArrayOutputStream.close();
            JSONObject jObj = new JSONObject(data);
            JSONArray jFactions = jObj.getJSONArray("factions");
            String result = "";
            for(int i=0;i<jFactions.length();i++) {
                JSONObject jFactionObj = jFactions.getJSONObject(i);
                String name = jFactionObj.get("name").toString();
                int strength = jFactionObj.getInt("strength");
                String relationship = jFactionObj.getString("relationship").toString();
                Faction f = createFaction(name,strength,relationship);
                result += f.toString() + "\n";
            }
            resultView.setText(result);
            downloadDone = true;
            conn.disconnect();
        } catch (MalformedURLException e) {
            System.out.println("Wow! Malformed url exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Wow! IOException: " + e.getMessage());
        } catch (CertificateException e) {
            System.out.println("Wow! Certificate exception: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Wow! No such algorithm exception: " + e.getMessage());
        } catch (KeyStoreException e) {
            System.out.println("Wow! Key store exception: " + e.getMessage());
        } catch (KeyManagementException e) {
            System.out.println("Wow! Key management exception: " + e.getMessage());
        } catch (JSONException e) {
            System.out.println("JSON Exception, failed to parse downloaded data to json object");
        }

    }

    private Faction createFaction(String name, int strength, String relationship) {
        return new Faction(name,strength,relationship);
    }

    private void addCertificate(HttpsURLConnection conn) throws IOException, CertificateException,
                                KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        Certificate cert;
        try(InputStream is = getApplicationContext().getResources().openRawResource(R.raw.cert))
        {
            cert = CertificateFactory.getInstance("X.509").generateCertificate(is);
        }

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", cert);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        conn.setSSLSocketFactory(sslContext.getSocketFactory());
        conn.setHostnameVerifier(new HostnameVerifier()
        {
            @Override
            public boolean verify(String hostname, SSLSession session)
            {
                return true;
            }
        });
    }


}