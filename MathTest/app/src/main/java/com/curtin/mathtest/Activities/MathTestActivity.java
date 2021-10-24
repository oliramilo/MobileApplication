package com.curtin.mathtest.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.curtin.mathtest.Database.TestHandler;
import com.curtin.mathtest.MainActivity;
import com.curtin.mathtest.Model.Answer;
import com.curtin.mathtest.Model.AppAlerter;
import com.curtin.mathtest.Model.Question;
import com.curtin.mathtest.Model.TestSubmission;
import com.curtin.mathtest.R;
import com.curtin.mathtest.Server.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class MathTestActivity extends AppCompatActivity implements View.OnClickListener {

    private long timeStart;
    private long timeEnd;
    private String dateStr;
    private Question question = null;
    private AppAlerter alert;
    private TestDownloader downloader;

    private ArrayList<Answer> submitted = new ArrayList<>();

    private Button nextQuestion;
    private Button endTestButton;
    private Button getNextAnswer;
    private Button getPrevAnswer;


    private Button option1;
    private Button option2;
    private Button option3;
    private Button option4;


    private TextView timerTextView;
    private TextView questionTextView;


    private EditText customAnswer;

    private TextView scoreText;


    private int grade = 0;
    private int startPtr = 0;
    private int endPtr = 4;

    private TestTimer timer;

    private SessionManager sessionManager;

    private TestHandler testHandler;
    public void submitAnswer(Question question,String answer, boolean skipped) {
        Answer ans = new Answer(question, answer, skipped);
        if(ans.isSkipped()) {

        }
        else {
            if(ans.isCorrect()) {
                grade+=10;
            }
            else {
                grade-=5;
            }
            scoreText.setText("Score: " + grade);

        }
        submitted.add(ans);
    }

    public void setChoices(Question q) {
        int[] size = q.getPossibleAnswers();
        if(size.length == 0) {
            option1.setVisibility(View.INVISIBLE);
            option2.setVisibility(View.INVISIBLE);
            option3.setVisibility(View.INVISIBLE);
            option4.setVisibility(View.INVISIBLE);

            customAnswer.setVisibility(View.VISIBLE);
            customAnswer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i== EditorInfo.IME_ACTION_DONE) {
                        String answer = customAnswer.getText().toString();
                        submitAnswer(question,answer,false);
                        setNewQuestion();
                        return true;
                    }
                    return false;
                }
            });
        }
        else if(size.length > 4) {
            customAnswer.setVisibility(View.INVISIBLE);
            getPrevAnswer.setVisibility(View.VISIBLE);
            getNextAnswer.setVisibility(View.VISIBLE);
            option1.setText("");
            option2.setText("");
            option3.setText("");
            option4.setText("");
            option1.setText(""+size[startPtr]);
            startPtr+=1;
            option2.setText(""+size[startPtr]);
            startPtr+=1;
            option3.setText(""+size[startPtr]);
            startPtr+=1;
            option4.setText(""+size[startPtr]);
        }

        if(size.length <= 4) {
            getPrevAnswer.setVisibility(View.INVISIBLE);
            getNextAnswer.setVisibility(View.INVISIBLE);
        }

    }

    public void setQuestion(Question question) {
        questionTextView.setText(question.getQuestion());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_test);
        testHandler = new TestHandler(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());

        timeStart = System.currentTimeMillis();

        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        dateStr = formatter.format(calendar.getTime());

        downloader = new TestDownloader();

        nextQuestion = (Button) findViewById(R.id.nextQuestionButton);
        endTestButton = (Button) findViewById(R.id.endTestButton);
        getNextAnswer = (Button) findViewById(R.id.moreAnswers);
        getPrevAnswer = (Button) findViewById(R.id.previousAnswers);

        option1 = (Button) findViewById(R.id.firstAnswer);
        option2 = (Button) findViewById(R.id.secondAnswer);
        option3 = (Button) findViewById(R.id.thirdAnswer);
        option4 = (Button) findViewById(R.id.fourthAnswer);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);
        timerTextView = (TextView) findViewById(R.id.timerText);
        questionTextView = (TextView) findViewById(R.id.questionText);
        scoreText = (TextView) findViewById(R.id.totalScoreView);

        customAnswer = (EditText) findViewById(R.id.customAnswerBox);


        alert = new AppAlerter(this);

        try {
            setNewQuestion();
        }
        catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            alert.error(msg);
        }

        getPrevAnswer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int[] possibleAnswers = question.getPossibleAnswers();
                int answersRemaining = startPtr - 4;

                if(answersRemaining > 4) {
                    option1.setText(""+possibleAnswers[startPtr]);
                    startPtr-=1;
                    option2.setText(""+possibleAnswers[startPtr]);
                    startPtr-=1;
                    option3.setText(""+possibleAnswers[startPtr]);
                    startPtr-=1;
                    option4.setText(""+possibleAnswers[startPtr]);
                }
                else {
                    option1.setText("Option 1");
                    option2.setText("Option 2");
                    option3.setText("Option 3");
                    option4.setText("Option 4");
                    switch(answersRemaining) {
                        case 1:
                            option1.setText(possibleAnswers[startPtr]);
                            startPtr-=1;
                            option2.setVisibility(View.INVISIBLE);
                            option3.setVisibility(View.INVISIBLE);
                            option4.setVisibility(View.INVISIBLE);
                            break;
                        case 2:
                            option1.setText(""+possibleAnswers[startPtr]);
                            startPtr-=1;
                            option2.setText(""+possibleAnswers[startPtr]);
                            startPtr-=1;
                            option3.setVisibility(View.INVISIBLE);
                            option4.setVisibility(View.INVISIBLE);
                            break;
                        case 3:
                            option1.setText(""+possibleAnswers[startPtr]);
                            startPtr-=1;
                            option2.setText(""+possibleAnswers[startPtr]);
                            startPtr-=1;
                            option3.setText(""+possibleAnswers[startPtr]);
                            startPtr-=1;
                            option4.setVisibility(View.INVISIBLE);
                            break;
                        case 4:
                            option1.setText(""+possibleAnswers[startPtr]);
                            startPtr-=1;
                            option2.setText(""+possibleAnswers[startPtr]);
                            startPtr-=1;
                            option3.setText(""+possibleAnswers[startPtr]);
                            startPtr-=1;
                            option4.setText(""+possibleAnswers[startPtr]);
                            startPtr-=1;
                            break;
                    }
                }
            }
        });

        getNextAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] possibleAnswers = question.getPossibleAnswers();
                int size = possibleAnswers.length;
                int answersRemaining = size - startPtr;
                if(answersRemaining == 0) {

                }
                else if(answersRemaining > 4) {
                    option1.setText(""+possibleAnswers[startPtr]);
                    startPtr+=1;
                    option2.setText(""+possibleAnswers[startPtr]);
                    startPtr+=1;
                    option3.setText(""+possibleAnswers[startPtr]);
                    startPtr+=1;
                    option4.setText(""+possibleAnswers[startPtr]);
                    startPtr+=1;
                }
                else {
                    option1.setText("");
                    option2.setText("");
                    option3.setText("");
                    option4.setText("");
                    switch(answersRemaining) {
                        case 1:
                            option1.setText(""+possibleAnswers[startPtr]);
                            startPtr+=1;
                            option2.setVisibility(View.INVISIBLE);
                            option3.setVisibility(View.INVISIBLE);
                            option4.setVisibility(View.INVISIBLE);
                            break;
                        case 2:
                            option1.setText(""+possibleAnswers[startPtr]);
                            startPtr+=1;
                            option2.setText(""+possibleAnswers[startPtr]);
                            startPtr+=1;
                            option3.setVisibility(View.INVISIBLE);
                            option4.setVisibility(View.INVISIBLE);
                            break;
                        case 3:
                            option1.setText(""+possibleAnswers[startPtr]);
                            startPtr+=1;
                            option2.setText(""+possibleAnswers[startPtr]);
                            startPtr+=1;
                            option3.setText(""+possibleAnswers[startPtr]);
                            startPtr+=1;
                            option4.setVisibility(View.INVISIBLE);
                            break;
                        case 4:
                            option1.setText(""+possibleAnswers[startPtr]);
                            startPtr+=1;
                            option2.setText(""+possibleAnswers[startPtr]);
                            startPtr+=1;
                            option3.setText(""+possibleAnswers[startPtr]);
                            startPtr+=1;
                            option4.setText(""+possibleAnswers[startPtr]);
                            startPtr+=1;
                            break;
                    }
                }
            }
        });




        endTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.displayMessage("Test ended, " + submitted.size() + "questions attempted ");
                timeEnd = System.currentTimeMillis();
                int timeSpent = (int)((timeEnd - timeStart) / 1000);
                String[] tok = dateStr.split(" ");
                String date = tok[0];
                String time = tok[1];
                String duration = timeSpent+"";
                TestSubmission submission = new TestSubmission(sessionManager.getUser(), date,time,duration,grade);
                testHandler.addTestSubmission(submission);
                Intent returnToMain = new Intent(MathTestActivity.this, MainActivity.class);
                startActivity(returnToMain);
            }
        });

        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPtr = 0;
                submitAnswer(question,"",true);
                setNewQuestion();
            }
        });
    }

    private void setNewQuestion() {
        option1.setVisibility(View.VISIBLE);
        option2.setVisibility(View.VISIBLE);
        option3.setVisibility(View.VISIBLE);
        option4.setVisibility(View.VISIBLE);
        downloader.downloadQuestion();

        boolean finished = downloader.waitForThreadToFinish();
        if(finished) {
            startPtr = 0;
            if(timer != null) {
                timer.stop();
                timer = null;
            }
            setQuestion(question);
            setChoices(question);
            timer = new TestTimer(question.getTimeToAnswer());
            timer.start();
        }
    }







    @Override
    public void onClick(View view) {
        String answer =((Button)view).getText().toString();
        submitAnswer(question,answer,false);
        setNewQuestion();
    }




    private class TestTimer {
        private int time;
        private CountDownTimer countDownTimer = null;
        public TestTimer(int time) {
            this.time = time;
        }

        public void start() {
            countDownTimer = new CountDownTimer(time*1000,1000) {
                @Override
                public void onTick(long l) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timerTextView.setText("Remaining time: " + l/1000);
                        }
                    });
                }

                @Override
                public void onFinish() {
                    nextQuestion.performClick();
                    countDownTimer.cancel();
                }
            }.start();
        }

        public void stop() {
            countDownTimer.cancel();
        }
    }

    private class TestDownloader {
        private Thread downloadThread;
        public TestDownloader() {
        }

        public boolean waitForThreadToFinish() {
            try {
                downloadThread.join();
                if(downloadThread != null) {
                    downloadThread.interrupt();
                    downloadThread = null;
                }
            } catch (InterruptedException e) {
                downloadThread = null;
                return false;
            }
            return true;
        }

        public boolean download() {
            downloadQuestion();
            return waitForThreadToFinish();
        }

        public void downloadQuestion() throws IllegalArgumentException {
            Runnable downloadQuestionTask = () -> {
                String urlStr = Uri.parse("https://169.254.123.204:8000/random/question/").buildUpon()
                        .appendQueryParameter("method","thedata.getit")
                        .appendQueryParameter("api_key","01189998819991197253")
                        .appendQueryParameter("format","json")
                        .toString();
                URL url = null;
                try {
                    url = new URL(urlStr);
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException("Server error, URL malformed. " + e.getMessage());
                }
                HttpsURLConnection conn = null;
                try {
                    conn = (HttpsURLConnection) url.openConnection();
                } catch (IOException e) {
                    throw new IllegalArgumentException("Server Error, Could not open connection.");
                }
                try {
                    addCertificate(conn);
                } catch (IOException | CertificateException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
                    throw new IllegalArgumentException("Server Error, Could not verify HTTPS Certificate.");
                }

                try {
                    if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        conn.disconnect();
                        throw new IllegalArgumentException("Server not responding, connection closed.");
                    }
                } catch (IOException e) {

                }
                try {
                    InputStream inputStream = conn.getInputStream();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    int downloadLength = conn.getContentLength();

                    while((bytesRead = inputStream.read(buffer, 0 ,buffer.length)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                    String data = byteArrayOutputStream.toString("UTF-8");
                    byteArrayOutputStream.close();
                    JSONObject jObj = new JSONObject(data);
                    String questionStr = jObj.getString("question");
                    int answer = jObj.getInt("result");
                    int timeToSolve = jObj.getInt("timetosolve");

                    JSONArray choices = jObj.getJSONArray("options");
                    int[] availableChoices = new int[choices.length()];

                    for(int i=0;i<choices.length();i++) {
                        availableChoices[i] = choices.getInt(i);
                    }
                    question = new Question(questionStr,answer,availableChoices,timeToSolve);
                    conn.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            };

            downloadThread = new Thread(downloadQuestionTask, "Question Download Thread");
            downloadThread.start();
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


}