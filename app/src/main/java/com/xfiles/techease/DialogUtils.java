package com.xfiles.techease;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attaurrahman.recordapplication.R;

import java.io.IOException;

import ak.sh.ay.musicwave.MusicWave;
import br.com.joinersa.oooalertdialog.Animation;
import br.com.joinersa.oooalertdialog.OnClickListener;
import br.com.joinersa.oooalertdialog.OoOAlertDialog;

public class DialogUtils {

    public static Visualizer mVisualizer;
    public static MusicWave musicWave;


    public static MediaPlayer mediaPlayer;
    public static void MediaPlayerDialog(LayoutInflater inflater, final Context context, String string) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final View dialogView = inflater.inflate(R.layout.mediaplayer_layout, null);
        dialogView.setBackgroundResource(android.R.color.transparent);
        dialogBuilder.setView(dialogView);
        final AlertDialog dialog = dialogBuilder.create();

        musicWave = dialogView.findViewById(R.id.musicWave);

        mediaPlayer = new MediaPlayer();

        String strPath = "/storage/emulated/0/XFiles/"+string;

        try {
            mediaPlayer.setDataSource(strPath);
            prepareVisualizer();
            mVisualizer.setEnabled(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


        TextView tvTitile = dialogView.findViewById(R.id.tv_title);
        Button btnStop = dialogView.findViewById(R.id.btn_stiop);

        tvTitile.setText(string);

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.stop();
                mVisualizer.setEnabled(false);
                dialog.dismiss();
            }
        });




        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (mediaPlayer!=null){
                    mediaPlayer.stop();
                    mVisualizer.setEnabled(false);
                }
                return false;
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                dialog.dismiss();
                mVisualizer.setEnabled(false);

            }
        });

    }

    private static void prepareVisualizer() {

        mVisualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        musicWave.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
        mVisualizer.setEnabled(true);
    }


}
