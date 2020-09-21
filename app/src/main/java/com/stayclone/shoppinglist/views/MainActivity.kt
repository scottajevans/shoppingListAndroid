package com.stayclone.shoppinglist.views

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.stayclone.shoppinglist.R
import com.stayclone.shoppinglist.controllers.Preferences
import com.stayclone.shoppinglist.controllers.ShoppingListAdapter
import com.stayclone.shoppinglist.models.ShoppingItem


class MainActivity : AppCompatActivity() {

    private lateinit var listView : RecyclerView
    private lateinit var shoppingList : List<ShoppingItem>
    private lateinit var listAdapter : ShoppingListAdapter

    //Defining itemTouchHelper to handle when shoppinglistitem is long pressed to be moved.
    //Also used when using the drag icon on the right.
    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : SimpleCallback(
                UP or
                        DOWN, 0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val adapter = recyclerView.adapter as ShoppingListAdapter
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    adapter.moveItem(from, to)
                    adapter.notifyItemMoved(from, to)
                    return true
                }
                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                }
                //Changes the alpha of the item to indicate being moved.
                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }
                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder?.itemView?.alpha = 1.0f
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setList()

        var fab : FloatingActionButton = findViewById(R.id.floatingActionButton)
        fab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java).apply {}
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        setList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    //Menu being defined, set the limit by displaying an alert dialog and allowing input.
    //Or create intent to share via email/messaging service.
    //Or clear the entire list by removing all items ans saving an empty list to preferences.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_set_limit -> {
                var budgetInput = EditText(this@MainActivity)
                budgetInput.inputType = InputType.TYPE_CLASS_NUMBER
                android.app.AlertDialog.Builder(this@MainActivity)
                    .setView(budgetInput)
                    .setTitle("Set Budget Limit")
                    .setMessage("What would you like to set as your new budget limit?")
                    .setPositiveButton(
                        "SET",
                        DialogInterface.OnClickListener() { dialog: DialogInterface, i: Int ->
                            Preferences(this@MainActivity).setBudgetLimit(
                                budgetInput.text.toString().toDouble()
                            )
                            setList()
                            dialog.dismiss()
                        })
                    .setNegativeButton(
                        "CANCEL",
                        DialogInterface.OnClickListener() { dialog: DialogInterface, i: Int ->
                            dialog.dismiss()
                        })
                    .create()
                    .show()
                return true
            }
            R.id.action_email -> {
                var message : String = ""
                for (item in shoppingList){
                    message += "${item.quantity} * ${item.name},\n"
                }

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Shopping List")
                intent.putExtra(Intent.EXTRA_TEXT, message)

                startActivity(Intent.createChooser(intent, "Send Email"))
                return true
            }
            R.id.action_clear_list -> {
                android.app.AlertDialog.Builder(this@MainActivity)
                    .setTitle("Clear Shopping List")
                    .setMessage("Are you sure you would like to clear the shopping list? This cannot be undone.")
                    .setPositiveButton(
                        "DELETE",
                        DialogInterface.OnClickListener() { dialog: DialogInterface, i: Int ->
                            Preferences(applicationContext).clearList()
                            setList()
                            dialog.dismiss()
                        })
                    .setNegativeButton(
                        "CANCEL",
                        DialogInterface.OnClickListener() { dialog: DialogInterface, i: Int ->
                            dialog.dismiss()
                        })
                    .create()
                    .show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //Handled here to have access to teh startDrag function. Called from adapter.
    fun startDragging(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    //setList function handles recreating the list from what is stored in preferences.
    //Also recalculates the total and displays at the bottom.
    fun setList(){
        listView = findViewById(R.id.listView)
        shoppingList = Preferences(applicationContext).getList()
        listAdapter = ShoppingListAdapter(shoppingList)
        itemTouchHelper.attachToRecyclerView(listView)

        val item = findViewById<View>(R.id.list_label) as RelativeLayout
        val child: View = layoutInflater.inflate(R.layout.list_item_shopping_list_title, null)
        item.addView(child)

        var cost = 0.0
        for (item in shoppingList){
            cost += item.cost * item.quantity
        }
        val totalCost = findViewById<TextView>(R.id.total_cost)
        totalCost.text = "£${String.format("%.2f", cost)}"

        val totalCostBar = findViewById<RelativeLayout>(R.id.total_cost_bar)
        val budgetValue = findViewById<TextView>(R.id.budget_value)
        val budgetLimit = Preferences(this@MainActivity).getBudgetLimit()
        budgetValue.text = "£${String.format("%.2f", budgetLimit)}"
        if (budgetLimit <= cost){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                totalCostBar.setBackgroundColor(getColor(R.color.warning))
            } else {
                totalCostBar.setBackgroundColor(resources.getColor(R.color.warning))
            }
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                totalCostBar.setBackgroundColor(getColor(R.color.white))
            } else {
                totalCostBar.setBackgroundColor(resources.getColor(R.color.white))
            }
        }

        listView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listAdapter
        }
        listView.addItemDecoration(
            DividerItemDecoration(
                this@MainActivity,
                LinearLayoutManager.VERTICAL
            )
        )
    }
}
