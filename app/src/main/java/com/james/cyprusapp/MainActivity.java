package com.james.cyprusapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fappsilya on 23.07.15.
 */
public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    public static int container = R.id.container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.back_arrow));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("MyExpandableRecyclerView");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
            }
        });

        changeFragment(new MainFragment(), false, this, container);
    }

    public static void changeFragment(Fragment f, boolean addToBackStack, Activity activity, Integer fragContainer) {
        FragmentManager mFm = ((AppCompatActivity)activity).getSupportFragmentManager();
        FragmentTransaction ft = mFm.beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(null);
        }

        Fragment oldFragment = mFm.findFragmentById(fragContainer);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.add(fragContainer, f);
        ft.commit();
    }

    public static Bitmap setImageInImageView(String imagePath) {
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(imagePath, options);
        return rotateBitmap(bm, imagePath);
    }

    public static Bitmap rotateBitmap(Bitmap newBitmap, String path) {
        try {
            ExifInterface ei = new ExifInterface(path);

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.getWidth(),
                            newBitmap.getHeight(), matrix, true);
                    Log.e("TAG", "90");
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.getWidth(),
                            newBitmap.getHeight(), matrix, true);
                    Log.e("TAG", "180");
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.getWidth(),
                            newBitmap.getHeight(), matrix, true);
                    Log.e("TAG", "270");
                    break;
            }
        } catch (IOException e) {
            Log.e("TAG", e.getMessage());
        }
        return newBitmap;
    }
}
