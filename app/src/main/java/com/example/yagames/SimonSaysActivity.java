package com.example.yagames;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimonSaysActivity extends AppCompatActivity {

    private Button btnStartGame, btnRed, btnGreen, btnBlue, btnYellow;
    private TextView tvScore;

    private final List<Integer> sequence = new ArrayList<>();
    private int userStep = 0;
    private int score = 0;
    private boolean userTurn = false;

    private MediaPlayer mediaPlayer;
    private final Random random = new Random();
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_simon_says);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnStartGame = findViewById(R.id.btnStartGame);
        btnRed = findViewById(R.id.btnRed);
        btnGreen = findViewById(R.id.btnGreen);
        btnBlue = findViewById(R.id.btnBlue);
        btnYellow = findViewById(R.id.btnYellow);
        tvScore = findViewById(R.id.tvScore);

        btnStartGame.setOnClickListener(v -> startGame());

        btnRed.setOnClickListener(v -> onColorPressed(0));
        btnGreen.setOnClickListener(v -> onColorPressed(1));
        btnBlue.setOnClickListener(v -> onColorPressed(2));
        btnYellow.setOnClickListener(v -> onColorPressed(3));
    }

    private void startGame() {
        sequence.clear();
        userStep = 0;
        score = 0;
        tvScore.setText("Score: " + score);
        addStep();
    }

    private void addStep() {
        userTurn = false;
        sequence.add(random.nextInt(4)); // 0-Red, 1-Green, 2-Blue, 3-Yellow
        playSequence();
    }

    private void playSequence() {
        int delay = 1000;
        for (int i = 0; i < sequence.size(); i++) {
            int color = sequence.get(i);
            handler.postDelayed(() -> flashButton(color), delay * (i + 1));
        }
        handler.postDelayed(() -> {
            userStep = 0;
            userTurn = true;
        }, delay * (sequence.size() + 1));
    }

    private void flashButton(int color) {
        Button btn = getButtonByColor(color);
        btn.setAlpha(0.5f);
        playSound(color);
        handler.postDelayed(() -> btn.setAlpha(1f), 500);
    }

    private void onColorPressed(int color) {
        if (!userTurn) return;

        playSound(color);

        if (color == sequence.get(userStep)) {
            userStep++;
            if (userStep == sequence.size()) {
                score++;
                tvScore.setText("Score: " + score);
                addStep();
            }
        } else {
            Toast.makeText(this, "Game Over! Final Score: " + score, Toast.LENGTH_LONG).show();
            userTurn = false;
        }
    }

    private Button getButtonByColor(int color) {
        switch (color) {
            case 0: return btnRed;
            case 1: return btnGreen;
            case 2: return btnBlue;
            case 3: return btnYellow;
            default: return null;
        }
    }

    private void playSound(int color) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        int resId = 0;
        switch (color) {
            case 0: resId = R.raw.simon1; break;
            case 1: resId = R.raw.simon2; break;
            case 2: resId = R.raw.simon3; break;
            case 3: resId = R.raw.simon4; break;
        }

        mediaPlayer = MediaPlayer.create(this, resId);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
