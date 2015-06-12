package com.monkeyviewcontroller.snapthat.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.monkeyviewcontroller.snapthat.Models.FriendRequest;
import com.monkeyviewcontroller.snapthat.Models.Game;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.monkeyviewcontroller.snapthat.R;
import com.parse.ParseCloud;
import com.parse.ParseUser;

public class CurrentGameListAdapter extends ArrayAdapter<Game> {

    private static class ViewHolder {
        TextView tvItemTerm;
        TextView tvItemCreator;
        TextView tvItemDate;
        ImageView ivGoToSnap;
    }

    public CurrentGameListAdapter(Context context, List<Game> objects) {
        super(context, R.layout.list_item_currentgame, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Game currentGame = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_currentgame, parent, false);
            viewHolder.tvItemTerm = (TextView) convertView.findViewById(R.id.tvItemTerm);
            viewHolder.tvItemCreator = (TextView) convertView.findViewById(R.id.tvItemCreator);
            viewHolder.tvItemDate = (TextView) convertView.findViewById(R.id.tvItemDate);
            viewHolder.ivGoToSnap = (ImageView) convertView.findViewById(R.id.ivGoToSnap);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvItemTerm.setText("#" + currentGame.getSearchItem());
        viewHolder.tvItemCreator.setText(currentGame.getCreatorUsername());
        Date temp = currentGame.getCreatedDate();
        viewHolder.tvItemDate.setText(temp.getMonth() + "/" + temp.getDay() + "/" + (temp.getYear()+1900));

        return convertView;
    }

}