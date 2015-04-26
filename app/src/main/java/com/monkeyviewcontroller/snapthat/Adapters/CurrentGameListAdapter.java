package com.monkeyviewcontroller.snapthat.Adapters;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.monkeyviewcontroller.snapthat.Models.FriendRequest;
import com.monkeyviewcontroller.snapthat.Models.Game;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.monkeyviewcontroller.snapthat.R;
import com.parse.ParseCloud;
import com.parse.ParseUser;

public class CurrentGameListAdapter extends ArrayAdapter<Game> {

    Context context;
    List<Game> currentGames;

    public CurrentGameListAdapter(Context context, List<Game> objects) {
        super(context, R.layout.list_item_currentgame, objects);
        this.context = context;
        this.currentGames = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_currentgame, parent, false);

        TextView tvItemTerm = (TextView) view.findViewById(R.id.tvItemTerm);
        tvItemTerm.setText("#" + currentGames.get(position).getSearchItem());

        TextView tvItemCreator = (TextView) view.findViewById(R.id.tvItemCreator);
        tvItemCreator.setText(currentGames.get(position).getCreatorUsername());

        TextView tvItemDate = (TextView) view.findViewById(R.id.tvItemDate);
        tvItemDate.setText(currentGames.get(position).getCreatedDate().toString());

        ImageButton ibtnGoToSnap = (ImageButton) view.findViewById(R.id.ibtnGoToSnap);
        ibtnGoToSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("MVC", "Clicking go to snap button");
                ViewPager vp = (ViewPager)((Activity)context).findViewById(R.id.pager);
                vp.setCurrentItem(1, true);
            }
        });

        return view;
    }

}