package com.james.cyprusapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.james.cyprusapp.database.MyDBHElper;
import com.james.cyprusapp.R;
import com.james.cyprusapp.pojo.User;
import com.james.cyprusapp.display.MainActivity;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by fappsilya on 13.10.15.
 */
public class UsersAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;

    // Default constructor
    public UsersAdapter(Context context) {
        super(context, null, true);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView mName = (TextView) view.findViewById(R.id.name);
        String name = cursor.getString( cursor.getColumnIndex(MyDBHElper.COLUMN_NAME) );
        mName.setText(name);

        CircleImageView mPhoto = (CircleImageView) view.findViewById(R.id.profile_image);
        String image = cursor.getString(cursor.getColumnIndex(MyDBHElper.COLUMN_PHOTO));
        mPhoto.setImageBitmap(((MainActivity) context).setImageInImageView(image));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // R.layout.list_row is your xml layout for each row
        return cursorInflater.inflate(R.layout.item_user, parent, false);
    }

    @Override
    public User getItem(int position) {
        User user = new User();
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
            user.setId(cursor.getLong(cursor.getColumnIndex(MyDBHElper.COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndex(MyDBHElper.COLUMN_NAME)));
            user.setAge(cursor.getInt(cursor.getColumnIndex(MyDBHElper.COLUMN_AGE)));
            user.setPhoto(cursor.getString(cursor.getColumnIndex(MyDBHElper.COLUMN_PHOTO)));
        Log.d("dfsd", " " + position);

        return user;
    }
}