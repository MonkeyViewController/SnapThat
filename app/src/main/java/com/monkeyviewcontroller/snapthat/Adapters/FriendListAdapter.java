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

    private FloatingActionButton fab;
    private Boolean[] selected;

    private static class ViewHolder {
        TextView tvWins;
        TextView tvItemTextUsername;
        ImageView ivSettings;
        ImageView ivRemoveFriend;
        ImageView ivWinsBackground;
        CheckBox cbMyFriends;
    }

    public FriendListAdapter(Context context, List<STUser> objects, FloatingActionButton fab) {
        super(context, R.layout.list_item_friend, objects);
        this.fab = fab;

        selected = new Boolean[objects.size()];
        Arrays.fill(selected, false);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final STUser friend = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_friend, parent, false);
            viewHolder.tvWins = (TextView) convertView.findViewById(R.id.tvWins);
            viewHolder.tvItemTextUsername = (TextView) convertView.findViewById(R.id.tvItemTextUsername);
            viewHolder.ivSettings = (ImageView) convertView.findViewById(R.id.ivSettings);
            viewHolder.ivRemoveFriend = (ImageView) convertView.findViewById(R.id.ivRemoveFriend);
            viewHolder.ivWinsBackground = (ImageView) convertView.findViewById(R.id.ivWinsBackground);
            viewHolder.cbMyFriends = (CheckBox) convertView.findViewById(R.id.cbMyFriends);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.cbMyFriends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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

        viewHolder.cbMyFriends.setChecked(selected[position]);

        viewHolder.tvWins.setText(String.valueOf(friend.getWins()));
        viewHolder.tvItemTextUsername.setText(friend.getUsername());

        setViewBackgroundWithoutResettingPadding(viewHolder.ivSettings);
        viewHolder.ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("MVC", "Clicking Settings");

                //TODO: rename, block, etc.

            }
        });

        setViewBackgroundWithoutResettingPadding(viewHolder.ivRemoveFriend);
        viewHolder.ivRemoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: are you sure you want to remove this friend?

                Log.d("MVC", "Clicking Remove friend.");
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("user", ParseUser.getCurrentUser().getObjectId());
                params.put("friend",friend.getObjectId());
                ParseCloud.callFunctionInBackground("removefriend", params);
                remove(friend);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public static void setViewBackgroundWithoutResettingPadding(final View v) {
        final int paddingBottom = v.getPaddingBottom(), paddingLeft = v.getPaddingLeft();
        final int paddingRight = v.getPaddingRight(), paddingTop = v.getPaddingTop();
        v.setBackgroundColor(Color.TRANSPARENT);
        v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }
}