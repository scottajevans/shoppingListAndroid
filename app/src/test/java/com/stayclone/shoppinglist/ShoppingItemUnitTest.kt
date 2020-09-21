package com.stayclone.shoppinglist

import com.stayclone.shoppinglist.models.ShoppingItem
import org.junit.Assert
import org.junit.Test

class ShoppingItemUnitTest {

    @Test
    fun `creating valid shopping item object passes`(){
        val id = 1
        val name = "Example name string"
        val quantity = 1
        val cost = 0.0
        val got = false
        val newObj = ShoppingItem(id, name, quantity, cost, got)
        Assert.assertTrue(newObj.id == 1)
        Assert.assertTrue(newObj.name == "Example name string")
        Assert.assertTrue(newObj.quantity == 1)
        Assert.assertTrue(newObj.cost == 0.0)
        Assert.assertTrue(!newObj.got)
    }
}