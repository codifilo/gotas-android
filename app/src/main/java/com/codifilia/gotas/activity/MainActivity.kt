package com.codifilia.gotas.activity

import android.Manifest
import android.content.Context
import android.content.res.Resources.Theme
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
import com.codifilia.gotas.fragment.MapFragment
import com.patloew.rxlocation.RxLocation
import io.vrinda.kotlinpermissions.PermissionCallBack
import io.vrinda.kotlinpermissions.PermissionsActivity

class MainActivity : PermissionsActivity() {

    var rxLocation: RxLocation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rxLocation = RxLocation(applicationContext)

        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        // Setup spinner
        val spinner = findViewById(R.id.spinner) as Spinner?
        spinner!!.adapter = MyAdapter(toolbar!!.context, resources.getStringArray(R.array.section_titles))

        val fragments = listOf(ChartFragment(), MapFragment())

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


        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
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
