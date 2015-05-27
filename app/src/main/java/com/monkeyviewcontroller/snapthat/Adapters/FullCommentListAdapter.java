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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.monkeyviewcontroller.snapthat.Models.Comment;
import com.monkeyviewcontroller.snapthat.Models.FriendRequest;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.monkeyviewcontroller.snapthat.R;
import com.parse.ParseCloud;
import com.parse.ParseUser;

public class FullCommentListAdapter extends ArrayAdapter<Comment> {

    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
    private TextDrawable mDrawableBuilder;

    private static class ViewHolder {
        ImageView ivThumbnail;
        TextView tvUsername;
        TextView tvMessage;
        TextView tvTime;
    }

    public FullCommentListAdapter(Context context, List<Comment> objects) {
        super(context, R.layout.list_item_fullcomment, objects);
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        //If the user name of the commenter is us, use menu type 0, else use menu type 1
        if( weAreCommenter(getItem(position).getCommenter().getUsername()))
            return 0;
        else
            return 1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Comment comment = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_fullcomment, parent, false);
            viewHolder.ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setThumbnail(viewHolder.ivThumbnail, String.valueOf(comment.getCommenter().getWins()));
        viewHolder.tvUsername.setText(comment.getCommenter().getUsername());
        viewHolder.tvMessage.setText(comment.getMessage());
        viewHolder.tvTime.setText(getFriendlyTime(comment.getCreatedDate()));

        return convertView;
    }

    public String getFriendlyTime(Date d)
    {
        String friendly;
        Date today = new Date();
        long millis = today.getTime() - d.getTime();
        int years = (int) (millis/(1000 * 60 * 60 * 24 * 7 * 52));
        int weeks = (int) (millis/(1000 * 60 * 60 * 24 * 7));
        int days  = (int) (millis/(1000 * 60 * 60 * 24));
        int hours = (int) (millis/(1000 * 60 * 60));
        int mins  = (int) (millis/(1000 * 60));

        friendly = mins + "m";

        if(hours > 0 )
            friendly = hours + "h";

        if(days > 0 )
            friendly = days + "d";

        if(weeks > 0 )
            friendly = weeks + "w";

        if(years > 0 )
            friendly = years + "y";

        return friendly;
    }

    public void setThumbnail(ImageView view, String score) {

        int color = mColorGenerator.getRandomColor();
        mDrawableBuilder = TextDrawable.builder()
                .buildRound(score, color);
        view.setImageDrawable(mDrawableBuilder);
    }

    private boolean weAreCommenter(String username)
    {
        return ParseUser.getCurrentUser().getUsername().equals(username);
    }
}