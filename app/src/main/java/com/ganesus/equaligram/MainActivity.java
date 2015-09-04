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
    private ByteBuffer buffBitmapGray = null;
    private ByteBuffer buffBitmap1 = null;

    public  void onClicker(View v){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buffBitmap = loadBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.lena));
        buffBitmapGray = createGrayscale(buffBitmap);
        buffBitmap1 = applyAlgo1(buffBitmapGray);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap canvas = Bitmap.createBitmap(780, 320, conf);



        ImageView ivAsli = (ImageView) findViewById(R.id.ivAsli);
        ImageView hisAsli = (ImageView) findViewById(R.id.hisAsli);
        ImageView hisGray = (ImageView) findViewById(R.id.hisGray);
        ImageView ivGray = (ImageView) findViewById(R.id.ivGray);
        ImageView hisAlgo1 = (ImageView) findViewById(R.id.hisAlgo1);
        ImageView ivAlgo1= (ImageView) findViewById(R.id.ivAlgo1);


        ivAsli.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.lena));
        hisAsli.setImageBitmap(genHistogram(buffBitmap, canvas, false));
        ivGray.setImageBitmap(createGrayscaleBmp(buffBitmap));
        ivAlgo1.setImageBitmap(applyAlgo1Bmp(buffBitmapGray));

        canvas = Bitmap.createBitmap(520, 320, conf);
        hisGray.setImageBitmap(genHistogram(buffBitmapGray, canvas, true));

        canvas = Bitmap.createBitmap(520, 320, conf);
        hisAlgo1.setImageBitmap(genHistogram(buffBitmap1, canvas, true));
    }

    public native ByteBuffer loadBitmap(Bitmap bitmap);
    public native ByteBuffer createGrayscale(ByteBuffer bitmem);
    public native ByteBuffer applyAlgo1(ByteBuffer bitmem);

    public native Bitmap genHistogram(ByteBuffer bitmem, Bitmap canvas, boolean isGrayScale);
    public native Bitmap createGrayscaleBmp(ByteBuffer bitmem);
    public native Bitmap applyAlgo1Bmp(ByteBuffer bitmem);

    static {
        System.loadLibrary("equaligram");
    }
}
