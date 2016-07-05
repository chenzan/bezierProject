package com.chzan.bezier;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chzan.bezier.stick.StickViewTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenzan on 2016/7/4.
 */
public class NextActivity extends AppCompatActivity {

    private List<Integer> lists;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        initView();
    }

    private void initView() {
        ListView listView = (ListView) findViewById(R.id.lv);
        MyAdapter myAdapter = new MyAdapter();
        lists = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            lists.add(i);
        }
        listView.setAdapter(myAdapter);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.tv);
            TextView stickView = (TextView) view.findViewById(R.id.stickview);
            textView.setText(lists.get(position) + "");
            stickView.setText(lists.get(position) + "");
            String text = stickView.getText().toString();
            stickView.setTag(text);
            StickViewTouchListener stickViewTouchListener = new StickViewTouchListener(stickView);
            stickView.setOnTouchListener(stickViewTouchListener);
            return view;
        }
    }
}
