package com.example.braintrainer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ArrayList numbers = new ArrayList();
    TextView answerTextView, timerTextView, questionTextView, scoreTextView;
    Button op1, op2, op3, op4, playAgainButton;
    int correctOption, numberOfQuestions=0, correctAnswers=0;
    GridLayout gridLayout;

    public void reset(View view) {
        playAgainButton.setVisibility(View.INVISIBLE);
        timerTextView.setText("30s");
        scoreTextView.setText("0/0");
        answerTextView.setText("");
        numberOfQuestions=0;
        correctAnswers=0;
        startTimer();
        newQuestion();
    }

    public void startTimer() {
        new CountDownTimer(30000 + 100, 1000) {
            public void onTick(long millisecondsLeft) {
                timerTextView.setText((millisecondsLeft/1000)+"s");
            }
            public void onFinish() {
                answerTextView.setText("GAME OVER!");
                playAgainButton.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    public void startGame(View view) {
        view.setVisibility(View.INVISIBLE);
        questionTextView.setVisibility(View.VISIBLE);
        gridLayout.setVisibility(View.VISIBLE);
        timerTextView.setVisibility(View.VISIBLE);
        scoreTextView.setVisibility(View.VISIBLE);
        startTimer();
    }

    public void newQuestion() {
        if (!numbers.isEmpty())
            numbers.clear();

        Random random = new Random();
        int a = random.nextInt(21);
        int b = random.nextInt(21);

        ++numberOfQuestions;

        correctOption = random.nextInt(4);

        questionTextView.setText(a + " + " + b + " = ? ");

        for (int i=0; i<4; i++) {
            if(i == correctOption)
                numbers.add(a + b);
            else {
                int randomNumber = random.nextInt(41);
                while (randomNumber == (a+b)) {
                    randomNumber = random.nextInt(41);
                }
                numbers.add(randomNumber);
            }
        }

        op1.setText(Integer.toString((Integer) numbers.get(0)));
        op2.setText(Integer.toString((Integer) numbers.get(1)));
        op3.setText(Integer.toString((Integer) numbers.get(2)));
        op4.setText(Integer.toString((Integer) numbers.get(3)));
    }

    public void chooseAnswer(View view) {
        Button btn = (Button) view;
        int i = Integer.parseInt(btn.getTag().toString());
        answerTextView.setVisibility(View.VISIBLE);

        //if (i == correctOption) {
        if (btn.getTag().toString().equals(Integer.toString(correctOption))) {
            ++correctAnswers;
            answerTextView.setText("CORRECT!");
        } else {
            answerTextView.setText("WRONG :(");
            //Log.i("Correct Option: ", btn.getTag().toString()+" "+correctOption);
        }
        scoreTextView.setText(correctAnswers + "/" + numberOfQuestions);
        newQuestion();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionTextView = (TextView) findViewById(R.id.questionTextView);
        answerTextView = (TextView) findViewById(R.id.answerTextView);
        gridLayout = findViewById(R.id.gridLayout);
        timerTextView = findViewById(R.id.timerTextView);
        scoreTextView = findViewById(R.id.scoreTextView);

        op1 = findViewById(R.id.button1);
        op2 = findViewById(R.id.button2);
        op3 = findViewById(R.id.button3);
        op4 = findViewById(R.id.button4);
        playAgainButton = findViewById(R.id.playAgainButton);

        newQuestion();
    }
}