package com.james.cyprusapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;

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
        ContentValues cv = new ContentValues();
        for (int i = 1; i <= 3; i++) {
            cv.put(MyDBHElper.COLUMN_NAME, "name " + i);
            cv.put(MyDBHElper.COLUMN_AGE, i);
            cv.put(MyDBHElper.COLUMN_PHOTO, "photo " + i);
            getActivity().getContentResolver().insert(MyContentProvider.CONTENT_ADDRESS_URI, cv);
        }
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("DSA", "_id " + adapter.getItem(position).getId());
            }
        });

        adapter = new UsersAdapter(getActivity());
        mLv.setAdapter(adapter);


        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        return  v;
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
