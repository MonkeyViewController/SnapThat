package com.monkeyviewcontroller.snapthat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import com.amulyakhare.textdrawable.TextDrawable;
import com.monkeyviewcontroller.snapthat.Models.Comment;
import com.monkeyviewcontroller.snapthat.R;

public class BasicCommentListAdapter extends ArrayAdapter<Comment> {

    private static class ViewHolder {
        TextView tvUsername;
        TextView tvMessage;
    }

    public BasicCommentListAdapter(Context context, List<Comment> objects) {
        super(context, R.layout.list_item_basiccomment, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Comment comment = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_basiccomment, parent, false);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvUsername.setText(comment.getCommenter().getUsername());
        viewHolder.tvMessage.setText(comment.getMessage());
        return convertView;
    }
}