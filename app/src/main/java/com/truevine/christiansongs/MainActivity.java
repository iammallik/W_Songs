package com.truevine.christiansongs;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;



public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {


    SectionedRecyclerViewAdapter mSectionAdapter;
    Context mContext;
    private String[] songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;


        songs = getResources().getStringArray(R.array.song_names);
        mSectionAdapter = new SectionedRecyclerViewAdapter();
        for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++){
            List<String> songList = getSongsList(alphabet);
            if(songList.size()>0){
                mSectionAdapter.addSection(new SongsSection(String.valueOf(alphabet), songList));
            }
        }

        RecyclerView recyclerView  = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mSectionAdapter);


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        // getSectionsMap requires library version 1.0.4+
        for (Section section : mSectionAdapter.getSectionsMap().values()) {
            if (section instanceof FilterableSection) {
                ((FilterableSection)section).filter(query);
            }
        }
        mSectionAdapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private class SongsSection extends StatelessSection implements FilterableSection{

        String title;
        List<String> list;
        List<String> filteredList;
        int index = 1;

        SongsSection(String title, List<String> list) {
            super(new SectionParameters.Builder(R.layout.section_item)
                .headerResourceId(R.layout.section_header)
                .build());

            this.title = title;
            this.list = list;
            this.filteredList = new ArrayList<>(list);
        }

        @Override
        public int getContentItemsTotal() {

            //return list.size();
            return filteredList.size();

        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;

            final String name = filteredList.get(position);

            itemHolder.tvItem.setText(name);
            final int myInt = Arrays.asList(songs).indexOf(name)+1;
            itemHolder.index.setText( ""+myInt);

            itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SongActivity.class);
                    intent.putExtra("song_index", myInt);
                    startActivity(intent);
                    //Toast.makeText(mContext, String.format("Clicked on position #%s of Section %s", mSectionAdapter.getPositionInSection(itemHolder.getAdapterPosition()), title), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.tvTitle.setText(title);
        }

        @Override
        public void filter(String query) {
            if (TextUtils.isEmpty(query)) {
                filteredList = new ArrayList<>(list);
                this.setVisible(true);
            }
            else {
                filteredList.clear();
                for (String value : list) {
                    int i = Arrays.asList(songs).indexOf(value)+1;
                    String value1 = ""+i;
                    if (value.toLowerCase().contains(query.toLowerCase()) || value1.toLowerCase().contains(query.toLowerCase())  ) {
                        filteredList.add(value);
                    }
                }

                this.setVisible(!filteredList.isEmpty());
            }
        }
    }

    private List<String> getSongsList(char letter){
        List<String> songs = new ArrayList<>();
        for (String name: getResources().getStringArray(R.array.song_names)){
            if(name.charAt(0) == letter) {
                songs.add(name);
            }
        }
        return songs;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;

        HeaderViewHolder(View view) {
            super(view);

            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        }
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final TextView index;
        private final TextView tvItem;

        ItemViewHolder(View view) {
            super(view);

            rootView = view.findViewById(R.id.card_view);
            index = (TextView) view.findViewById(R.id.index);
            tvItem = (TextView) view.findViewById(R.id.tvItem);
        }
    }

    interface FilterableSection {
        void filter(String query);
    }
}
