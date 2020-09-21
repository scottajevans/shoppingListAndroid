package com.stayclone.shoppinglist.controllers

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stayclone.shoppinglist.models.ShoppingItem
import java.lang.reflect.Type

//Handles local storage of all data.
class Preferences(context: Context) {

    private val PREFS_FILENAME = "com.stayclone.shoppinglist.prefs"
    private val SHOPPING_LIST = "SHOPPING_LIST"
    private val BUDGET_LIMIT = "BUDGET_LIMIT"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    //Store an update to the budget.
    fun setBudgetLimit(newLimit : Double){
        prefs.edit().putString(BUDGET_LIMIT, newLimit.toString()).apply()
    }

    //Retrieve budget. Must be cast to double from string. If no value default to 100.
    fun getBudgetLimit() : Double {
        val str = prefs.getString(BUDGET_LIMIT, null)
        return str?.toDouble() ?: 100.0
    }

    //Add item to list in preferences. Convert from string to json first.
    fun addItemToList(newItem : ShoppingItem) : Boolean{
        var list : MutableList<ShoppingItem> = mutableListOf()
        val str = prefs.getString(SHOPPING_LIST, null)
        if (str != null){
            val gson = Gson()
            val type: Type = object : TypeToken<List<ShoppingItem?>?>() {}.type
            list = gson.fromJson(str, type)
        }
        //If the list contains a duplicate of ID return an error. Else add and save.
        return if (list.map{it.id}.contains(newItem.id)){
            false
        } else {
            list.add(newItem)
            saveList(list)
            true
        }
    }

    //Remove by finding instance of object passed. Save list to update.
    fun removeItemFromList(itemToRemove : ShoppingItem){
        var mutableList: MutableList<ShoppingItem> = getList().toMutableList()
        mutableList.remove(itemToRemove)
        saveList(mutableList)
    }

    //Update the item by setting the got bool to opposite.
    fun checkItem(itemToCheck : ShoppingItem){
        var list = getList()
        for (item in list) {
            if (item.id == itemToCheck.id) {
                item.got = !item.got
            }
        }
        saveList(list)
    }

    //Apply an update by replacing the list entirely with new instance.
    fun saveList(data : List<ShoppingItem>){
        var gson = Gson()
        var jsonString = gson.toJson(data)
        prefs.edit().putString(SHOPPING_LIST, jsonString).apply()
    }

    //Clear list entirely
    fun clearList(){
        prefs.edit().putString(SHOPPING_LIST, mutableListOf<ShoppingItem>().toString()).apply()
    }

    //Retrieve list, if none and first time, create a new example list.
    fun getList() : List<ShoppingItem>{
        val str = prefs.getString(SHOPPING_LIST, null)
        return if (str == null){
            mutableListOf<ShoppingItem>()
        } else {
            val gson = Gson()
            val type: Type = object : TypeToken<List<ShoppingItem?>?>() {}.type
            gson.fromJson(str, type)
        }

    }
}