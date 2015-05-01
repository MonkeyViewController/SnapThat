package com.monkeyviewcontroller.snapthat.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.monkeyviewcontroller.snapthat.Models.FriendRequest;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.monkeyviewcontroller.snapthat.R;
import com.parse.ParseCloud;
import com.parse.ParseUser;

public class FriendListAdapter extends ArrayAdapter<STUser> {

    Context context;
    List<STUser> friends;
    private Boolean[] selected;
    private FloatingActionButton fab;

    public FriendListAdapter(Context context, List<STUser> objects, FloatingActionButton fab) {
        super(context, R.layout.list_item_friend, objects);
        this.context = context;
        this.friends = objects;
        this.fab = fab;

        selected = new Boolean[this.friends.size()];
        Arrays.fill(selected, false);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_friend, parent, false);

        TextView tvItemTextUsername = (TextView) view.findViewById(R.id.tvItemTextUsername);
        tvItemTextUsername.setText(friends.get(position).getUsername());

        CheckBox cbMyFriends = (CheckBox)view.findViewById(R.id.cbMyFriends);
        cbMyFriends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selected[position] = isChecked;
                fab.setVisibility(View.GONE);
                for(boolean b: selected)
                {
                    if(b)
                    {
                        fab.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        cbMyFriends.setChecked(selected[position]);

        ImageView ivSettings = (ImageView) view.findViewById(R.id.ivSettings);
        setViewBackgroundWithoutResettingPadding(ivSettings);
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("MVC", "Clicking Settings");

                //TODO: rename, block, etc.

            }
        });



        ImageView ivRemoveFriend = (ImageView) view.findViewById(R.id.ivRemoveFriend);
        setViewBackgroundWithoutResettingPadding(ivRemoveFriend);
        ivRemoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: are you sure you want to remove this friend?

                Log.d("MVC", "Clicking Remove friend.");
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("user", ParseUser.getCurrentUser().getObjectId());
                params.put("friend", friends.get(position).getObjectId());
                ParseCloud.callFunctionInBackground("removefriend", params);
                friends.remove(position);
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