package com.stayclone.shoppinglist

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.stayclone.shoppinglist.controllers.Preferences
import com.stayclone.shoppinglist.models.ShoppingItem
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferencesInstrumentedTest {

    @Test
    fun `getBugetReturnDouble`() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences(appContext).setBudgetLimit(100.0)
        val result = Preferences(appContext).getBudgetLimit()
        assertEquals(100.0, result)
        assertTrue(result is Double)
    }

    @Test
    fun `getListReturnsExpected`() {
        val default = listOf(ShoppingItem(0,"These are example", 1, 1.50, false), ShoppingItem(1, "items, please add your",1,1.09,false), ShoppingItem(2,"own for full functionality", 3, 0.89, false))
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences(appContext).saveList(default)
        val result = Preferences(appContext).getList()
        assertEquals(default, result)

        Preferences(appContext).clearList()
    }

    @Test
    fun `addItemToListAddsCorrectly`() {
        val default = mutableListOf<ShoppingItem>(ShoppingItem(0,"These are example", 1, 1.50, false), ShoppingItem(1, "items, please add your",1,1.09,false))
        val extraItem =  ShoppingItem(2,"own for full functionality", 3, 0.89, false)
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences(appContext).saveList(default)

        val result = Preferences(appContext).getList()
        assertTrue(result.size == 2)
        assertTrue(!result.contains(extraItem))

        val passed = Preferences(appContext).addItemToList(extraItem)
        assert(passed)
        val newList = Preferences(appContext).getList()
        assertTrue(newList.size == 3)
        assertTrue(newList.contains(extraItem))

        Preferences(appContext).clearList()

    }

    @Test
    fun `addItemFailsWhenAddingDuplicateId`() {
        val default = mutableListOf<ShoppingItem>(ShoppingItem(0,"These are example", 1, 1.50, false), ShoppingItem(1, "items, please add your",1,1.09,false))
        val extraItem =  ShoppingItem(1,"own for full functionality", 3, 0.89, false)
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences(appContext).saveList(default)

        val result = Preferences(appContext).getList()
        assertTrue(result.size == 2)
        assertTrue(!result.contains(extraItem))

        val passed = Preferences(appContext).addItemToList(extraItem)
        assert(!passed)
        val newList = Preferences(appContext).getList()
        assertTrue(newList.size == 2)
        assertTrue(!newList.contains(extraItem))

        Preferences(appContext).clearList()
    }

    @Test
    fun `removeItemFromListRemovesCorrectly`() {
        val default = mutableListOf<ShoppingItem>(ShoppingItem(0,"These are example", 1, 1.50, false), ShoppingItem(1, "items, please add your",1,1.09,false))
        val extraItem =  ShoppingItem(2,"own for full functionality", 3, 0.89, false)
        default.add(extraItem)
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences(appContext).saveList(default)

        assertTrue(default.size == 3)
        Preferences(appContext).removeItemFromList(extraItem)

        val newList = Preferences(appContext).getList()
        assertTrue(newList.size == 2)
        assertTrue(!newList.contains(extraItem))

        Preferences(appContext).clearList()
    }

    @Test
    fun `checkingItemSetsCorrectly`() {
        val default = mutableListOf<ShoppingItem>(ShoppingItem(0,"These are example", 1, 1.50, false), ShoppingItem(1, "items, please add your",1,1.09,false))
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences(appContext).saveList(default)

        val result = Preferences(appContext).getList()
        assertTrue(!result[0].got)

        Preferences(appContext).checkItem(result[0])

        val newList = Preferences(appContext).getList()
        assertTrue(newList[0].got)

        Preferences(appContext).clearList()
    }


}