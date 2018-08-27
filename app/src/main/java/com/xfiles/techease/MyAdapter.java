package com.xfiles.techease;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.File;
import java.io.IOException;
import java.util.List;
import br.com.joinersa.oooalertdialog.Animation;
import br.com.joinersa.oooalertdialog.OnClickListener;
import br.com.joinersa.oooalertdialog.OoOAlertDialog;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


    private List<FileHelper> fileHelpers;
    private Context context;
    private LayoutInflater inflater;
    private String strPath;

    public MyAdapter(List<FileHelper> fileHelpers, Context context, LayoutInflater inflaters) {
        this.fileHelpers = fileHelpers;
        this.context = context;
        this.inflater = inflaters;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recording_file, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {

        final FileHelper fileHelper = fileHelpers.get(position);
        holder.tvRecordingFiles.setText(fileHelper.getStrRecordingFilePath());
        holder.tvRecordingDuration.setText(fileHelper.getStrRecordingDuration());
        holder.tvRecordingDateTime.setText(fileHelper.getStrRecordingTimeDate());

        holder.tvRecordingFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String string = fileHelper.getStrRecordingFilePath();
                DialogUtils.MediaPlayerDialog(inflater, context, string);

            }
        });

        holder.tvRecordingDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = fileHelper.getStrRecordingFilePath();
                DialogUtils.MediaPlayerDialog(inflater, context, string);


            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strPath = "/storage/emulated/0/XFiles/" + fileHelper.getStrRecordingFilePath();


                new OoOAlertDialog.Builder((Activity) context)
                        .setTitle("XFile")
                        .setMessage("Do you want to delete Recording?")
                        .setAnimation(Animation.ZOOM)
                        .setPositiveButton("Delete", new OnClickListener() {
                            @Override
                            public void onClick() {

                                DeleteRecordingFile(strPath);

                            }
                        })
                        .setNegativeButton("Cancel", new OnClickListener() {
                            @Override
                            public void onClick() {

                            }
                        }).build();


            }
        });
    }

    @Override
    public int getItemCount() {
        return fileHelpers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRecordingFiles, tvRecordingDuration, tvRecordingDateTime;
        LinearLayout linearLayout;
        ImageView ivDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.layout);

            tvRecordingFiles = itemView.findViewById(R.id.tv_recording_file);
            tvRecordingDuration = itemView.findViewById(R.id.tv_recording_duration);
            tvRecordingDateTime = itemView.findViewById(R.id.tv_recording_date_time);
            ivDelete = itemView.findViewById(R.id.iv_delete);

        }

    }

    private void DeleteRecordingFile(String strPath) {
        File file = new File(strPath);
        file.delete();
        if (file.exists()) {
            try {
                file.getCanonicalFile().delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (file.exists()) {
                context.getApplicationContext().deleteFile(file.getName());
            }

        }

        Utilities.connectFragmentWithOutBackStack(context, new RecordingViewFragment());
    }
}