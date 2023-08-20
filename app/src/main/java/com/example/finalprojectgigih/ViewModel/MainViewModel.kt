package com.example.finalprojectgigih.ViewModel

import android.content.ContentValues
import android.util.Log
import androidx.core.view.isInvisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.finalprojectgigih.MainActivity
import com.example.finalprojectgigih.adapter.DisasterReportAdapter
import com.example.finalprojectgigih.api.RetrofitInstance
import com.example.finalprojectgigih.model.Disaster
import com.example.finalprojectgigih.model.DisasterData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel:ViewModel() {
    private val _disasterList = MutableLiveData<ArrayList<Disaster>>()
    val disasterList: LiveData<ArrayList<Disaster>> = _disasterList
    private val _stateLoading = MutableLiveData<Boolean>()
    val stateLoading: LiveData<Boolean> = _stateLoading
    private val _filteredListDisaster = MutableLiveData<ArrayList<Disaster>>()
    val filteredListDisaster: LiveData<ArrayList<Disaster>> = _filteredListDisaster

    fun getData() {
        _stateLoading.value = true
        val localDisasterList = ArrayList<Disaster>()
        val timePeriod: Long = 604800
        val report = RetrofitInstance.api.getDisasterReport(timePeriod)
        report.enqueue(object : Callback<DisasterData?> {
            override fun onResponse(call: Call<DisasterData?>, response: Response<DisasterData?>) {
                if (response.isSuccessful) {
                    val geometry = response.body()?.result?.objects?.output?.geometries
                    if (geometry != null) {
                        for (geo in geometry) {
                            localDisasterList.add(
                                Disaster(
                                    geo.properties.disaster_type,
                                    geo.properties.image_url,
                                    geo.properties.created_at,
                                    geo.coordinates,
                                    geo.properties.tags.instance_region_code
                                )
                            )
                        }

                        _disasterList.value = localDisasterList
                        _stateLoading.value = false
                    }

                }
            }

            override fun onFailure(call: Call<DisasterData?>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: database faliure")
                _stateLoading.value = false
            }
        })
    }


//    fun getFilteredData(region: String, filter: String): ArrayList<Disaster> {
//        val localDisasterList = ArrayList<Disaster>()
//        if (!region.equals("all")) {
//            if (filter.equals(null)) {
//                getData()
//            } else {
//                disasterList.observe(MainActivity(),observerDisasterList)
//            }
//        } else {
//            if (filter.equals(null)) {
//                getRegionDisaster
//            } else {
//                getRegionDisasterFiltered
//            }
//        }
//        return localDisasterList
//    }
//    private val observerDisasterList = Observer<ArrayList<Disaster>> {
//        for (disaster in it) {
//            if (disaster.disasterType.equals(filter)) {
//                localDisasterList.add(disaster)
//            }
//        }
//    }
}