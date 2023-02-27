package com.example.travelapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.mapstemplate.R
import com.example.travelapp.itineraries.Itinerary
import com.example.travelapp.itineraries.Step

class StepListAdapter(private val context: Context, dataArray: List<Step>) : ArrayAdapter<Step>(context,
    R.layout.step_list_adapter_view, dataArray) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val step = getItem(position)
        val view : View = LayoutInflater.from(context).inflate(R.layout.step_list_adapter_view, null)

        val step_number = view.findViewById<TextView>(R.id.textView_step_adapter_number)
        val title = view.findViewById<TextView>(R.id.textView_step_adapter_title)
        val address = view.findViewById<TextView>(R.id.textView_step_adapter_address)
        val price = view.findViewById<TextView>(R.id.textView_step_adapter_price)

        step_number.text = "${position+1}."
        title.text = step!!.name
        address.text = step.address
        price.text = "${step.price} €"
        return view
    }
}