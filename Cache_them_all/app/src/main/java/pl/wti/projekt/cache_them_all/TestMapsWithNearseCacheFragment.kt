package pl.wti.projekt.cache_them_all


import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.content.Intent
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.fragment_test_cache_manager.*
import org.json.JSONException
import pl.wti.projekt.cache_them_all.caches.Cache
import pl.wti.projekt.cache_them_all.caches.locationToString
import pl.wti.projekt.cache_them_all.caches.stringToLocation
import com.android.volley.Response.Listener
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.MarkerOptions
import pl.wti.projekt.cache_them_all.caches.stringToLatLng


/**
 * A simple [Fragment] subclass.
 */
class TestMapsWithNearseCacheFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    lateinit var map : GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private lateinit var lastLocation: Location

    private lateinit var caches : ArrayList<Cache>
    private lateinit var mQueue: RequestQueue

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_test_maps_with_nearse_cache, container, false)

        caches = ArrayList<Cache>()
        mQueue = Volley.newRequestQueue(context)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var mapFragment : SupportMapFragment = childFragmentManager.findFragmentById(R.id.map2) as SupportMapFragment

        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context)
    }

    override fun onMapReady(googleMap: GoogleMap){
        map = googleMap

        map.getUiSettings().setZoomControlsEnabled(true)
        map.setOnMarkerClickListener(this)

        setUpMap()

    }

    //lokalizacja urzytkownika
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this.context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // 1
        map.isMyLocationEnabled = true

        // 2
        fusedLocationClient.lastLocation.addOnSuccessListener(this.activity) { location ->
            // Got last known location. In some rare situations this can be null.
            // 3 - ładnie wygląda kiedy ładuje sięmapa z animacją na obecną lokalizację GPS
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))

                showCacheMarker()
            }
        }
    }

    private fun showCacheMarker(){
        var key : String = resources.getString(R.string.customer_key)

        var url : String  = "https://opencaching.pl/okapi/services/caches/search/nearest?center=" + lastLocation.latitude + "|" + lastLocation.longitude + "&consumer_key=" + key

        val request = JsonObjectRequest(Request.Method.GET, url, null, Listener { response ->
            try {

                val jsonArray = response.getJSONArray("results")
                for (i in 0 until jsonArray.length()) {

                    url = "https://opencaching.pl/okapi/services/caches/geocache?cache_code=" + jsonArray.getString(i) + "&consumer_key=" + key

                    //Log.d("->",url);

                    val requestCache = JsonObjectRequest(Request.Method.GET, url, null, Listener { response ->
                        try {
                            var newCache: Cache = Cache(jsonArray.getString(i))
                            newCache.name = response.getString("name").toString()
                            newCache.locationS = response.getString("location").toString()
                            newCache.location = stringToLocation(response.getString("location"))
                            newCache.locationLL = stringToLatLng(response.getString("location"))
                            newCache.type = response.getString("type")
                            newCache.status = response.getString("status")
                            caches.add(newCache)

                            //Log.d("->", locationToString(newCache.location));
                            map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code))

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { error -> error.printStackTrace() })
                    mQueue.add(requestCache)
                    /*
                        Abrakadabra to czary i magia
                    */
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error -> error.printStackTrace() })

        mQueue.add(request)
    }

    //override fun onMarkerClick(p0: Marker?) = false

    override fun onMarkerClick(marker: Marker):Boolean{

        for (cache in caches){
            if(cache.code == marker.snippet){
                var text:String = cache.code+"\n"+cache.name+"\n"+cache.locationS+"\n"+cache.type+"\n"+cache.status
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                return false
            }
        }

        return false
    }
/*
    public fun chkGPSorNetworkEnabled(){
        var locationManager : LocationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        var isGPSEnable = locationManager.isProviderEnabled(locationManager)
        var isNetworkEnable = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)

        var gpsMsg : String = "Please Enable your GPS/Location Service"
        var netMsg : String = "Please Enable your Network Service"

        if(!isGPSEnable){
            var dialog : AlertDialog.Builder = AlertDialog.Builder(context)
            dialog.setMessage(gpsMsg)

            dialog.setPositiveButton("GPS Setting", DialogInterface.OnClickListener(){

            });
        }

    }
*/
}// Required empty public constructor
