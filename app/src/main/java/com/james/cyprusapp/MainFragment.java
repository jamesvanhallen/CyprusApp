package com.james.cyprusapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
                b.putParcelable("user", adapter.getItem(position));
                fragment.setArguments(b);
                MainActivity.changeFragment(fragment, true, getActivity(), MainActivity.container);
            }
        });

        adapter = new UsersAdapter(getActivity());
        mLv.setAdapter(adapter);


        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        return  v;
    }

    @OnClick(R.id.fabButton)
    void onFabButtonClick(){
        MainActivity.changeFragment(new UserCreateFragment(), true, getActivity(), MainActivity.container);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                MyContentProvider.CONTENT_ADDRESS_URI, //uri для таблицы Classes
                null, //список столбцов, которые должны присутствовать в выборке
                null, // условие WHERE для выборки
                null, // аргументы для условия WHERE
                null); // порядок сортировки
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
