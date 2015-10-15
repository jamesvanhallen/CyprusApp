package com.james.cyprusapp.display;

import android.app.Dialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.james.cyprusapp.database.MyContentProvider;
import com.james.cyprusapp.R;
import com.james.cyprusapp.pojo.User;
import com.james.cyprusapp.adapter.UsersAdapter;

import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fappsilya on 13.10.15.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.lv)
    ListView mLv;
    private UsersAdapter adapter;
    private User mUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, v);

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserCreateFragment fragment = new UserCreateFragment();
                Bundle b = new Bundle();
                mUser = adapter.getItem(position);
                b.putParcelable("user", mUser);
                fragment.setArguments(b);
                FrameLayout container2 = (FrameLayout) getActivity().findViewById(R.id.container2);
                if(container2!=null){
                    ((MainActivity)getActivity()).changeFragment(fragment, false, R.id.container2);
                } else {
                    ((MainActivity)getActivity()).changeFragment(fragment, true,  R.id.container);
                }
            }
        });

        adapter = new UsersAdapter(getActivity());
        mLv.setAdapter(adapter);


        startLoading();
        return  v;
    }

    public void startLoading() {
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
    }

    @OnClick(R.id.fabButton)
    void onFabButtonClick(){
        FrameLayout container2 = (FrameLayout) getActivity().findViewById(R.id.container2);
        if(container2!=null){
            MyDialog dialog = new MyDialog();
            dialog.show(getActivity().getSupportFragmentManager(), MainActivity.DIALOG_ALERT);
        } else {
            ((MainActivity)getActivity()).changeFragment(new UserCreateFragment(), true, R.id.container);

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                MyContentProvider.CONTENT_ADDRESS_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public User getUser() {
        return mUser;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
