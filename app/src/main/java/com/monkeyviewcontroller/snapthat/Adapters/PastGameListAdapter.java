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
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class PastGameListAdapter extends ArrayAdapter<Game> {

    Context context;
    List<Game> pastGames;

    private static class ViewHolder {
        TextView tvTermAndWinner;
        //total submissions?(not so relevant because goal is speed so submissions will generally be low)
        //total time taken?
        //number of players?
        ImageView ivWinningImage;
    }

    public PastGameListAdapter(Context context, List<Game> objects) {
        super(context, R.layout.list_item_pastgame, objects);
        this.context = context;
        this.pastGames = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Game winningGame = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_pastgame, parent, false);
            viewHolder.tvTermAndWinner = (TextView) convertView.findViewById(R.id.tvTermAndWinner);
            viewHolder.ivWinningImage = (ImageView) convertView.findViewById(R.id.ivWinningImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTermAndWinner.setText(winningGame.getSearchItem() + " [" + this.randomString(6) + "]");

        Picasso.with(getContext()).
                load("https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg") //winningGame.getWinningUrl()
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(viewHolder.ivWinningImage);

        /*viewHolder.tvTermAndWinner.setText(winningGame.getSearchItem() + " [" + winningGame.getWinningUser() + "]");

        Picasso.with(getContext()).
                load("https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg") //winningGame.getWinningUrl()
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(viewHolder.ivWinningImage);*/

        return convertView;
    }

    public String randomString(final int length) {
        Random r = new Random(); // perhaps make it a class variable so you don't make a new one every time
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            char c = (char)(r.nextInt((int)(Character.MAX_VALUE)));
            sb.append(c);
        }
        return sb.toString();
    }

}