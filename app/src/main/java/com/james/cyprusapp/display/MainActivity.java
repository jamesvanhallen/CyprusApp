package com.james.cyprusapp.display;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;
import com.james.cyprusapp.R;
import com.james.cyprusapp.database.UsersContentProvider;
import com.james.cyprusapp.database.UsersDataBase;
import com.james.cyprusapp.pojo.User;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;


public class MainActivity extends AppCompatActivity{

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private User user;
    public static final String DIALOG_ALERT = "DIALOG";
    public static final String TAG = "MainActivity";
    public static  final int SELECT_FILE = 112;
    public static  final int REQUEST_CAMERA = 113;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if(savedInstanceState!=null){
            user = savedInstanceState.getParcelable("user");
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        toolbar.setNavigationOnClickListener(v -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                finish();
            }
        });

        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {
        FrameLayout container2 = (FrameLayout) findViewById(R.id.container2);
        if(savedInstanceState!=null&&savedInstanceState.getParcelable("user")!=null){
            UserCreateFragment fragment = new UserCreateFragment();
            Bundle b = new Bundle();
            b.putParcelable("user", savedInstanceState.getParcelable("user"));
            fragment.setArguments(b);

            if(container2==null){
                changeFragment(fragment, false, R.id.container);
            } else {
                changeFragment(fragment, false, R.id.container2);
            }
        } else {
            if(container2!=null){
                changeFragment(new EmptyFragment(), false, R.id.container2);
            }
        }
        changeFragment(new MainFragment(), false, R.id.container);
    }

    public void changeFragment(Fragment f, boolean addToBackStack, Integer fragContainer) {
        FragmentManager mFm = getSupportFragmentManager();
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

    public Observable<Bitmap> getBitmap(String icon) {

        return Observable.create(subscriber -> {
            subscriber.onNext(createBitmap(icon));
            subscriber.onCompleted();
        });
    }

    public Bitmap createBitmap(String imagePath) {
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
        bm = rotateBitmap(BitmapFactory.decodeFile(imagePath, options), imagePath);

        return bm;
    }

    public Bitmap rotateBitmap(Bitmap newBitmap, String path) {
        try {
            ExifInterface ei = new ExifInterface(path);

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.getWidth(),
                            newBitmap.getHeight(), matrix, true);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.getWidth(),
                            newBitmap.getHeight(), matrix, true);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.getWidth(),
                            newBitmap.getHeight(), matrix, true);
                    break;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return newBitmap;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(getSupportFragmentManager().findFragmentById(R.id.container2)!=null){
            MainFragment frag = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            if(frag.getUser()!=null){
                user = frag.getUser();
            }
            if(user!=null){
                outState.putParcelable("user", user);
            }
        }
        super.onSaveInstanceState(outState);
    }


    public void saveInDB(User user) {
        ContentValues cv = new ContentValues();
        if(user.getId() != -1){
            cv.put(UsersDataBase.COLUMN_ID, user.getId());
        }
        cv.put(UsersDataBase.COLUMN_NAME, user.getName());
        cv.put(UsersDataBase.COLUMN_AGE, user.getAge());
        cv.put(UsersDataBase.COLUMN_PHOTO, user.getPhoto());
        getContentResolver().insert(UsersContentProvider.CONTENT_ADDRESS_URI, cv);
    }

    public void removeFromDB(long id){
        String[] data = {String.valueOf(id)};
        getContentResolver().delete(UsersContentProvider.CONTENT_ADDRESS_URI, "_id=?", data);
    }

    public String getImagePath(Uri uri, Context context){
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

}
