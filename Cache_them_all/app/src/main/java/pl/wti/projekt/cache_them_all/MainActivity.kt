package pl.wti.projekt.cache_them_all

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils.replace
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import pl.wti.projekt.cache_them_all.R.id.drawer_layout

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        displayScreen(R.id.test_map_with_filtr)

        val str : String = resources.getString(R.string.customer_key)
        //val intent = Intent(baseContext, MapsActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun  displayScreen (id: Int){
        val fragment = when (id){

            R.id.first_fragment -> {
                FirstFragment()
            }

            R.id.second_fragment -> {
                SecondFragment()
            }

            R.id.parse_json_fragment -> {
                ParseJsonUsingVolleyFragment()
            }

            R.id.parse_json_nearset_fragment ->{
                ParseJsonNearsetJavaFragment()
            }

            R.id.test_map_fragment ->{
                MapsFragment()
            }

            R.id.test_cache_manager ->{
                TestCacheManagerFragment()
            }

            R.id.test_map_with_cache ->{
                TestMapsWithNearseCacheFragment()
            }

            R.id.test_dialog_box ->{
                TestDialogBoxFragment()
            }

            R.id.test_map_with_filtr ->{
                TestMapWithFiltrCache()
            }

            else -> {
                FirstFragment()
            }
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.relativelayout, fragment)
                .commit()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        displayScreen(item.itemId)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
