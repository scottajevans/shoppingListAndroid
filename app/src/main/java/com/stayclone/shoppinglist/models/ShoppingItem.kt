package com.stayclone.shoppinglist.models

data class ShoppingItem(val id : Int, val name : String, val quantity : Int, val cost : Double, var got : Boolean) {

}