package com.codifilia.gotas.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources.Theme
import android.location.Location
import android.os.Bundle
import android.support.v7.widget.ThemedSpinnerAdapter
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.codifilia.gotas.R
import com.codifilia.gotas.fragment.ChartFragment
import com.codifilia.gotas.util.lastKnownLocation
import com.codifilia.gotas.util.latitudeKey
import com.codifilia.gotas.util.longitudeKey
import com.patloew.rxlocation.RxLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import io.vrinda.kotlinpermissions.PermissionCallBack
import io.vrinda.kotlinpermissions.PermissionsActivity

class MainActivity : PermissionsActivity() {

    private var locationTextView: TextView? = null
    private var rxLocation: RxLocation? = null
    private val disposable = CompositeDisposable()
    private val locationPickerRequestCode = 1
    private val coordNotAvailable = 250.0

    private val preferences: SharedPreferences get() {
        return getPreferences(Context.MODE_PRIVATE)
    }

    val location: Subject<Location>? = BehaviorSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rxLocation = RxLocation(applicationContext)
        locationTextView = findViewById(R.id.locationTextView) as? TextView

        location?.observeOn(Schedulers.io())
                ?.flatMap { loc -> rxLocation?.geocoding()?.fromLocation(loc)?.toObservable() }
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { address ->  locationTextView?.text = address.locality }
                ?.let { disposable.add(it) }

        publishLocation()

        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        // Setup spinner
        val spinner = findViewById(R.id.spinner) as Spinner?
        spinner!!.adapter = MyAdapter(toolbar!!.context, resources.getStringArray(R.array.section_titles))

        val fragments = listOf(ChartFragment())

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // When the given dropdown item is selected, show its contents in the
                // container view.

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, fragments[position])
                        .commit()
            }

            override fun onNothingSelected(parent: AdapterView<*>) { }
        }

        requestPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, object : PermissionCallBack {
            override fun permissionGranted() {
                super.permissionGranted()
                Log.v("Location permissions", "Granted")
            }

            override fun permissionDenied() {
                super.permissionDenied()
                Log.v("Location permissions", "Denied")
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_refresh) {
            publishLocation()
            return true
        }
        else if (id == R.id.action_select_location) {
            val intent = Intent(this, LocationPickerActivity::class.java)
            startActivityForResult(intent, locationPickerRequestCode)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if  (requestCode == locationPickerRequestCode) {
            when (resultCode) {
                LocationPickerActivity.resultGpsCode -> selectedLocation = null
                LocationPickerActivity.resultLatLngCode -> {
                    val lat = data?.getDoubleExtra(latitudeKey, coordNotAvailable) ?: coordNotAvailable
                    val lng = data?.getDoubleExtra(longitudeKey, coordNotAvailable) ?: coordNotAvailable
                    selectedLocation = newLocation(lat, lng, "LocationPicker")
                }
            }
            publishLocation()
        }
    }

    private fun newLocation(lat: Double, lng: Double, provider: String): Location? {
        if (lat != coordNotAvailable && lng != coordNotAvailable) {
            val l = Location(provider)
            l.latitude = lat
            l.longitude = lng
            return l
        } else {
            return null
        }
    }

    private var selectedLocation: Location?
        get() {
            val lat = preferences.getFloat(latitudeKey, coordNotAvailable.toFloat())
            val lng= preferences.getFloat(longitudeKey, coordNotAvailable.toFloat())
            return newLocation(lat.toDouble(), lng.toDouble(), "Preferences")
        }
        set(location) {
            val editor = preferences.edit()
            editor.putFloat(latitudeKey, location?.latitude?.toFloat() ?: coordNotAvailable.toFloat())
            editor.putFloat(longitudeKey, location?.longitude?.toFloat() ?: coordNotAvailable.toFloat())
            editor.commit()
        }

    private fun publishLocation() {
        if (selectedLocation != null) {
            location?.onNext(selectedLocation)
        }
        else if (lastKnownLocation != null) {
            location?.onNext(lastKnownLocation)
        }
    }


    private class MyAdapter(context: Context, objects: Array<String>) :
            ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, objects), ThemedSpinnerAdapter {
        private val mDropDownHelper: ThemedSpinnerAdapter.Helper

        init {
            mDropDownHelper = ThemedSpinnerAdapter.Helper(context)
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                val inflater = mDropDownHelper.dropDownViewInflater
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            } else {
                view = convertView
            }

            val textView = view.findViewById(android.R.id.text1) as TextView
            textView.text = getItem(position)

            return view
        }

        override fun getDropDownViewTheme(): Theme? {
            return mDropDownHelper.dropDownViewTheme
        }

        override fun setDropDownViewTheme(theme: Theme?) {
            mDropDownHelper.dropDownViewTheme = theme
        }
    }
}
