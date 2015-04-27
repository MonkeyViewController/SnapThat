package com.monkeyviewcontroller.snapthat.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.monkeyviewcontroller.snapthat.Models.FriendRequest;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.monkeyviewcontroller.snapthat.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseUser;

public class FriendRequestListAdapter extends ArrayAdapter<STUser> {

    Context context;
    List<STUser> usersRequesting;

    public FriendRequestListAdapter(Context context, List<STUser> objects) {
        super(context, R.layout.list_item_friendrequest, objects);
        this.context = context;
        this.usersRequesting = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_friendrequest, parent, false);

        TextView tvItemTextUsername = (TextView) view.findViewById(R.id.tvItemTextUsername);
        tvItemTextUsername.setText(usersRequesting.get(position).getUsername());

        ImageButton btnAccept = (ImageButton) view.findViewById(R.id.btnAccept);
        setViewBackgroundWithoutResettingPadding(btnAccept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("MVC", "Clicking Accept");
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("user", ParseUser.getCurrentUser().getObjectId());
                params.put("friend", usersRequesting.get(position).getObjectId());
                ParseCloud.callFunctionInBackground("acceptfriend", params);
                usersRequesting.remove(position);
                notifyDataSetChanged();
            }
        });

        ImageButton btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
        setViewBackgroundWithoutResettingPadding(btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("MVC", "Clicking Cancel");
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("user", ParseUser.getCurrentUser().getObjectId());
                params.put("friend", usersRequesting.get(position).getObjectId());
                ParseCloud.callFunctionInBackground("declinefriend", params);
                usersRequesting.remove(position);
                notifyDataSetChanged();
            }
        });
        return view;
    }

    public static void setViewBackgroundWithoutResettingPadding(final View v) {
        final int paddingBottom = v.getPaddingBottom(), paddingLeft = v.getPaddingLeft();
        final int paddingRight = v.getPaddingRight(), paddingTop = v.getPaddingTop();
        v.setBackgroundColor(Color.TRANSPARENT);
        v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

}
