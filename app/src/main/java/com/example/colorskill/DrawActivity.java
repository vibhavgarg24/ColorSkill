package com.example.colorskill;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DrawActivity extends AppCompatActivity {

    View view1, view2, view3, view4, view5, view6, view7, view8, view9, view10, view11, view12, view13, view14, view15, view16, view17, view18, view19, view20, view21, view22, view23, view24, view25;
    private final List<View> viewList = new ArrayList<>();

    ImageButton redButton, greenButton, blueButton, whiteButton, blackButton;
    private final List<Integer> colorList = new ArrayList<>();
    private final List<ImageButton> colorButtonList = new ArrayList<>();
    private static int brushColor;
//    private ImageView brushIconIv;
    private View brushIconIv;

    private ProgressBar timeLeftBar;
    private CountDownTimer timer;
    public static final long MAX_TIME_MS = 30 * 1000;       // 20 sec
    private long timeLeftMs = MAX_TIME_MS;
    private boolean timerRunning = false;

    private Dialog dialog;
    private ImageButton pauseButton;

//    private ImageView previewPicIv;
    private RecyclerView previewRv;
    private PreviewAdapter previewAdapter;
    int[] previewPicMatrix;
    int[] drawingMatrix = new int[25];

    private TextView levelTv;
    private TextView scoreTv;
    private int level = 1;
    private long score = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        Window window = DrawActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(DrawActivity.this, R.color.my_grey));

        levelTv = findViewById(R.id.levelTv);
        scoreTv = findViewById(R.id.scoreTv);

        brushIconIv = findViewById(R.id.brushIconIv);
        viewListInit();
        colorListInit();
        colorButtonListInit();

        previewRv = findViewById(R.id.previewRv);
        previewRv.setHasFixedSize(true);
        previewRv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        nextLevel();
        setLevelAndScore(level, score);

// Setting Click Listener on Drawing Views
        for (int i=0; i<viewList.size(); i++) {
            int finalI = i;
            viewList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setViewColor(finalI);
                    checkDrawing();
                }
            });
        }

// Setting Click Listener on Color Buttons
        for (int i=0; i<colorButtonList.size(); i++) {
            int finalI = i;
            colorButtonList.get(i).setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    brushColor = colorList.get(finalI);
                    setBrushIconColor(finalI);
                    setButtonSizes(finalI);
                }
            });
        }

        timeLeftBar = findViewById(R.id.timeLeftBar);
        timeLeftBar.setMax((int) (MAX_TIME_MS/1000));
        timeLeftBar.setProgress((int) (MAX_TIME_MS/1000));

        pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning) {
                    pauseTimer();
                    previewRv.setVisibility(View.INVISIBLE);
                    pauseButton.setVisibility(View.INVISIBLE);
                    pauseDialog();
                }
            }
        });

        startTimer();
        whiteButton.callOnClick();
    }

    private void setViewColor(int viewIndex) {
        ColorDrawable viewColorDrawable = (ColorDrawable) viewList.get(viewIndex).getBackground();
        int viewColor = viewColorDrawable.getColor();

        int finalColor = 0;
        if (isBinary(brushColor) || viewColor == brushColor || !isPrimary(viewColor)) {
            viewList.get(viewIndex).setBackgroundColor(brushColor);
        } else {
            finalColor = getColorComb(viewColor, brushColor);
            viewList.get(viewIndex).setBackgroundColor(finalColor);
        }
    }

    private void setBrushIconColor(int finalI) {
        Drawable buttonDrawable = brushIconIv.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, colorList.get(finalI));
        brushIconIv.setBackground(buttonDrawable);
    }

    private int getColorComb(int viewColor, int brushColor) {
        if (viewColor == getResources().getColor(R.color.my_red)) {
            if (brushColor == getResources().getColor(R.color.my_green)) {
                return getResources().getColor(R.color.my_yellow);
            } else if (brushColor == getResources().getColor(R.color.my_blue)) {
                return getResources().getColor(R.color.my_magenta);
            }
        } else if (viewColor == getResources().getColor(R.color.my_green)) {
            if (brushColor == getResources().getColor(R.color.my_red)) {
                return getResources().getColor(R.color.my_yellow);
            } else if (brushColor == getResources().getColor(R.color.my_blue)) {
                return getResources().getColor(R.color.my_cyan);
            }
        } else if (viewColor == getResources().getColor(R.color.my_blue)) {
            if (brushColor == getResources().getColor(R.color.my_red)) {
                return getResources().getColor(R.color.my_magenta);
            } else if (brushColor == getResources().getColor(R.color.my_green)) {
                return getResources().getColor(R.color.my_cyan);
            }
        }
        return 0;
    }

    private boolean isBinary(int color) {
        return (color == getResources().getColor(R.color.my_white)
                || color == getResources().getColor(R.color.my_black));
    }

    private boolean isPrimary(int color) {
        return (color == getResources().getColor(R.color.my_red)
                || color == getResources().getColor(R.color.my_green)
                || color == getResources().getColor(R.color.my_blue));
    }

    private void pauseDialog() {
        getDialogInstance();
        dialog.setContentView(R.layout.dialog_pause);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        Button homeButton, restartButton, resumeButton;

        homeButton = dialog.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });

        restartButton = dialog.findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
                resetViews();
                level = 1;
                score = 0;
                setLevelAndScore(level, score);
                nextLevel();
                dialog.cancel();
            }
        });

        resumeButton = dialog.findViewById(R.id.resumeButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                startTimer();
            }
        });
    }

    private void timeOverDialog() {
        getDialogInstance();
        dialog.setContentView(R.layout.dialog_timeover);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        Button homeButton, restartButton;

        homeButton = dialog.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });

        restartButton = dialog.findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
                resetViews();
                level = 1;
                score = 0;
                setLevelAndScore(level, score);
                nextLevel();
                dialog.cancel();
            }
        });
    }

    private void wonDialog() {
        pauseTimer();
        getDialogInstance();
        dialog.setContentView(R.layout.dialog_won);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

//        Button homeButton, replayButton, nextButton;
        Button homeButton, nextButton;

        homeButton = dialog.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });

//        replayButton = dialog.findViewById(R.id.replayButton);
//        replayButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetViews();
//                resetTimer();
//                dialog.cancel();
//            }
//        });

        nextButton = dialog.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score += timeLeftMs/1000 + 70;
                setLevelAndScore(++level, score);
                nextLevel();
                resetViews();
                resetTimer();
                dialog.cancel();
            }
        });
    }

    private void nextLevel() {
        generatePreviewMatrix();
        previewAdapter = new PreviewAdapter(getApplicationContext(), colorList, previewPicMatrix);
        previewRv.setAdapter(previewAdapter);
        previewAdapter.notifyDataSetChanged();
    }

    private void setLevelAndScore(int level, long score) {
        levelTv.setText(String.valueOf(level));
        scoreTv.setText(String.valueOf(score));
    }

    private void getDialogInstance() {
        if (dialog != null) {
            dialog.cancel();
        }
        dialog = new Dialog(DrawActivity.this);
    }

    private void checkDrawing() {
        generateDrawingMatrix();
        boolean matchMatrix = matchMatrix();
        if (matchMatrix) {
            wonDialog();
        }
    }

    private boolean matchMatrix() {
        for (int i=0; i<viewList.size(); i++) {
            if (drawingMatrix[i] != previewPicMatrix[i] ) {
                return false;
            }
        }
        return true;
    }

    private void generateDrawingMatrix() {
        for (int i=0; i<viewList.size(); i++) {
            ColorDrawable viewColorDrawable = (ColorDrawable) viewList.get(i).getBackground();
            int viewColor = viewColorDrawable.getColor();
            int colorId = colorList.indexOf(viewColor);
            drawingMatrix[i] = colorId;
        }
    }

    private void generatePreviewMatrix() {
        Random random = new Random();
        int bound = getBound();

        previewPicMatrix = new int[25];
        for (int i=0; i<25; i++) {
            previewPicMatrix[i] = random.nextInt(bound);
        }
    }

    private int getBound() {
        if (level == 1) {
            return 1;
        }
        if (level <= 3) {
            return 2;
        }
        if (level <= 6) {
            return 3;
        }
        if (level <= 8) {
            return 5;
        }
        if (level <= 11) {
            return 6;
        }
        if (level <= 15) {
            return 7;
        }
        return 8;
    }

    private void startTimer() {
        timerRunning = true;
        previewRv.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(timeLeftMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMs = millisUntilFinished;
                timeLeftBar.setProgress((int) (timeLeftMs/1000));
            }
            @Override
            public void onFinish() {
            //      Snackbar.make(findViewById(R.id.drawActivityContainer), "Time Over!", Snackbar.LENGTH_SHORT).show();
                timeOverDialog();
            }
        }.start();
    }

    private void pauseTimer() {
        timer.cancel();
        timerRunning = false;
    }

    private void resetTimer() {
        timer.cancel();
        timeLeftMs = MAX_TIME_MS;
        timeLeftBar.setProgress((int) (MAX_TIME_MS/1000));
        startTimer();
    }

    private void resetViews() {
        for (int i=0; i<viewList.size(); i++) {
            viewList.get(i).setBackgroundColor(Color.WHITE);
        }
        whiteButton.callOnClick();
    }

    private void setButtonSizes(int finalI) {
        for (int i=0; i<colorButtonList.size(); i++) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) colorButtonList.get(i).getLayoutParams();
            final float scale = getApplication().getResources().getDisplayMetrics().density;
            int pixels;
            if (i == finalI) {
                pixels = (int) (50 * scale + 0.5f);
            } else {
                pixels = (int) (40 * scale + 0.5f);
            }
            layoutParams.height = pixels;
            layoutParams.width = pixels;
            colorButtonList.get(i).setLayoutParams(layoutParams);
        }
    }

    private void viewListInit() {
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        view5 = findViewById(R.id.view5);
        view6 = findViewById(R.id.view6);
        view7 = findViewById(R.id.view7);
        view8 = findViewById(R.id.view8);
        view9 = findViewById(R.id.view9);
        view10 = findViewById(R.id.view10);
        view11 = findViewById(R.id.view11);
        view12 = findViewById(R.id.view12);
        view13 = findViewById(R.id.view13);
        view14 = findViewById(R.id.view14);
        view15 = findViewById(R.id.view15);
        view16 = findViewById(R.id.view16);
        view17 = findViewById(R.id.view17);
        view18 = findViewById(R.id.view18);
        view19 = findViewById(R.id.view19);
        view20 = findViewById(R.id.view20);
        view21 = findViewById(R.id.view21);
        view22 = findViewById(R.id.view22);
        view23 = findViewById(R.id.view23);
        view24 = findViewById(R.id.view24);
        view25 = findViewById(R.id.view25);

        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewList.add(view4);
        viewList.add(view5);
        viewList.add(view6);
        viewList.add(view7);
        viewList.add(view8);
        viewList.add(view9);
        viewList.add(view10);
        viewList.add(view11);
        viewList.add(view12);
        viewList.add(view13);
        viewList.add(view14);
        viewList.add(view15);
        viewList.add(view16);
        viewList.add(view17);
        viewList.add(view18);
        viewList.add(view19);
        viewList.add(view20);
        viewList.add(view21);
        viewList.add(view22);
        viewList.add(view23);
        viewList.add(view24);
        viewList.add(view25);
    }

    private void colorListInit() {

        colorList.add(getResources().getColor(R.color.my_red));         // 0
        colorList.add(getResources().getColor(R.color.my_green));       // 1
        colorList.add(getResources().getColor(R.color.my_blue));        // 2
        colorList.add(getResources().getColor(R.color.my_white));       // 3
        colorList.add(getResources().getColor(R.color.my_black));       // 4
        colorList.add(getResources().getColor(R.color.my_yellow));      // 5
        colorList.add(getResources().getColor(R.color.my_cyan));        // 6
        colorList.add(getResources().getColor(R.color.my_magenta));     // 7

//        colorList.add(Color.RED);       // 0
//        colorList.add(Color.GREEN);     // 1
//        colorList.add(Color.BLUE);      // 2
//        colorList.add(Color.WHITE);     // 3
//        colorList.add(Color.BLACK);     // 4
//        colorList.add(Color.YELLOW);    // 5
//        colorList.add(Color.CYAN);      // 6
//        colorList.add(Color.MAGENTA);   // 7
    }

    private void colorButtonListInit() {
        redButton = findViewById(R.id.redButton);
        greenButton = findViewById(R.id.greenButton);
        blueButton = findViewById(R.id.blueButton);
        whiteButton = findViewById(R.id.whiteButton);
        blackButton = findViewById(R.id.blackButton);

        colorButtonList.add(redButton);
        colorButtonList.add(greenButton);
        colorButtonList.add(blueButton);
        colorButtonList.add(whiteButton);
        colorButtonList.add(blackButton);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dialog == null || !dialog.isShowing()) {
            startTimer();
        }
    }
}