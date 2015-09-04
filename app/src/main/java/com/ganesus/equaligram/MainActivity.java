package com.ganesus.equaligram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.ByteBuffer;


public class MainActivity extends AppCompatActivity {
    private ByteBuffer buffBitmap = null;

    public  void onClicker(View v){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = (TextView) findViewById(R.id.tv);
        buffBitmap = loadBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.lena));

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap canvas = Bitmap.createBitmap(500, 500, conf);

        Bitmap tes = genHistogram(buffBitmap, canvas);

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageBitmap(tes);
    }

    public native ByteBuffer loadBitmap(Bitmap bitmap);
    public native Bitmap genHistogram(ByteBuffer bitmem, Bitmap canvas);

    static {
        System.loadLibrary("equaligram");
    }
}
