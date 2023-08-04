package com.example.finalprojectgigih

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectgigih.RetrofitInstance.api
import com.example.finalprojectgigih.databinding.ActivityMainBinding
import com.example.finalprojectgigih.databinding.ActivityMainBinding.inflate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mMap: GoogleMap
    private lateinit var disasterReportAdaper: DisasterReportAdapter

    private lateinit var listDisaster: ArrayList<Disaster>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val sharedPreferences = getSharedPreferences("nightMode", Context.MODE_PRIVATE)
        val savedNightMode = sharedPreferences.getBoolean("nightModeState",false)
        listDisaster = ArrayList()
        setupSearchView()
        initiateBottomSheet()
        setFilterButton()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_container_view) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.rvDisaster.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.fabSetting.setOnClickListener { view ->
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val jakarta = LatLng(-6.206872814696454, 106.8336342105742)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(jakarta))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10f))
        mMap.uiSettings.setZoomControlsEnabled(true)
        getData()
    }

    fun putDisasterOnMap(listDisaster: ArrayList<Disaster>) {
        mMap.clear()
        for (disaster in listDisaster) {
            addDisasterPoint(disaster.coordinate, disaster.disasterType)
        }
    }

    fun addDisasterPoint(coor: List<Double>, disasterType: String) {
        if (this::mMap.isInitialized) {
            val coordinate = LatLng(coor.get(1), coor.get(0))
            mMap.addMarker(
                MarkerOptions()
                    .position(coordinate)
                    .title(disasterType)
            )
        }
    }

    fun setupSearchView() {
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.item_label)
        val cursorAdapter = SimpleCursorAdapter(
            this,
            R.layout.item_label,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
        val suggestions = listOf(
            Pair("Semua", "all"),
            Pair("Aceh", "ID-AC"),
            Pair("Bali", "ID-BA"),
            Pair("Kep Bangka Belitung", "ID-BB"),
            Pair("Banten", "ID-BT"),
            Pair("Bengkulu", "ID-BE"),
            Pair("Jawa Tengah", "ID-JT"),
            Pair("Kalimantan Tengah", "ID-KT"),
            Pair("Sulawesi Tengah", "ID-ST"),
            Pair("Jawa Timur", "ID-JI"),
            Pair("Kalimantan Timur", "ID-KI"),
            Pair("Nusa Tenggara Timur", "ID-NT"),
            Pair("Gorontalo", "ID-GO"),
            Pair("DKI Jakarta", "ID-JK"),
            Pair("Jambi", "ID-JA"),
            Pair("Lampung", "ID-LA"),
            Pair("Maluku", "ID-MA"),
            Pair("Kalimantan Utara", "ID-KU"),
            Pair("Maluku Utara", "ID-MU"),
            Pair("Sulawesi Utara", "ID-SA"),
            Pair("Sumatera Utara", "ID-SU"),
            Pair("Papua", "ID-PA"),
            Pair("Riau", "ID-RI"),
            Pair("Kepulauan Riau", "ID-KR"),
            Pair("Sulawesi Tenggara", "ID-SG"),
            Pair("Kalimantan Selatan", "ID-KS"),
            Pair("Sulawesi Selatan", "ID-SN"),
            Pair("Sumatera Selatan", "ID-SS"),
            Pair("DI Yogyakarta", "ID-YO"),
            Pair("Jawa Barat", "ID-JB"),
            Pair("Kalimantan Barat", "ID-KB"),
            Pair("Nusa Tenggara Barat", "ID-NB"),
            Pair("Papua Barat", "ID-PB"),
            Pair("Sulawesi Barat", "ID-SR"),
            Pair("Sumatera Barat", "ID-SB"),
        )
        binding.searchView.suggestionsAdapter = cursorAdapter
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.progressBar.isVisible = true
                val selectedArea = suggestions.find {
                    it.first.lowercase().equals(query?.lowercase())
                }
                if (query?.lowercase().equals("semua") || query.equals("")) {
                    putDisasterOnMap(listDisaster)
                    binding.rvDisaster.adapter = DisasterReportAdapter(listDisaster)
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(6f))
                    binding.progressBar.isInvisible = true
                } else if (selectedArea != null) {
                    val disasterListFiltered: ArrayList<Disaster> = ArrayList()
                    for (disaster in listDisaster) {
                        if (disaster.instance_region_code.equals(selectedArea?.second)) {
                            disasterListFiltered.add(disaster)
                        }
                    }
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLng(
                            LatLng(
                                disasterListFiltered.get(0).coordinate.get(
                                    1
                                ), disasterListFiltered.get(0).coordinate.get(0)
                            )
                        )
                    )
                    putDisasterOnMap(disasterListFiltered)
                    binding.rvDisaster.adapter = DisasterReportAdapter(disasterListFiltered)
                    binding.progressBar.isInvisible = true
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val cursor = MatrixCursor(
                    arrayOf(
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_TEXT_2
                    )
                )
                newText?.let {
                    suggestions.forEachIndexed { index, suggestion ->
                        if (suggestion.first.contains(newText, true))
                            cursor.addRow(arrayOf(index, suggestion.first, suggestion.second))
                    }
                }

                cursorAdapter.changeCursor(cursor)
                return true
            }
        })
        binding.searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            @SuppressLint("Range")
            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = binding.searchView.suggestionsAdapter.getItem(position) as Cursor
                val selectedData =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                binding.searchView.setQuery(selectedData, false)
                return true
            }
        })

    }

    fun getData() {
        val timePeriod: Long = 604800
        val report = api.getDisasterReport(timePeriod)
        report.enqueue(object : Callback<DisasterData?> {
            override fun onResponse(call: Call<DisasterData?>, response: Response<DisasterData?>) {
                if (response.isSuccessful) {
                    val geometry = response.body()?.result?.objects?.output?.geometries
                    if (geometry != null) {
                        for (geo in geometry) {
                            listDisaster.add(
                                Disaster(
                                    geo.properties.disaster_type,
                                    geo.properties.image_url,
                                    geo.properties.created_at,
                                    geo.coordinates,
                                    geo.properties.tags.instance_region_code
                                )
                            )
                            Log.d("testData", "onResponse: " + geo.coordinates)
                        }

                        putDisasterOnMap(listDisaster)
                        binding.rvDisaster.adapter = DisasterReportAdapter(listDisaster)
                    }

                }
                binding.progressBar.isInvisible = true
            }

            override fun onFailure(call: Call<DisasterData?>, t: Throwable) {
                Log.d(TAG, "onFailure: database faliure")
            }
        })
    }

    fun initiateBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)
        bottomSheetBehavior.apply {
            peekHeight = 100
            this.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val upperState = 0.66
                val lowerState = 0.33
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_SETTLING) {
                    if (slideOffset >= upperState) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    if (slideOffset > lowerState && slideOffset < upperState) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    }
                    if (slideOffset <= lowerState) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            }
        })
    }

    fun getFilteredbyDisasterData(disasterType: String) {
        val disasterListFiltered: ArrayList<Disaster> = ArrayList()
        for (disaster in listDisaster) {
            if (disaster.disasterType.equals(disasterType)) {
                disasterListFiltered.add(disaster)
            }
        }
        mMap.moveCamera(
            CameraUpdateFactory.newLatLng(
                LatLng(
                    disasterListFiltered.get(0).coordinate.get(
                        1
                    ), disasterListFiltered.get(0).coordinate.get(0)
                )
            )
        )
        putDisasterOnMap(disasterListFiltered)
        binding.rvDisaster.adapter = DisasterReportAdapter(disasterListFiltered)
        binding.progressBar.isInvisible = true
    }

    fun setFilterButton() {
        binding.btnSearchFlood.setOnClickListener {
            val filterOn = onCustomRadioButtonClick(it)
            if (filterOn) {
                getFilteredbyDisasterData("flood")
            } else {
                getData()
            }
        }
        binding.btnSearchHaze.setOnClickListener {
            val filterOn = onCustomRadioButtonClick(it)
            if (filterOn) {
                getFilteredbyDisasterData("haze")
            } else {
                getData()
            }
        }
        binding.btnSearchWind.setOnClickListener {
            val filterOn = onCustomRadioButtonClick(it)
            if (filterOn) {
                getFilteredbyDisasterData("wind")
            } else {
                getData()
            }
        }
        binding.btnSearchEarthquake.setOnClickListener {
            val filterOn = onCustomRadioButtonClick(it)
            if (filterOn) {
                getFilteredbyDisasterData("earthquake")
            } else {
                getData()
            }
        }
        binding.btnSearchFire.setOnClickListener {
            val filterOn = onCustomRadioButtonClick(it)
            if (filterOn) {
                getFilteredbyDisasterData("fire")
            } else {
                getData()
            }
        }
        binding.btnSearchVolcano.setOnClickListener {
            val filterOn = onCustomRadioButtonClick(it)
            if (filterOn) {
                getFilteredbyDisasterData("volcano")
            } else {
                getData()
            }
        }
    }

    fun onCustomRadioButtonClick(view: View): Boolean {
        val isAlreadySelected = view.isSelected

        for (i in 0 until binding.filterList.childCount) {
            val child = binding.filterList.getChildAt(i)
            child.background = getDrawable(R.drawable.rounded_filter_default)
            child.isSelected = false
        }

        if (!isAlreadySelected) {
            view.isSelected = true
            view.background = getDrawable(R.drawable.rounded_filter_green)
        } else {
            view.isSelected = false
        }

        return view.isSelected
    }



}