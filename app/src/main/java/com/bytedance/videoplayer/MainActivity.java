package com.bytedance.videoplayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = findViewById(R.id.imageView);
        String url = "https://s3.pstatp.com/toutiao/static/img/logo.271e845.png";
        Glide.with(this).load(url).into(imageView);

        final Uri uri = getIntent().getData();
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener(){

            @Override

            public void onClick(View v){
                Intent i = new Intent(MainActivity.this , VideoActivity.class);
                i.putExtra("URI",uri.toString());
                Log.i("nnn","uri" + uri);
                startActivity(i);
            }

        });

    }


}
