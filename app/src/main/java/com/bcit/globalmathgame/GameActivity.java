package com.bcit.globalmathgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    public static final int MAX_QUESTIONS = 5;

    Random rand;
    Button[] answerButtons;

    GameQuestion question;
    int questionCount;
    int score;



    public static final String CHANNEL_1_ID = "channel1";

    private NotificationManagerCompat notificationManagerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        initNotificationChannels();
        notificationManagerCompat = NotificationManagerCompat.from(this);


        //rand = new Random(100); //ONLY FOR TEST DATA - same numbers each run time.
        rand = new Random(System.currentTimeMillis()); //UNCOMMENT FOR REAL DATA

        answerButtons = new Button[3];
        answerButtons[0] = findViewById(R.id.answer1Btn);
        answerButtons[1] = findViewById(R.id.answer2Btn);
        answerButtons[2] = findViewById(R.id.answer3Btn);


        question = new GameQuestion(rand);
        triggerNextQuestion();
    }

    public void chooseAns(View view){
        String toastTxt = "Error";

        if (questionCount <= MAX_QUESTIONS) {
            Button b = (Button) view;
            int ans = Integer.parseInt(b.getText().toString());

            if (ans == this.question.correctAns) {
                toastTxt = getResources().getString(R.string.correctTxt);;
                score++;
            } else {
                toastTxt = getResources().getString(R.string.incorrectTxt);;
            }

            Toast toast = Toast. makeText(getApplicationContext(), toastTxt, Toast. LENGTH_SHORT);
            toast.show();

        } else {
            endGame(true);
        }


        //if next question isnt above max, trigger question. Else end game
        if (questionCount <= MAX_QUESTIONS - 1) {
            triggerNextQuestion();
        } else {
            endGame(true);
        }

    }


    public void triggerNextQuestion(){
        questionCount++;
        question = new GameQuestion(rand);


        String questionLabelTxt = getResources().getString(R.string.questionLabel);
        String scoreLabelTxt = getResources().getString(R.string.scoreLabelTxt);


        TextView questionLabel = findViewById(R.id.questionLabel);
        questionLabel.setText(questionLabelTxt + " " + questionCount);

        TextView questionText = findViewById(R.id.questionValue);
        questionText.setText(question.question);


        int correctBtn = rand.nextInt(2);
        for (int i = 0; i < 3; i++) {
            if (i == correctBtn) {
                answerButtons[i].setText("" + question.correctAns);
            } else {
                answerButtons[i].setText("" + question.getBadAns(rand));
            }
        }


        TextView scoreTxt = findViewById(R.id.scoreTxt);
        scoreTxt.setText(scoreLabelTxt + " " + score + "/" + MAX_QUESTIONS);

    }


    public void skipQuestion(View v) {
        if (questionCount <= MAX_QUESTIONS - 1) {
            triggerNextQuestion();
        } else {
            endGame(true);
        }
    }

    public void reset(View view) {
        questionCount = 0;
        score = 0;

        endGame(false);
    }


    public void endGame(boolean showScoreNotification){

        if(showScoreNotification){
            makeNotification();
        }

        Intent i = new Intent(GameActivity.this, MainActivity.class);
        startActivity(i);
    }


    private void initNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_1_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("score notification");

            NotificationManager notificationChannel = getSystemService(NotificationManager.class);
            notificationChannel.createNotificationChannel(channel);
        }
    }

    private void makeNotification() {
        String title = getResources().getString(R.string.app_name);
        String scoreNotificationString = getResources().getString(R.string.scoreNotificationStr);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(scoreNotificationString + " " + score + " / " + MAX_QUESTIONS)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManagerCompat.notify(1, notification);
    }

}


