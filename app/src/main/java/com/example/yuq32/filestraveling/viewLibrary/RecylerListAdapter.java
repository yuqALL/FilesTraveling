package com.example.yuq32.filestraveling.viewLibrary;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yuq32.filestraveling.R;
import com.example.yuq32.filestraveling.utils.FileDown;

import java.util.List;

public class RecylerListAdapter extends RecyclerView.Adapter<RecylerListAdapter.ViewHolder> {

  //下载文件的list
  private List<FileDown> fileDownList;

  //当前下载的文件
  private FileDown currentFileDown;
  //当前文件下载持续时间
  private int currentDuration = 0;

  //当前是否下载标志
  private boolean isPlaying = false;

  private static final int SECOND_MS = 1000;

  /**
   * needed handler for recyclerview
   */
  private Handler mHandler = new Handler();

  //计算秒数
  private final Runnable mRunnable = new Runnable() {
    @Override public void run() {
      currentDuration += 1;//一秒加1
      mHandler.postDelayed(mRunnable, SECOND_MS);
    }
  };

  //set下载文件列表
  public void setFileDownList(List<FileDown> fileDownList) {
    this.fileDownList = fileDownList;
    notifyDataSetChanged();
  }

  /**
   * Create View holder
   */
  @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View view =
        LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
    ViewHolder viewHolder = new ViewHolder(view);
    return viewHolder;
  }

  /**
   * I did little bit trick on this method
   * Normally ProgressLayout library already has runnable to update
   * current second and current progress. But with ViewHolder pattern
   * when we scroll down or up, viewholder objects which we created before
   * are using again. That is why i keep some data to check whether item is playing
   * or not.
   */
  @Override public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
    //得到下载文件
    final FileDown fileDown = fileDownList.get(i);

    //设置下载时长
    viewHolder.textViewDuration.setText(calculateFileDownDuration(fileDown.getDurationInSec()));
    //设置文件名
    viewHolder.textViewFileName.setText(fileDown.getFileName());
    viewHolder.textViewOwner.setText(fileDown.getOwnerName());  //设置发送者名
    viewHolder.imageViewAction.setBackgroundResource(R.drawable.play);  //设置下载按钮的图标
    viewHolder.progressLayout.setMaxProgress(fileDown.getDurationInSec());  //设置progress

    if (currentFileDown != null && currentFileDown == fileDown) {
      viewHolder.imageViewAction.setBackgroundResource(
          isPlaying ? R.drawable.pause : R.drawable.play);
      viewHolder.progressLayout.setCurrentProgress(currentDuration);
      if (isPlaying) viewHolder.progressLayout.start(); //设置进度条开始
    } else {
      viewHolder.progressLayout.cancel();  //关闭进度条
    }

    //给图片加上监听
    viewHolder.imageViewAction.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        if (fileDown != currentFileDown) {
          currentFileDown = fileDown;
          mHandler.removeCallbacks(mRunnable);
          currentDuration = 0;
        }

        if (!viewHolder.progressLayout.isPlaying()) {
          isPlaying = true;
          viewHolder.progressLayout.start();
          mHandler.postDelayed(mRunnable, 0);
          viewHolder.imageViewAction.setBackgroundResource(R.drawable.pause);
          notifyDataSetChanged();
        } else {
          isPlaying = false;
          viewHolder.progressLayout.stop();
          mHandler.removeCallbacks(mRunnable);
          viewHolder.imageViewAction.setBackgroundResource(R.drawable.play);
          notifyDataSetChanged();
        }
      }
    });
    viewHolder.progressLayout.setProgressLayoutListener(new ProgressLayoutListener() {
      @Override public void onProgressCompleted() {
        viewHolder.imageViewAction.setBackgroundResource(R.drawable.play);
      }

      @Override public void onProgressChanged(int seconds) {
        viewHolder.textViewDuration.setText(calculateFileDownDuration(seconds));
      }
    });
  }

  /**
   * List count
   */
  @Override public int getItemCount() {
    return fileDownList.size();
  }

  //计算下载所需的时间
  private String calculateFileDownDuration(int seconds) {
    return new StringBuilder(String.valueOf(seconds / 60))
        .append(":")
        .append(String.valueOf(seconds % 60))
        .toString();
  }

  /**
   * ViewHolder
   */
  public static class ViewHolder extends RecyclerView.ViewHolder {


    ImageView imageViewAction;

    ProgressLayout progressLayout;

    TextView textViewFileName;

    TextView textViewOwner;

    TextView textViewDuration;

    public ViewHolder(View itemView) {
      super(itemView);
      imageViewAction=(ImageView)itemView.findViewById(R.id.imageviewAction);
      progressLayout=(ProgressLayout)itemView.findViewById(R.id.progressLayout);
      textViewFileName=(TextView)itemView.findViewById(R.id.textviewFileName);
      textViewOwner=(TextView)itemView.findViewById(R.id.textviewOwner);
      textViewDuration=(TextView)itemView.findViewById(R.id.textviewDuration);
    }

  }
}
