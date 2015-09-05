package com.ganesus.equaligram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;


public class MainActivity extends AppCompatActivity {
    private ByteBuffer buffBitmap = null;
    private ByteBuffer buffBitmapGray = null;
    private ByteBuffer buffBitmap1 = null;

    SeekBar minSeekbar;
    SeekBar maxSeekbar;
    TextView minTextView;
    TextView maxTextView;
    int min_rgb_value; int max_rgb_value;


    public  void onClicker(View v){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        Bitmap bmp = BitmapFactory.decodeFile(intent.getStringExtra("PATH"));

        if (bmp.getWidth() > 2000){
            bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth()/2, bmp.getHeight()/2, false);
        }

        buffBitmap = loadBitmap(bmp);
        //buffBitmap = loadBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.lena));
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
        final ImageView ivAlgo2 = (ImageView) findViewById(R.id.ivAlgo2);

        this.minSeekbar = (SeekBar) findViewById(R.id.minSeekbar);
        this.maxSeekbar = (SeekBar) findViewById(R.id.maxSeekbar);

        this.minTextView = (TextView) findViewById(R.id.minTextView);
        this.maxTextView = (TextView) findViewById(R.id.maxTextView);

        this.minSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                minTextView.setText(String.valueOf(progress));
                min_rgb_value = progress;
                ivAlgo2.setImageBitmap(applyAlgoLinearBmp(buffBitmapGray,min_rgb_value,max_rgb_value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        this.maxSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                maxTextView.setText(String.valueOf(progress));
                max_rgb_value = progress;
                ivAlgo2.setImageBitmap(applyAlgoLinearBmp(buffBitmapGray,min_rgb_value,max_rgb_value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //set default value
        min_rgb_value = Integer.parseInt(minTextView.getText().toString()); minTextView.setText(String.valueOf(min_rgb_value));
        max_rgb_value = Integer.parseInt(maxTextView.getText().toString()); maxTextView.setText(String.valueOf(max_rgb_value));

        ivAsli.setImageBitmap(bmp);
        hisAsli.setImageBitmap(genHistogram(buffBitmap, canvas, false));
        ivGray.setImageBitmap(createGrayscaleBmp(buffBitmap));
        //TODO change to different imageview
        //TODO change parameter
        ivAlgo1.setImageBitmap(applyAlgo1Bmp(buffBitmapGray));
        //ivAlgo2.setImageBitmap(applyAlgoLinearBmp(buffBitmapGray,min_rgb_value,max_rgb_value));

        canvas = Bitmap.createBitmap(520, 320, conf);
        hisGray.setImageBitmap(genHistogram(buffBitmapGray, canvas, true));

        canvas = Bitmap.createBitmap(520, 320, conf);
        hisAlgo1.setImageBitmap(genHistogram(buffBitmap1, canvas, true));

    }

    public native ByteBuffer loadBitmap(Bitmap bitmap);
    public native ByteBuffer createGrayscale(ByteBuffer bitmem);
    public native ByteBuffer applyAlgo1(ByteBuffer bitmem);
    public native ByteBuffer applyAlgoLinear(ByteBuffer bitmem,int new_min,int new_max);

    public native Bitmap genHistogram(ByteBuffer bitmem, Bitmap canvas, boolean isGrayScale);
    public native Bitmap createGrayscaleBmp(ByteBuffer bitmem);
    public native Bitmap applyAlgo1Bmp(ByteBuffer bitmem);
    public native Bitmap applyAlgoLinearBmp(ByteBuffer bitmem,int new_min,int new_max);

    static {
        System.loadLibrary("equaligram");
    }
}
