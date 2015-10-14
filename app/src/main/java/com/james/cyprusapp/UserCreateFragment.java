package com.james.cyprusapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by james on 13.10.15.
 */
public class UserCreateFragment extends Fragment {

    @Bind(R.id.name)
    TextInputLayout mName;
    @Bind(R.id.age)
    TextInputLayout mAge;
    @Bind(R.id.delete_btn)
    Button mDeleteBtn;
    @Bind(R.id.create_btn)
    Button mCreateBtn;
    @Bind(R.id.profile_image)
    CircleImageView mProfilePhoto;

    private View v;
    private Observable<User> mCreateObservable;
    private Observable<Long> mDeleteObservable;
    private long id = -1;
    public static  final int SELECT_FILE = 112;
    public static  final int REQUEST_CAMERA = 113;

    private String imagePath = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_user_create, container, false);
        ButterKnife.bind(this, v);
        if(getArguments()!=null){
            User user = getArguments().getParcelable("user");
            mName.getEditText().setText(user.getName());
            mAge.getEditText().setText(user.getAge() + "");
            id = user.getId();
            imagePath = user.getPhoto();
            mCreateBtn.setText("Изменить");
            mProfilePhoto.setImageBitmap(MainActivity.setImageInImageView(imagePath));
            mDeleteBtn.setVisibility(View.VISIBLE);
        }

        return  v;
    }

    @OnClick(R.id.create_btn)
    void onCreateBtnClick(){
        final String name = mName.getEditText().getText().toString().trim();
        final String age = mAge.getEditText().getText().toString().trim();
        boolean validator = true;
        if("".equals(name)){
            validator = false;
            Snackbar.make(v, R.string.input_name, Snackbar.LENGTH_SHORT).show();
        }
        if("".equals(age)){
            validator = false;
            Snackbar.make(v, R.string.input_age, Snackbar.LENGTH_SHORT).show();
        }
        if("".equals(imagePath)){
            validator = false;
            Snackbar.make(v, R.string.no_photo, Snackbar.LENGTH_SHORT).show();
        }
        if (validator){
            User user = new User();
            user.setName(name);
            user.setAge(Integer.valueOf(age));
            user.setId(id);
            user.setPhoto(imagePath);
            mCreateObservable = Observable.just(user);
            mCreateObservable.subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe(UserCreateFragment.this::saveInDB,
                               Throwable::printStackTrace,
                               UserCreateFragment.this::finishRedact);

        }
    }

    @OnClick(R.id.delete_btn)
    void onDeleteBtnClick(){
        mDeleteObservable = Observable.just(id);
        mDeleteObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(UserCreateFragment.this::removeFromDB,
                        Throwable::printStackTrace,
                        UserCreateFragment.this::finishRedact);
    }


    private void saveInDB(User user) {
        ContentValues cv = new ContentValues();
        if(user.getId() != -1){
            cv.put(MyDBHElper.COLUMN_ID, user.getId());
        }
        cv.put(MyDBHElper.COLUMN_NAME, user.getName());
        cv.put(MyDBHElper.COLUMN_AGE, user.getAge());
        cv.put(MyDBHElper.COLUMN_PHOTO, user.getPhoto());
        getActivity().getContentResolver().insert(MyContentProvider.CONTENT_ADDRESS_URI, cv);
    }

    private void removeFromDB(long id){
        String[] data = {String.valueOf(id)};
        getActivity().getContentResolver().delete(MyContentProvider.CONTENT_ADDRESS_URI, "_id=?", data);
    }

    public void finishRedact(){
        Log.d("ART", "onCompl");
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @OnClick(R.id.profile_image)
    void selectImage() {
        final CharSequence[] items = { "Камера", "Галерея", "Назад" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Добавить фото!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if ("Камера".equals(items[item])) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if ("Галерея".equals(items[item])) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if ("Назад".equals(items[item])) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                imagePath = getImagePath(data.getData(), getContext());
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                if(imagePath != null){
                    thumbnail = MainActivity.rotateBitmap(thumbnail, imagePath);
                }
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mProfilePhoto.setImageBitmap(thumbnail);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                imagePath = getImagePath(selectedImageUri, getContext());
                Bitmap bm = MainActivity.setImageInImageView(imagePath);
                mProfilePhoto.setImageBitmap(bm);
            }
        }
    }

    public static String getImagePath(Uri uri, Context context){
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor =  context.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        if (mCreateObservable != null){
            mCreateObservable.unsubscribeOn(Schedulers.io());
            mCreateObservable = null;
        }
        if(mDeleteObservable != null){
            mDeleteObservable.unsubscribeOn(Schedulers.io());
            mDeleteObservable = null;
        }
        super.onDestroy();
    }
}
