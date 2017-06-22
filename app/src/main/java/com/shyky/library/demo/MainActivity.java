package com.shyky.library.demo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shyky.library.widget.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private class TextAdapter extends RecyclerView.Adapter<TextAdapter.TextViewHolder> {
        private final LayoutInflater layoutInflater;
        private List<String> data;

        public TextAdapter(Context context, List<String> data) {
            layoutInflater = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TextViewHolder(layoutInflater.inflate(R.layout.item_text, parent, false));
        }

        @Override
        public void onBindViewHolder(TextViewHolder holder, int position) {
            // 此方法中的position参数不需要减去加入的header view个数，因为内部已经处理了，否则不正常
            holder.textView.setText(getItem(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public String getItem(int position) {
            return data.get(position);
        }

        public class TextViewHolder extends RecyclerView.ViewHolder {
            public final TextView textView;

            public TextViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UltimateRecyclerView recyclerView = (UltimateRecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        TextView header1 = new TextView(this);
        header1.setTextColor(Color.RED);
        header1.setText("I am header 1.");

        TextView header2 = new TextView(this);
        header2.setTextColor(Color.GREEN);
        header2.setText("I am header 2.");

        TextView header3 = new TextView(this);
        header3.setTextColor(Color.BLUE);
        header3.setText("I am header 3.");

        recyclerView.addHeaderView(header1);
        recyclerView.addHeaderView(header2);
        recyclerView.addHeaderView(header3);
        TextView footer1 = new TextView(this);
        footer1.setTextColor(Color.GRAY);
        footer1.setText("I am footer 1.");

        TextView footer2 = new TextView(this);
        footer2.setTextColor(Color.GRAY);
        footer2.setText("I am footer 2.");

        TextView footer3 = new TextView(this);
        footer3.setTextColor(Color.GRAY);
        footer3.setText("I am footer 3.");

        recyclerView.addFooterView(footer1);
        recyclerView.addFooterView(footer2);
        recyclerView.addFooterView(footer3);

        List<String> data = new ArrayList<>();
        for (int j = 0; j < 50; j++) {
            data.add("I am Text " + (j + 1));
        }

        TextAdapter adapter = new TextAdapter(this, data);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setOnItemClickListener(new UltimateRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, int position, long id) {
                Log.d("mock", "onItemClick position = " + position);
            }
        });
    }
}