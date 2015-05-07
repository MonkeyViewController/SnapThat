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

public class HighScoreListAdapter extends ArrayAdapter<STUser> {

    private static class ViewHolder {
        TextView tvWins;
        TextView tvItemTextUsername;
        ImageView ivWinsBackground;
    }

    public HighScoreListAdapter(Context context, List<STUser> objects) {
        super(context, R.layout.list_item_highscore, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final STUser friend = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_highscore, parent, false);
            viewHolder.tvWins = (TextView) convertView.findViewById(R.id.tvWins);
            viewHolder.tvItemTextUsername = (TextView) convertView.findViewById(R.id.tvItemTextUsername);
            viewHolder.ivWinsBackground = (ImageView) convertView.findViewById(R.id.ivWinsBackground);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvWins.setText(String.valueOf(friend.getWins()));
        viewHolder.tvItemTextUsername.setText(friend.getUsername());

        return convertView;
    }
}