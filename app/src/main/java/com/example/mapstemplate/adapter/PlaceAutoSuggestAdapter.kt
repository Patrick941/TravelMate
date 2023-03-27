package com.example.mapstemplate.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import com.example.mapstemplate.models.PlaceApi

class PlaceAutoSuggestAdapter(context: Context, resId: Int) : ArrayAdapter<String>(context, resId), Filterable {

    private var results: ArrayList<String> = ArrayList()
    private var resource: Int = resId
    private var context: Context = context
    private var placeApi: PlaceApi = PlaceApi()

    override fun getCount(): Int {
        return results.size
    }

    override fun getItem(pos: Int): String {
        return results[pos]
    }

    override fun getFilter(): Filter {
        val filter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    results = placeApi.autoComplete(constraint.toString())

                    filterResults.values = results
                    filterResults.count = results.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
        return filter
    }
}
