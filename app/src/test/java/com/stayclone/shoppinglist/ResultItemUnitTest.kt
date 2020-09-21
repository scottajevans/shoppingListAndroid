package com.stayclone.shoppinglist

import com.stayclone.shoppinglist.models.ResultItem
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultItemUnitTest {

    @Test
    fun `creating valid result object passes`(){
        val id = 1
        val name = "Example name string"
        val cost = 0.0
        val newObj = ResultItem(id, name, cost)
        assertTrue(newObj.id == 1)
        assertTrue(newObj.name == "Example name string")
        assertTrue(newObj.cost == 0.0)
    }
}