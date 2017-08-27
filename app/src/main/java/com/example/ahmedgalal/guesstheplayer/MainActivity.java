package com.example.ahmedgalal.guesstheplayer;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> playersURL = new ArrayList<String>();
    ArrayList<String> playerName = new ArrayList<String>();

    int chosenPlayer = 0;
    int locationOfCorrectAnswer = 0;
    String[] answers = new String[4];

    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    ProgressBar spinner;

    TextView loading;
    GridLayout  dataLayout;
    RelativeLayout loadingLayout ;

    public void playerChosen (View view)
    {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer)))
        {
            Toast.makeText(getApplicationContext(),"Correct!", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Wrong :( It was.. " + playerName.get(chosenPlayer), Toast.LENGTH_LONG).show();
        }

        newQuestion();
    }

    public class imageDownloader extends AsyncTask<String, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    public class downloadTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected void onPreExecute() {

            loadingLayout.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try
            {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            loadingLayout.setVisibility(View.INVISIBLE);
            dataLayout.setVisibility(View.VISIBLE);

        }
    }

    public void newQuestion ()
    {
        Random random = new Random();
        chosenPlayer = random.nextInt(playersURL.size());

        imageDownloader imageTask = new imageDownloader();
        Bitmap playerImage;
        try
        {
            playerImage = imageTask.execute(playersURL.get(chosenPlayer)).get();

            imageView.setImageBitmap(playerImage);

            locationOfCorrectAnswer = random.nextInt(4);

            int incorrectAnswerLocation;

            for (int i = 0; i < 4; i++)
            {
                if (i == locationOfCorrectAnswer)
                {
                    answers[i] = playerName.get(chosenPlayer);
                }
                else
                {
                    incorrectAnswerLocation = random.nextInt(playersURL.size());

                    while (incorrectAnswerLocation == chosenPlayer)
                    {
                        incorrectAnswerLocation = random.nextInt(playersURL.size());
                    }
                    answers[i] = playerName.get(incorrectAnswerLocation);
                }
            }

            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (ProgressBar) findViewById(R.id.progress);

        dataLayout = (GridLayout) findViewById(R.id.dataLayout);
        loadingLayout = (RelativeLayout) findViewById(R.id.loadingPanel);


        imageView = (ImageView) findViewById(R.id.imageView);

        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);


            downloadTask task = new downloadTask();
            String result = null;

            try {
                result = task.execute("http://www.manutd.com/en/Players-And-Staff/First-Team.aspx").get();

                //String [] splitResult = result.split()

                Pattern p = Pattern.compile("<img height=\"79\" width=\"116\" src=\"(.*?)\"");
                Matcher m = p.matcher(result);

                while (m.find()) {
                    //System.out.println(m.group(1));
                    playersURL.add(m.group(1));
                }

                p = Pattern.compile("alt = \"Image of (.*?)\"");
                m = p.matcher(result);

                while (m.find()) {
                    //System.out.println(m.group(1));
                    playerName.add(m.group(1));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            newQuestion();

    }
}
