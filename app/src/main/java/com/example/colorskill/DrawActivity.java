package com.example.colorskill;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import java.util.ArrayList;
import java.util.List;

public class DrawActivity extends AppCompatActivity {

    View view1, view2, view3, view4, view5, view6, view7, view8, view9, view10, view11, view12, view13, view14, view15, view16, view17, view18, view19, view20, view21, view22, view23, view24, view25;
    private final List<View> viewList = new ArrayList<>();

    ImageButton redButton, greenButton, blueButton, whiteButton, blackButton;
    private final List<Integer> colorList = new ArrayList<>();
    private final List<ImageButton> colorButtonList = new ArrayList<>();
    private static int brushColor;
    ImageView brushIconIv;

    ProgressBar timeLeftBar;
    private CountDownTimer timer;
    public static final long MAX_TIME_MS = 30 * 1000;       // 20 sec
    private long timeLeftMs = MAX_TIME_MS;
    private boolean timerRunning = false;

    private Dialog dialog;
    ImageButton pauseButton;

    ImageView previewPicIv;
    int[] previewPicMatrix = {4, 3, 0, 3, 4, 5, 5, 0, 7, 7, 5, 1, 3, 2, 7, 3, 1, 6, 2, 3, 4, 6, 6, 6, 4};
    int[] drawingMatrix = new int[25];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(0xFF5C5A5A);
        getSupportActionBar().hide();

        previewPicIv = findViewById(R.id.previewPicIv);
        brushIconIv = findViewById(R.id.brushIconIv);
        viewListInit();
        colorListInit();
        colorButtonListInit();

// Setting Click Listener on Drawing Views
        for (int i=0; i<viewList.size(); i++) {
            int finalI = i;
//            viewList.get(i).setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    setViewColor(finalI);
//                    checkDrawing();
//                    return true;
//                }
//            });
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
                    previewPicIv.setVisibility(View.INVISIBLE);
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

    private int getColorComb(int viewColor, int brushColor) {
        if (viewColor == Color.RED) {
            if (brushColor == Color.GREEN) {
                return Color.YELLOW;
            } else if (brushColor == Color.BLUE) {
                return Color.MAGENTA;
            }
        } else if (viewColor == Color.GREEN) {
            if (brushColor == Color.RED) {
                return Color.YELLOW;
            } else if (brushColor == Color.BLUE) {
                return Color.CYAN;
            }
        } else if (viewColor == Color.BLUE) {
            if (brushColor == Color.RED) {
                return Color.MAGENTA;
            } else if (brushColor == Color.GREEN) {
                return Color.CYAN;
            }
        }
        return 0;
    }

    private boolean isSecondary(int color) {
        return (color == Color.YELLOW || color == Color.CYAN || color == Color.MAGENTA);
    }

    private boolean isBinary(int color) {
        return (color == Color.WHITE || color == Color.BLACK);
    }

    private boolean isPrimary(int color) {
        return (color == Color.RED || color == Color.BLUE || color == Color.GREEN);
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
                dialog.cancel();
                resetTimer();
                resetViews();
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
                dialog.cancel();
                resetTimer();
                resetViews();
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

        Button homeButton, replayButton, nextButton;

        homeButton = dialog.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });

        replayButton = dialog.findViewById(R.id.replayButton);
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetViews();
                resetTimer();
                dialog.cancel();
            }
        });

        nextButton = dialog.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });
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

    private void startTimer() {
        timerRunning = true;
        previewPicIv.setVisibility(View.VISIBLE);
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

    private void setBrushIconColor(int finalI) {
        Drawable buttonDrawable = brushIconIv.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, colorList.get(finalI));
        brushIconIv.setBackground(buttonDrawable);
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
        colorList.add(Color.RED);       // 0
        colorList.add(Color.GREEN);     // 1
        colorList.add(Color.BLUE);      // 2
        colorList.add(Color.WHITE);     // 3
        colorList.add(Color.BLACK);     // 4
        colorList.add(Color.YELLOW);    // 5
        colorList.add(Color.CYAN);      // 6
        colorList.add(Color.MAGENTA);   // 7
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