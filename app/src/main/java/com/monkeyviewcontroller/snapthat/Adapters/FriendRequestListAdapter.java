package com.monkeyviewcontroller.snapthat.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import com.monkeyviewcontroller.snapthat.Models.FriendRequest;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.monkeyviewcontroller.snapthat.R;

public class FriendRequestListAdapter extends ArrayAdapter<STUser> {

    Context context;
    List<STUser> usersRequesting;
    String currentObjectId;

    public FriendRequestListAdapter(Context context, List<STUser> objects, String currentObjectId) {
        super(context, R.layout.list_item_friendrequest, objects);
        this.context = context;
        this.currentObjectId = currentObjectId;
        this.usersRequesting = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_friendrequest, parent, false);

        TextView tvItemTextUsername = (TextView) view.findViewById(R.id.tvItemTextUsername);
        tvItemTextUsername.setText(usersRequesting.get(position).getUsername());

        ImageButton btnAccept = (ImageButton) view.findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("MVC", "Clicking Accept");
                //TODO: Remove friendrequest from database
                /*FriendRequest fr = friendRequests.get(position);
                fr.put("status", 1);
                fr.saveInBackground();
                friendRequests.remove(fr);
                notifyDataSetChanged();*/
            }
        });

        ImageButton btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("MVC", "Clicking Cancel");
                //TODO: Remove friendrequest from database
                /*FriendRequest fr = friendRequests.get(position);
                fr.deleteInBackground();
                friendRequests.remove(fr);
                notifyDataSetChanged();*/
            }
        });
        return view;
    }

}
