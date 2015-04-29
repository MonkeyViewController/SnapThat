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
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
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

public class GameSelectionListAdapter extends ArrayAdapter<String> {

    Context context;
    List<String> terms;
    private Boolean[] selected;

    public GameSelectionListAdapter(Context context, List<String> objects) {
        super(context, android.R.layout.simple_list_item_multiple_choice, objects);
        this.context = context;
        this.terms = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);

        final CheckedTextView ctvGameTerm = (CheckedTextView)view.findViewById(android.R.id.text1);

        ctvGameTerm.setText(terms.get(position));

        //TODO: like the friendlist adapter, scrolling will remove the checks. Need an alternative fix to the one used in the FriendListAdapter.

        /*for(Boolean b: selected)
        {
            Log.d("MVC", "GetViewB: " + b);
        }

        ctvGameTerm.setChecked(selected[position]);*/

        return view;
    }

    private void test()
    {

    }
}