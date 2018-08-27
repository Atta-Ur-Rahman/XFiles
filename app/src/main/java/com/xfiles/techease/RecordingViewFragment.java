package com.xfiles.techease;


import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class RecordingViewFragment extends Fragment {

    View parentView;

    RecyclerView rvRecordingFile;
    List<FileHelper> list = new ArrayList<>();
    MyAdapter adpter;
    boolean aBooleanRecordingNotFound = true;

    private static final DecimalFormat format = new DecimalFormat("#.##");
    private static final long MiB = 1024 * 1024;
    private static final long KiB = 1024;

    TextView tvRecordingFileNotFound;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        parentView = inflater.inflate(R.layout.fragment_recording_view, container, false);

        rvRecordingFile = parentView.findViewById(R.id.rv_recording_file);
        tvRecordingFileNotFound = parentView.findViewById(R.id.tv_recording_file_not_found);

        rvRecordingFile.setHasFixedSize(true);
        rvRecordingFile.setLayoutManager(new LinearLayoutManager(getActivity()));


        String path = Environment.getExternalStorageDirectory().toString() + "/XFiles";
        File directory = new File(path);
        File[] files = directory.listFiles();

        String strGetLengthDirectory = String.valueOf(files);



        if (strGetLengthDirectory.equals("null")) {
            tvRecordingFileNotFound.setVisibility(View.VISIBLE);
        }else {

            for (int i = 0; i < files.length; i++) {

                aBooleanRecordingNotFound = false;

                String string = files[i].getName();

                ////set here file path
                String strRecordingPath = "/storage/emulated/0/XFiles/" + string;


                ////get file duration
                String mediaPath = Uri.parse(strRecordingPath).getPath();
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(mediaPath);
                String strDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                String formatDuration = milliSecondToTimer(Long.parseLong(strDuration));

                mmr.release();


                ///get file size
                String strFileSize = getFileSize(new File(strRecordingPath));


                ////get file modified date and time
                File fileDate = new File(strRecordingPath);
                Date lastModDate = new Date(fileDate.lastModified());
                Log.d("File last modified @ : ", lastModDate.toString());


                SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
                String strDateTime = simpleDate.format(lastModDate);


                FileHelper fileHelper = new FileHelper();
                fileHelper.setStrRecordingFilePath(string);
                fileHelper.setStrRecordingDuration(formatDuration);
                fileHelper.setStrRecordingSiize(strFileSize);
                fileHelper.setStrRecordingTimeDate(strDateTime);


                list.add(fileHelper);


            }
            adpter = new MyAdapter(list, getActivity(), inflater);
            rvRecordingFile.setAdapter(adpter);
            adpter.notifyDataSetChanged();



            Collections.sort(list, new Comparator<FileHelper>() {
                @Override
                public int compare(FileHelper lhs, FileHelper rhs) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String strCurrentDateandTime = sdf.format(new Date());
                    return strCurrentDateandTime.compareTo(rhs.getStrRecordingTimeDate());
                }
            });

            if (aBooleanRecordingNotFound) {
                tvRecordingFileNotFound.setVisibility(View.VISIBLE);
            }
        }

        return parentView;
    }


    public String milliSecondToTimer(long millisecond) {

        String finalTimerString = "";
        String secondString;

        //convert total time into duration

        int hours = (int) (millisecond / (1000 * 60 * 60));
        int minutes = (int) (millisecond % (1000 * 60 * 60)) / (1000 * 60);
        int second = (int) (millisecond % (1000 * 60 * 60)) % (1000 * 60) / (1000);

        //Add hours if there


        if (hours > 0) {
            finalTimerString = hours + ":";

        }

        //prepending 0 to second if it is one digit

        if (second < 10) {

            secondString = "0" + second;
        } else {

            secondString = "" + second;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondString;

        return finalTimerString;

    }

    public String getFileSize(File file) {

        if (!file.isFile()) {
            throw new IllegalArgumentException("Expected a file");
        }
        final double length = file.length();

        if (length > MiB) {
            return format.format(length / MiB) + " MB";
        }
        if (length > KiB) {
            return format.format(length / KiB) + " KB";
        }
        return format.format(length) + " B";
    }
}
