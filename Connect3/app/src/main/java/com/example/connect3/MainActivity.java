package com.example.connect3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    int activePlayer = 0; //0: yellow , 1: red , 2: empty
    int gameState[] = {2,2,2,2,2,2,2,2,2};
    int winningCombinations[][] = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    boolean hasWinner = false;
    int count = 0;


    public void dropIn(View view) {
        ImageView counter = (ImageView) view;
        int selectedCounter = Integer.parseInt(counter.getTag().toString());

        if (gameState[selectedCounter] == 2 && !hasWinner) {
            count++;
            gameState[selectedCounter] = activePlayer;

            counter.setTranslationY(-1500);

            if (activePlayer == 0) {
                counter.setImageResource(R.drawable.yellow);
                activePlayer = 1;
            } else {
                counter.setImageResource(R.drawable.red);
                activePlayer = 0;
            }
            counter.animate().translationYBy(1500).setDuration(500);

            if(count == 9) {
                Button playAgainButton = (Button) findViewById(R.id.playAgainButton);
                TextView winnerTextView = (TextView) findViewById(R.id.winnerTextView);

                winnerTextView.setText("IT'S A TIE");
                playAgainButton.setVisibility(View.VISIBLE);
                winnerTextView.setVisibility(View.VISIBLE);
            }


            for (int winningCombination[] : winningCombinations) {
                if (gameState[winningCombination[0]] == gameState[winningCombination[1]] && gameState[winningCombination[1]] == gameState[winningCombination[2]] && gameState[winningCombination[0]] != 2) {
                    //Winner decided
                    String msg;
                    hasWinner = true;
                    if (activePlayer == 0)
                        msg = "Red is the winner!";
                    else
                        msg = "Yellow is the winner!";
//                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

                    //REPLAY OPTION and WINNER DISPLAY (both initially invisible)
                    Button playAgainButton = (Button) findViewById(R.id.playAgainButton);
                    TextView winnerTextView = (TextView) findViewById(R.id.winnerTextView);

                    winnerTextView.setText(msg);
                    playAgainButton.setVisibility(View.VISIBLE);
                    winnerTextView.setVisibility(View.VISIBLE);

//                    break;
                }
            }
        } else {
            Toast.makeText(this, "Invalid move!", Toast.LENGTH_SHORT).show();
        }
    }

    public void playAgain(View view) {
        Button playAgainButton = (Button) findViewById(R.id.playAgainButton);
        TextView winnerTextView = (TextView) findViewById(R.id.winnerTextView);
        ImageView tableImageView = (ImageView) findViewById(R.id.tableImageView);

        playAgainButton.setVisibility(View.INVISIBLE);
        winnerTextView.setVisibility(View.INVISIBLE);

        androidx.gridlayout.widget.GridLayout gridLayout = findViewById(R.id.gridLayout);

        for (int i=0; i<gridLayout.getChildCount(); i++) {
            ImageView counter = (ImageView) gridLayout.getChildAt(i);
            counter.setImageDrawable(null);
        }
        tableImageView.setImageResource(R.drawable.board);
//
        for(int i=0; i<9; i++) {
            gameState[i] = 2;
        }
        activePlayer = 0; //0: yellow , 1: red , 2: empty
        hasWinner = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}