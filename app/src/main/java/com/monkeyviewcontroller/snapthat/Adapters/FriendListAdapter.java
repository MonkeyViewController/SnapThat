package com.monkeyviewcontroller.snapthat.Adapters;

import android.content.Context;
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
import java.util.List;
import com.monkeyviewcontroller.snapthat.Models.FriendRequest;
import com.monkeyviewcontroller.snapthat.R;

public class FriendListAdapter extends ArrayAdapter<FriendRequest> {

    Context context;
    List<FriendRequest> friendRequests;
    private Boolean[] selected;
    private String currentUser;

    public FriendListAdapter(Context context, List<FriendRequest> objects, String currentUser) {
        super(context, R.layout.list_item_friendrequest, objects);
        this.context = context;
        this.friendRequests = objects;
        this.currentUser = currentUser;

        selected = new Boolean[this.friendRequests.size()];
        Arrays.fill(selected, false);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_friend, parent, false);

        TextView tvItemTextUsername = (TextView) view.findViewById(R.id.tvItemTextUsername);

        if(friendRequests.get(position).getFriendOne().equals(currentUser))
            tvItemTextUsername.setText(friendRequests.get(position).getFriendTwo());
        else
            tvItemTextUsername.setText(friendRequests.get(position).getFriendOne());

        CheckBox cbMyFriends = (CheckBox)view.findViewById(R.id.cbMyFriends);
        cbMyFriends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selected[position] = isChecked;
            }
        });

        cbMyFriends.setChecked(selected[position]);

        ImageButton btnSettings = (ImageButton) view.findViewById(R.id.ibtnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("MVC", "Clicking Settings");

                //TODO: rename, block, etc.

            }
        });

        ImageButton btnRemoveFriend = (ImageButton) view.findViewById(R.id.ibtnRemoveFriend);
        btnRemoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: are you sure you want to remove this friend?

                Log.d("MVC", "Clicking Remove friend.");
                FriendRequest fr = friendRequests.get(position);
                fr.deleteInBackground();
                friendRequests.remove(fr);
                notifyDataSetChanged();
            }
        });
        return view;
    }

}