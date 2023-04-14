package com.example.mapstemplate.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import java.io.File

class ImageRecyclerViewAdapter(
    private val data : List<File>
) : RecyclerView.Adapter<ImageRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_recycle_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder  {
        val inflatedView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_images_adapter, parent, false)
        return ItemViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val localfile: File = data[position]
        val bitmap: Bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
        holder.imageView.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}