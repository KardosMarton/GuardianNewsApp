package com.example.android.guardiannewsapp;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;



public class NewsAdapter extends ArrayAdapter<News>{


    public NewsAdapter(@NonNull Context context, ArrayList<News> newses) {
        super(context, 0, newses);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        final News currentNews = getItem(position);

        TextView newsTitle = (TextView) listItemView.findViewById(R.id.txt_title);
        newsTitle.setText(currentNews.getWebTitle());

        TextView author = (TextView) listItemView.findViewById(R.id.txt_author);
        author.setText(currentNews.getAuthorsName());

        TextView publishedDate = (TextView) listItemView.findViewById(R.id.txt_webPublishedDte);
        publishedDate.setText(currentNews.getWebPublicationDate());

        TextView sectionId = (TextView) listItemView.findViewById(R.id.txt_sectionId);
        sectionId.setText(currentNews.getSectionName());

        return listItemView;
    }
}
