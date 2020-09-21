package com.stayclone.shoppinglist.controllers

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.stayclone.shoppinglist.R
import com.stayclone.shoppinglist.models.ResultItem
import com.stayclone.shoppinglist.models.ShoppingItem
import com.stayclone.shoppinglist.views.AddActivity

class SearchListViewHolder(inflater : LayoutInflater, parent : ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(
    R.layout.list_item_search_list, parent, false)) {

    fun bind(searchItem : ResultItem){
        //First bind fields with item text
        var nameField = itemView.findViewById(R.id.field_name) as TextView
        var costField = itemView.findViewById(R.id.field_cost) as TextView

        nameField.text = searchItem.name
        costField.text = "£${String.format("%.2f", searchItem.cost)}"

        itemView.setOnClickListener {
            //Quantity max set to 30, min 0.
            var numberPicker : NumberPicker = NumberPicker(itemView.context)
            numberPicker.maxValue = 30
            numberPicker.minValue = 1
            //Display number picker in dialog asking quantity required.
            AlertDialog.Builder(itemView.context as AddActivity)
                .setView(numberPicker)
                .setTitle("Quantity")
                .setMessage("Select the quantity needed: ")
                .setPositiveButton("ADD", DialogInterface.OnClickListener() { dialog: DialogInterface, i: Int ->
                    //Create new instance of ShoppingItem and save to list.
                    var newShoppingItem : ShoppingItem = ShoppingItem(searchItem.id, searchItem.name, numberPicker.value, searchItem.cost, false)
                    var done = Preferences(itemView.context).addItemToList(newShoppingItem)
                    dialog.dismiss()
                    //If it has errored, this is due to duplicate, display error.
                    //Otherwise provide success message as visual feedback of adding to list.
                    if (!done){
                        Toast.makeText(itemView.context, "You have entered a duplicate, please check your list...", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(itemView.context, "Added ${searchItem.name} to shopping list", Toast.LENGTH_SHORT).show()
                    }
                })
                .setNegativeButton("CANCEL", DialogInterface.OnClickListener() { dialog : DialogInterface, i: Int ->
                    dialog.dismiss()
                })
                .create()
                .show()
        }
    }
}