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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.monkeyviewcontroller.snapthat.Models.FriendRequest;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.monkeyviewcontroller.snapthat.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseUser;

public class FriendRequestListAdapter extends ArrayAdapter<STUser> {

    Context context;
    List<STUser> usersRequesting;
    private TextDrawable mDrawableBuilder;
    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;

    private static class ViewHolder {
        TextView tvItemTextUsername;
        ImageView ivAccept;
        ImageView ivDecline;
        ImageView ivThumbnail;
    }

    public FriendRequestListAdapter(Context context, List<STUser> objects) {
        super(context, R.layout.list_item_friendrequest, objects);
        this.usersRequesting = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final STUser friend = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_friendrequest, parent, false);
            viewHolder.tvItemTextUsername = (TextView) convertView.findViewById(R.id.tvItemTextUsername);
            viewHolder.ivAccept = (ImageView) convertView.findViewById(R.id.ivAccept);
            viewHolder.ivDecline = (ImageView) convertView.findViewById(R.id.ivDecline);
            viewHolder.ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setThumbnail(viewHolder.ivThumbnail, String.valueOf(friend.getWins()));
        viewHolder.tvItemTextUsername.setText(usersRequesting.get(position).getUsername());

        setViewBackgroundWithoutResettingPadding(viewHolder.ivAccept);
        viewHolder.ivAccept.setOnClickListener(new View.OnClickListener() {
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

        setViewBackgroundWithoutResettingPadding(viewHolder.ivDecline);
        viewHolder.ivDecline.setOnClickListener(new View.OnClickListener() {
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

        return convertView;
    }

    public void setThumbnail(ImageView view, String score) {

        int color = mColorGenerator.getRandomColor();
        mDrawableBuilder = TextDrawable.builder()
                .buildRound(score, color);
        view.setImageDrawable(mDrawableBuilder);
    }

    public static void setViewBackgroundWithoutResettingPadding(final View v) {
        final int paddingBottom = v.getPaddingBottom(), paddingLeft = v.getPaddingLeft();
        final int paddingRight = v.getPaddingRight(), paddingTop = v.getPaddingTop();
        v.setBackgroundColor(Color.TRANSPARENT);
        v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

}
