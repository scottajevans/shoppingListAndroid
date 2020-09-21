package com.stayclone.shoppinglist.controllers

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stayclone.shoppinglist.models.ResultItem

class SearchListAdapter(private var dataSource : List<ResultItem>) : RecyclerView.Adapter<SearchListViewHolder>() {
    //Can pass inflater instead of itemView as not required at this level. Pass down to holder to inflate.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder {
        val inflater : LayoutInflater = LayoutInflater.from(parent.context)
        return SearchListViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        val listItem = dataSource[position]
        holder.bind(listItem)
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

}