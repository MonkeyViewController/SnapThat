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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.monkeyviewcontroller.snapthat.Models.FriendRequest;
import com.monkeyviewcontroller.snapthat.Models.Game;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.monkeyviewcontroller.snapthat.R;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class PastGameListAdapter extends ArrayAdapter<Game> {

    private static class ViewHolder {
        TextView tvTerm;
        TextView tvWinner;
        //total time taken?
        TextView tvPlayers;
        TextView tvSubmissions;
        ImageView ivWinningImage;
    }

    public PastGameListAdapter(Context context, List<Game> objects) {
        super(context, R.layout.list_item_pastgame, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Game winningGame = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_pastgame, parent, false);
            viewHolder.tvWinner = (TextView) convertView.findViewById(R.id.tvWinner);
            viewHolder.tvTerm = (TextView) convertView.findViewById(R.id.tvTerm);
            viewHolder.tvPlayers = (TextView) convertView.findViewById(R.id.tvPlayers);
            viewHolder.tvSubmissions = (TextView) convertView.findViewById(R.id.tvSubmissions);
            viewHolder.ivWinningImage = (ImageView) convertView.findViewById(R.id.ivWinningImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            winningGame.getWinningSubmission().getCreator().fetchIfNeeded().getUsername();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.tvTerm.setText(winningGame.getSearchItem());
        viewHolder.tvWinner.setText(winningGame.getWinningSubmission().getCreatorUsername());
        viewHolder.tvSubmissions.setText(String.valueOf(winningGame.getSubmissions().length()));
        viewHolder.tvPlayers.setText(String.valueOf(winningGame.getParticipants().length()));

        Picasso.with(getContext()).
                load(winningGame.getWinningSubmission().getThumbnailURL())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(viewHolder.ivWinningImage);

        return convertView;
    }
}