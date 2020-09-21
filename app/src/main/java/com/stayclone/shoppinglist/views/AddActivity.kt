package com.stayclone.shoppinglist.views

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stayclone.shoppinglist.R
import com.stayclone.shoppinglist.controllers.SearchListAdapter
import com.stayclone.shoppinglist.models.ResultItem
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class AddActivity : AppCompatActivity() {

    private lateinit var loadingProgressBar : ProgressBar
    private lateinit var resultList : RecyclerView
    private lateinit var searchText : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        var searchButton = findViewById<ImageButton>(R.id.search_button)
        searchText = findViewById<EditText>(R.id.search_text_input)
        loadingProgressBar = findViewById<ProgressBar>(R.id.loading_progress_bar)
        resultList = findViewById(R.id.result_list)

        searchButton.setOnClickListener {
            search()
        }

        searchText.setOnEditorActionListener { _, action, _ ->
            var handled = false
            if (action == EditorInfo.IME_ACTION_DONE) {
                val inputManager: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.SHOW_FORCED)
                search()
                handled = true
            }
            handled
        }

    }

    //Display progress bar, make sure results list is hidden.
    private fun search(){
        resultList.visibility = View.INVISIBLE
        loadingProgressBar.visibility = View.VISIBLE
        run(searchText.text.toString())
    }

    //Handles displaying the results in the recyclerList
    fun displayResults(resultString : String?){
        val jsonResult = JSONObject(resultString)
        try {
            //First break down the JSONObject response to get results array.
            val uk = jsonResult.getJSONObject("uk")
            val ghs = uk.getJSONObject("ghs")
            val products = ghs.getJSONObject("products")
            val results = products.getJSONArray("results")
            val productList : MutableList<ResultItem> = mutableListOf<ResultItem>()

            //Once we have the array, loop and add each item to the results list.
            for (i in 0 until results.length()){
                var product = results.getJSONObject(i)
                productList.add(i, ResultItem(product["id"] as Int, product["name"].toString(), product["price"] as Double))
            }

            //We are done with progress bar, hide and display the list using adapter.
            loadingProgressBar.visibility = View.INVISIBLE
            val resultAdapter = SearchListAdapter(productList)
            //Must be run on UI thread to implement changes to UI objects.
            this@AddActivity.runOnUiThread(java.lang.Runnable {
                resultList.apply {
                    layoutManager = LinearLayoutManager(this@AddActivity)
                    adapter = resultAdapter
                }
                resultList.addItemDecoration(DividerItemDecoration(this@AddActivity, LinearLayoutManager.VERTICAL))
                resultList.visibility = View.VISIBLE

            })
        } catch (e : Exception) {
            //If cannot access results, must be problem with UI call. Most likely got to this stage because of problem in JSON.
            //Call error for connection.
            loadingProgressBar.visibility = View.INVISIBLE
            displayError(applicationContext)
        }
    }

    //Use OKHTTP3 and call tesco dev api.
    private fun run(query: String) {
        val client = OkHttpClient()
        val url = "${applicationContext.resources.getString(R.string.apiurl)}${query}${applicationContext.resources.getString(R.string.apiurl_post_query)}${applicationContext.resources.getString(R.string.apiurl_subscription_key)}"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            //On failure connection error display message.
            override fun onFailure(call: Call, e: IOException) {
                loadingProgressBar.visibility = View.INVISIBLE
                displayError(applicationContext)
            }
            //On success start display.
            override fun onResponse(call: Call, response: Response) { displayResults(response.body()?.string()) }
        })
    }

    private fun displayError(context : Context){
        this@AddActivity.runOnUiThread {
            Toast.makeText(context, "An error occured, please check connection and retry.", Toast.LENGTH_LONG).show()
        }
    }
}