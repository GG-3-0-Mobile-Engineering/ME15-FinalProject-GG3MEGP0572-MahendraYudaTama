package com.example.finalprojectgigih.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectgigih.databinding.ItemDisasterReportBinding
import com.example.finalprojectgigih.model.Disaster
import com.example.finalprojectgigih.model.Geometry
import com.squareup.picasso.Picasso

class DisasterReportAdapter(private val data:List<Disaster>) :
    RecyclerView.Adapter<DisasterReportAdapter.DisasterReportViewHolder>() {
    inner class DisasterReportViewHolder(val binding: ItemDisasterReportBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Geometry>() {
        override fun areItemsTheSame(oldItem: Geometry, newItem: Geometry): Boolean {
            return oldItem.properties.disaster_type == newItem.properties.disaster_type
        }

        override fun areContentsTheSame(oldItem: Geometry, newItem: Geometry): Boolean {
            return oldItem == newItem
        }
    }
//    private val differ = AsyncListDiffer(this, diffCallback)
//    var disasterReports: List<Disaster>
//        get() = differ.currentList
//        set(value) {
//            differ.submitList(value)
//        }

    override fun getItemCount(): Int = data.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisasterReportViewHolder {
        return DisasterReportViewHolder(ItemDisasterReportBinding.inflate(
            LayoutInflater.from(parent.context), parent,false
        ))
    }

    override fun onBindViewHolder(holder: DisasterReportViewHolder, position: Int) {
        holder.binding.apply {
            val disasterReport = data[position]
            tvDisasterName.text = disasterReport.disasterType
            tvDisasterDescription.text = disasterReport.reportTime
            if (disasterReport.imageUrl!=null){
                Picasso.get().load(Uri.parse(disasterReport.imageUrl)).into(ivDisasterImage)
            }
        }
    }

}