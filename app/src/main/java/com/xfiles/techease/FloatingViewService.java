package com.xfiles.techease;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.attaurrahman.recordapplication.R;

import me.itangqi.waveloadingview.WaveLoadingView;

import static java.lang.System.out;


public class FloatingViewService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    WaveLoadingView mWaveLoadingView;

    Thread runner;
    final Runnable updater = new Runnable() {

        public void run() {
            updateSound();
        }
    };

    final Handler mHandler = new Handler();


    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);



        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        mWaveLoadingView = mFloatingView.findViewById(R.id.waveLoadingView);
        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        mWaveLoadingView.setBottomTitle("Scanning");
        mWaveLoadingView.setCenterTitleColor(Color.WHITE);
        mWaveLoadingView.setBorderColor(Color.WHITE);
        mWaveLoadingView.setBottomTitleSize(8);
        mWaveLoadingView.setBorderWidth(2);


        if (runner == null) {
            runner = new Thread() {
                public void run() {
                    while (runner != null) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                        ;
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
        }





        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window

                Utilities.putValueInEditor(FloatingViewService.this).putBoolean("thread_runner", false).commit();
                Utilities.putValueInEditor(FloatingViewService.this).putBoolean("savePath", true).commit();
                Toast.makeText(FloatingViewService.this, "Recording has been Saved", Toast.LENGTH_SHORT).show();
                Utilities.putValueInEditor(FloatingViewService.this).putBoolean("savePath", false).commit();
                Utilities.putValueInEditor(FloatingViewService.this).putBoolean("playButtonVisibility", false).commit();
                Utilities.putValueInEditor(FloatingViewService.this).putBoolean("runner", false).commit();

                stopService(new Intent(FloatingViewService.this, RecordingService.class));
                stopSelf();



            }
        });







        //Open the application on thi button click
        ImageView openButton = (ImageView) mFloatingView.findViewById(R.id.open_button);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open the application  click.
                Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


                //close the service
                stopSelf();
            }
        });


        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:


                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {


                                Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                stopSelf();
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }


    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    private void updateSound() {


        String string = Utilities.getSharedPreferences(getApplicationContext()).getString("wave", "");

        try {
            mWaveLoadingView.setProgressValue(Integer.parseInt(string));
            mWaveLoadingView.setAmplitudeRatio(Integer.parseInt(string));

        } catch (NumberFormatException nfe) {
            out.println("Could not parse " + nfe);
        }
    }
}
