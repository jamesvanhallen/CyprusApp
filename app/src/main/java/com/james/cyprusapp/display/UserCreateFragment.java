package com.james.cyprusapp.display;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import com.james.cyprusapp.R;
import com.james.cyprusapp.pojo.User;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


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

    private long id = -1;
    private Subscription mSubscription;
    private String imagePath = "";
    private static final String TAG = "UserCreateFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_create, container, false);
        ButterKnife.bind(this, v);

        if(getArguments()!=null){
            User mUser = getArguments().getParcelable("user");
            initViews(mUser);
        }

        return v;
    }

    private void initViews(User user) {

        mName.getEditText().setText(user.getName());
        mAge.getEditText().setText(user.getAge() + "");
        id = user.getId();
        imagePath = user.getPhoto();
        mCreateBtn.setText(R.string.Update);
        mDeleteBtn.setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).getBitmap(imagePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mProfilePhoto::setImageBitmap);
    }

    @OnClick(R.id.create_btn)
    void onCreateBtnClick(){
        final String name = mName.getEditText().getText().toString().trim();
        final String age = mAge.getEditText().getText().toString().trim();
        boolean validator = true;
        String error = "";
        if("".equals(name)){
            validator = false;
            error = getResources().getString(R.string.input_name);
        }
        if("".equals(age)){
            validator = false;
            error = getResources().getString(R.string.input_age);
        }
        if("".equals(imagePath)){
            validator = false;
            error = getResources().getString(R.string.no_photo);
        }
        if (validator){
            User user = new User();
            user.setName(name);
            user.setAge(Integer.valueOf(age));
            user.setId(id);
            user.setPhoto(imagePath);
            Observable<User> mCreateObservable = Observable.just(user);
            mSubscription = mCreateObservable.subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe(((MainActivity)getActivity())::saveInDB,
                               Throwable::printStackTrace,
                               UserCreateFragment.this::finishRedact);

        } else {
            AlertDialog.Builder alb = new AlertDialog.Builder(getActivity());
            alb.setMessage(error)
                .setPositiveButton(getResources().getString(R.string.ok), null)
                .create()
                .show();
        }
    }

    @OnClick(R.id.delete_btn)
    void onDeleteBtnClick(){
        Observable<Long> mDeleteObservable = Observable.just(id);
        mSubscription = mDeleteObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(((MainActivity)getActivity())::removeFromDB,
                        Throwable::printStackTrace,
                        UserCreateFragment.this::finishRedact);
    }

    public void finishRedact(){
        FrameLayout container = (FrameLayout) getActivity().findViewById(R.id.container2);
        if(container!=null){
            MainFragment frag = (MainFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
            frag.startLoading();
            ((MainActivity)getActivity()).changeFragment(new EmptyFragment(), false, R.id.container2);
        } else {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @OnClick(R.id.profile_image)
    void selectImage() {
        final CharSequence[] items = {
                getResources().getString(R.string.camera),
                getResources().getString(R.string.gallery),
                getResources().getString(R.string.on_back) };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, (dialog, item) -> {
            if (getResources().getString(R.string.camera).equals(items[item])) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, MainActivity.REQUEST_CAMERA);
            } else if (getResources().getString(R.string.gallery).equals(items[item])) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(intent, "Select File"),
                        MainActivity.SELECT_FILE);
            } else if (getResources().getString(R.string.on_back).equals(items[item])) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MainActivity.REQUEST_CAMERA) {
                Uri selectedUri = data.getData();
                if(selectedUri != null){
                    Log.d(TAG, "uri != null");
                    imagePath = ((MainActivity)getActivity()).getImagePath(data.getData(), getContext());
                }
                else {
                    Log.d(TAG, "uri == null");
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");
                    FileOutputStream fo;
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    try {
                        destination.createNewFile();
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imagePath = destination.getPath();
                }

            } else if (requestCode == MainActivity.SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                imagePath = ((MainActivity)getActivity()).getImagePath(selectedImageUri, getContext());
            }
            ((MainActivity)getActivity()).getBitmap(imagePath)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mProfilePhoto::setImageBitmap);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("name", mName.getEditText().getText().toString());
        outState.putInt("age", Integer.parseInt(mAge.getEditText().getText().toString()));
        outState.putString("photo", mName.getEditText().getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
        super.onDestroy();
    }
}
