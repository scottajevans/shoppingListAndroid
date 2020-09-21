package com.stayclone.shoppinglist.controllers

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.stayclone.shoppinglist.R
import com.stayclone.shoppinglist.models.ShoppingItem
import com.stayclone.shoppinglist.views.MainActivity

class ShoppingListAdapter(private var dataSource: List<ShoppingItem>) : RecyclerView.Adapter<ShoppingListViewHolder>() {

    //Parent referenced required later for saving list in moveItem function.
    private lateinit var parentReference : ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        parentReference = parent
        //In this case must inflate the layout now before handing to holder.
        val inflater : LayoutInflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.list_item_shopping_list, parent, false)
        val viewHolder = ShoppingListViewHolder(itemView)
        //Handle on click of the drag bars on far right of layout. Passback a call to the startDragging function in MainActivity.
        viewHolder.itemView.findViewById<ImageView>(R.id.handle_view).setOnTouchListener { view, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                (parent.context as MainActivity).startDragging(viewHolder)
            }
            return@setOnTouchListener true
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val listItem = dataSource[position]
        holder.bind(listItem)
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    //Used when re-ordering. Move item in list then update list in preferences.
    fun moveItem(from : Int, to : Int){
        var mutableList = dataSource.toMutableList()
        var moving = mutableList[from]
        mutableList.remove(moving)
        if (to < from){
            mutableList.add(to, moving)
        } else {
            mutableList.add(to - 1, moving)
        }
        dataSource = mutableList
        Preferences(parentReference.context).saveList(dataSource)
    }
}