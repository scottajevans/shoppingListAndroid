package com.stayclone.shoppinglist.controllers

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stayclone.shoppinglist.R
import com.stayclone.shoppinglist.models.ShoppingItem
import com.stayclone.shoppinglist.views.MainActivity

class ShoppingListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    fun bind(shoppingItem : ShoppingItem){
        var nameField = itemView.findViewById(R.id.field_name) as TextView
        var quantityField = itemView.findViewById(R.id.field_quantity) as TextView
        var costField = itemView.findViewById(R.id.field_cost) as TextView
        var gotField = itemView.findViewById(R.id.field_got) as CheckBox

        nameField.text = shoppingItem.name
        quantityField.text = shoppingItem.quantity.toString()
        costField.text = "£${String.format("%.2f", shoppingItem.cost)}"
        gotField.isChecked = shoppingItem.got

        //If checkbox checked/unchecked update the item in preferences to reflect.
        gotField.setOnCheckedChangeListener { buttonView, isChecked ->
            Preferences(itemView.context).checkItem(shoppingItem)
        }

        //Remove items by clicking the itemView as a whole. Display dialog.
        //On confirmation remove in preferences.
        itemView.setOnClickListener {
            AlertDialog.Builder(itemView.context as MainActivity)
                .setTitle("Remove Item")
                .setMessage("Are you sure you would like to remove ${shoppingItem.name}?")
                .setPositiveButton("CANCEL", DialogInterface.OnClickListener() { dialog: DialogInterface, i: Int ->
                    dialog.dismiss()
                })
                .setNegativeButton("REMOVE", DialogInterface.OnClickListener() { dialog : DialogInterface, i: Int ->
                    Preferences(itemView.context).removeItemFromList(shoppingItem)
                    (itemView.context as MainActivity).setList()
                    dialog.dismiss()
                })
                .create()
                .show()
        }

    }
}