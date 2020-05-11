package com.bytedance.videoplayer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;


public class VideoActivity extends AppCompatActivity {
    private Button buttonPlay;
    private Button buttonPause;
    private VideoView videoView;
    private SeekBar seekbar;
    private TextView timestart;
    private TextView timeend;
    private RelativeLayout relativeLayout;

    //刷新机制的标志
    private static final int UPDATE_UI = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_UI) {

                int currentPosition = videoView.getCurrentPosition();
                int totalduration = videoView.getDuration();

                updateTime(timestart, currentPosition);
                updateTime(timeend, totalduration);

                seekbar.setMax(totalduration);
                seekbar.setProgress(currentPosition);

                mHandler.sendEmptyMessageDelayed(UPDATE_UI, 500);

            }
        }
    };

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_video);
        setTitle("VideoView");

        seekbar = findViewById(R.id.sb);
        timestart = findViewById(R.id.timestart);
        timeend = findViewById(R.id.timeend);

        buttonPause = findViewById(R.id.buttonPause);
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
                synchScrollSeekBarAndTime();
            }
        });

        buttonPlay = findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
                Configuration configuration = getResources().getConfiguration();
                if (configuration.orientation == configuration.ORIENTATION_LANDSCAPE) {
                    seekbar.setVisibility(View.GONE);
                    timestart.setVisibility(View.GONE);
                    timeend.setVisibility(View.GONE);
                    buttonPause.setVisibility(View.GONE);
                    buttonPlay.setVisibility(View.GONE);
                    setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                }
                synchScrollSeekBarAndTime();
                mHandler.sendEmptyMessageDelayed(UPDATE_UI, 500);
            }
        });

        videoView = findViewById(R.id.videoView);
        videoView.setVideoPath(getVideoPath(R.raw.bytedance));

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //当屏幕方向是横屏的时候,我们应该对VideoView以及包裹VideoView的布局（也就是对整体）进行拉伸
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (videoView.isPlaying()) {
                seekbar.setVisibility(View.GONE);
                timestart.setVisibility(View.GONE);
                timeend.setVisibility(View.GONE);
                buttonPause.setVisibility(View.GONE);
                buttonPlay.setVisibility(View.GONE);
                setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                getWindow().clearFlags((WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN));
                getWindow().addFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));
            }
        }
        //当屏幕方向是竖屏的时候，竖屏的时候的高我们需要把dp转为px
        else {
            seekbar.setVisibility(View.VISIBLE);
            timestart.setVisibility(View.VISIBLE);
            timeend.setVisibility(View.VISIBLE);
            buttonPause.setVisibility(View.VISIBLE);
            buttonPlay.setVisibility(View.VISIBLE);
            getWindow().clearFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));
            getWindow().addFlags((WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN));
        }
    }

    private void setVideoViewScale(int width, int height) {
        //获取VideoView宽和高
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        //赋值给VideoView的宽和高
        layoutParams.width = width;
        layoutParams.height = height;
        //设置VideoView的宽和高
        videoView.setLayoutParams(layoutParams);
    }

    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(UPDATE_UI);
    }

    private void synchScrollSeekBarAndTime() {
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //进度改变的时候同步Time
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTime(timestart, progress);
            }
            //拖动的时候关闭刷新机制
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(UPDATE_UI);
            }

            //拖动停止同步VideoView和开启刷新机制
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                videoView.seekTo(progress);
                mHandler.sendEmptyMessage(UPDATE_UI);
            }
        });
    }

    public void updateTime(TextView textView, int millisecond) {
        int second = millisecond / 1000; //总共换算的秒
        int hh = second / 3600;  //小时
        int mm = second % 3600 / 60; //分钟
        int ss = second % 60; //时分秒中的秒的得数

        String str = null;
        if (hh != 0) {
            //如果是个位数的话，前面可以加0  时分秒
            str = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            str = String.format("%02d:%02d", mm, ss);
        }
        textView.setText(str);
    }

}
