package com.james.cyprusapp.display;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.james.cyprusapp.R;

/**
 * Created by james on 15.10.15.
 */
public class MyDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_user_create, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.container3, new UserCreateFragment())
                .commit();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Window window = getDialog().getWindow();
        window.setLayout(width/2, width/2);
        window.setGravity(Gravity.CENTER);
    }
}
