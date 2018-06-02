package pl.wti.projekt.cache_them_all

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONException
import pl.wti.projekt.cache_them_all.caches.Cache
import pl.wti.projekt.cache_them_all.caches.stringToLatLng
import pl.wti.projekt.cache_them_all.caches.stringToLocation
import com.android.volley.Response.Listener
import kotlinx.android.synthetic.main.fragment_test_dialog_box.*

/**
 * A simple [Fragment] subclass.
 */
class TestMapWithFiltrCache : Fragment(),
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener{

    lateinit var map : GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private lateinit var lastLocation: Location

    private lateinit var caches : ArrayList<Cache>
    private lateinit var mQueue: RequestQueue

    //filtr dialog
    lateinit var listCaches: Array<String>
    lateinit var listCachesShowText: Array<String>
    lateinit var checkedCaches: BooleanArray
    lateinit var mUserCaches: BooleanArray
    lateinit var tmpCaches: BooleanArray

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_test_map_with_filtr_cache, container, false)

        caches = ArrayList<Cache>()
        mQueue = Volley.newRequestQueue(context)

        listCaches = resources.getStringArray(R.array.types_of_caches)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //filtr skrzynek
        listCachesShowText = resources.getStringArray(R.array.types_of_caches_name_in_filtr)
        checkedCaches = BooleanArray(listCachesShowText.size)
        tmpCaches = BooleanArray(listCachesShowText.size)
        mUserCaches = BooleanArray(listCachesShowText.size)

        for (i in 0 until checkedCaches.size) {
            checkedCaches[i] = true
            mUserCaches[i] = true
            tmpCaches[i] = true
        }

        open_dialog.setOnClickListener(View.OnClickListener {
            val mBuilder = AlertDialog.Builder(context)
            mBuilder.setTitle(R.string.title_type_of_caches)
            mBuilder.setMultiChoiceItems(listCachesShowText, checkedCaches, DialogInterface.OnMultiChoiceClickListener { dialogInterface, position, isChecked ->
                if (isChecked) {
                    tmpCaches[position] = true
                } else {
                    tmpCaches[position] = false
                }
            })

            mBuilder.setCancelable(false)
            mBuilder.setPositiveButton(resources.getString(R.string.OK_type_of_caches), DialogInterface.OnClickListener { dialogInterface, which ->
                var item = ""
                for (i in 0 until mUserCaches.size) {
                    mUserCaches[i] = tmpCaches[i]
                    if(mUserCaches[i] == true) item+=listCachesShowText[i]
                    if (i != mUserCaches.size - 1) {
                        item += ", "
                    }
                }

                map.clear()

                for(cache in caches){
                    drawCacheMarker(cache)
                }

            })

            mBuilder.setNegativeButton(resources.getString(R.string.NO_type_of_caches), DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            /*
            mBuilder.setNegativeButton(resources.getString(R.string.ONLY_TRADITIONAL_type_of_caches), DialogInterface.OnClickListener { dialogInterface, which ->
                checkedCaches[0] = true
                tmpCaches[0] = true
                mUserCaches[0] = true
                for (i in 1 until mUserCaches.size) {
                    checkedCaches[i] = true
                    tmpCaches[i] = true
                    mUserCaches[i] = true
                }

                map.clear()
                for(cache in caches){
                    drawCacheMarker(cache)
                }
            })*/

            mBuilder.setNeutralButton(resources.getString(R.string.ALL_type_of_caches), DialogInterface.OnClickListener { dialogInterface, which ->
                for (i in 0 until mUserCaches.size) {
                    checkedCaches[i] = true
                    tmpCaches[i] = true
                    mUserCaches[i] = true
                }

                map.clear()
                for(cache in caches){
                    drawCacheMarker(cache)
                }
            })

            val mDialog = mBuilder.create()
            mDialog.show()
        })

        //mapy
        var mapFragment : SupportMapFragment = childFragmentManager.findFragmentById(R.id.map3) as SupportMapFragment

        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context)

    }

    override fun onMapReady(googleMap: GoogleMap){
        map = googleMap

        map.getUiSettings().setZoomControlsEnabled(true)
        map.setOnMarkerClickListener(this)


        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
        map.setOnCameraMoveListener(this);
        map.setOnCameraMoveCanceledListener(this);

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

                showCacheMarker(lastLocation.latitude.toString(), lastLocation.longitude.toString())
            }
            else{
                var loc = LatLng(52.39546319, 16.95475321)
                map.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(loc, 15f))
                showCacheMarker(loc.latitude.toString(), loc.longitude.toString())

            }
        }
    }

    private fun showCacheMarker(latitude:String, longitude:String){
        var key : String = resources.getString(R.string.customer_key)

        var url : String  = "https://opencaching.pl/okapi/services/caches/search/nearest?center=" + latitude + "|" + longitude + "&consumer_key=" + key

        val request = JsonObjectRequest(Request.Method.GET, url, null, Listener { response ->
            try {

                val jsonArray = response.getJSONArray("results")
                for (i in 0 until jsonArray.length()) {

                    var isOnMap : Boolean = false

                    for(cache in caches){
                        if(jsonArray.getString(i) == cache.code) isOnMap = true
                    }

                    if(!isOnMap){
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
                                if(newCache.type.equals(listCaches[0])) {
                                    if(mUserCaches[0]==true){
                                        map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.t_marker)))//tradycyjna +
                                    }
                                }else if(newCache.type.equals(listCaches[1])){
                                    if(mUserCaches[1]==true){
                                        map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.m_marker)))//multi +
                                    }
                                }else if(newCache.type.equals(listCaches[2])){
                                    if(mUserCaches[2]==true){
                                        map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.q_marker)))// quiz +
                                    }
                                }else if(newCache.type.equals(listCaches[3])){
                                    if(mUserCaches[3]==true){
                                        map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.v_marker)))// virtualna
                                    }
                                }else if(newCache.type.equals(listCaches[4])){
                                    if(mUserCaches[4]==true){
                                        map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.event_marker)))//event
                                    }
                                }else if(newCache.type.equals(listCaches[5])){
                                    if(mUserCaches[5]==true){
                                        map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.z_marker)))//other nietypowa +
                                    }
                                }else if(newCache.type.equals(listCaches[6])){
                                    if(mUserCaches[6]==true){
                                        map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.c_marker)))//webcam
                                    }
                                }else if(newCache.type.equals(listCaches[7])){
                                    if(mUserCaches[7]==true){
                                        map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.p_marker)))//mobile moving
                                    }
                                }else if(newCache.type.equals(listCaches[8])){
                                    if(mUserCaches[8]==true){
                                        map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.o_marker)))//own
                                    }
                                }else{
                                    map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))
                                }//c++

                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }, Response.ErrorListener { error -> error.printStackTrace() })
                        mQueue.add(requestCache)
                        /*
                            Abrakadabra to czary i magia
                        */
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error -> error.printStackTrace() })

        mQueue.add(request)
    }

    //override fun onMarkerClick(p0: Marker?) = false

    var codeLastClickMarker : String = ""
    var doubleBackToExitPressedOnce = false

    override fun onMarkerClick(marker: Marker):Boolean{

        for (cache in caches){
            if(cache.code == marker.snippet){

                if(!doubleBackToExitPressedOnce) {
                    codeLastClickMarker = marker.snippet
                    doubleBackToExitPressedOnce = !doubleBackToExitPressedOnce
                }
                else if(doubleBackToExitPressedOnce && codeLastClickMarker == marker.snippet){
                    //var text:String = cache.code+"\n"+cache.name+"\n"+cache.locationS+"\n"+cache.type+"\n"+cache.status
                    //Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

                    var cacheDetailsFragment = CacheDetailsFragment()

                    var bundle = Bundle()
                    bundle.putString("code",marker.snippet)
                    cacheDetailsFragment.arguments = bundle

                    var managerFragment = fragmentManager
                    managerFragment.beginTransaction()
                            .replace(R.id.relativelayout,cacheDetailsFragment,cacheDetailsFragment.tag)
                            .addToBackStack(null)
                            .commit()
                }else {
                    doubleBackToExitPressedOnce = false
                }

                return false
            }
        }

        return false
    }

    override fun onCameraMoveStarted(reason : Int) {

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            //Toast.makeText(context, "The user gestured on the map.", Toast.LENGTH_SHORT).show();
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                        .REASON_API_ANIMATION) {
            //Toast.makeText(context, "The user tapped something on the map.", Toast.LENGTH_SHORT).show();
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                        .REASON_DEVELOPER_ANIMATION) {
            //Toast.makeText(context, "The app moved the camera.", Toast.LENGTH_SHORT).show();
        }
    }

    override fun onCameraMove() {
        //Toast.makeText(context, "The camera is moving.",Toast.LENGTH_SHORT).show();
        //Log.d("camera","moving")

    }

    override fun onCameraMoveCanceled() {
        //Toast.makeText(context, "Camera movement canceled.", Toast.LENGTH_SHORT).show()

    }

    override fun onCameraIdle() {

        //var t : String = "The camera has stopped moving."+map.cameraPosition.target.latitude.toString()+"|"+map.cameraPosition.target.latitude.toString()+"."
        //Toast.makeText(context, t, Toast.LENGTH_SHORT).show()
        //Log.d("map",t)

        showCacheMarker(map.cameraPosition.target.latitude.toString(), map.cameraPosition.target.longitude.toString())

    }

    fun drawCacheMarker(newCache : Cache){
        if(newCache.type.equals(listCaches[0])) {
            if(mUserCaches[0]==true){
                map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.t_marker)))//tradycyjna +
            }
        }else if(newCache.type.equals(listCaches[1])){
            if(mUserCaches[1]==true){
                map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.m_marker)))//multi +
            }
        }else if(newCache.type.equals(listCaches[2])){
            if(mUserCaches[2]==true){
                map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.q_marker)))// quiz +
            }
        }else if(newCache.type.equals(listCaches[3])){
            if(mUserCaches[3]==true){
                map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.v_marker)))// virtualna
            }
        }else if(newCache.type.equals(listCaches[4])){
            if(mUserCaches[4]==true){
                map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.event_marker)))//event
            }
        }else if(newCache.type.equals(listCaches[5])){
            if(mUserCaches[5]==true){
                map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.z_marker)))//other nietypowa +
            }
        }else if(newCache.type.equals(listCaches[6])){
            if(mUserCaches[6]==true){
                map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.c_marker)))//webcam
            }
        }else if(newCache.type.equals(listCaches[7])){
            if(mUserCaches[7]==true){
                map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.p_marker)))//mobile moving
            }
        }else if(newCache.type.equals(listCaches[8])){
            if(mUserCaches[8]==true){
                map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.fromResource(R.drawable.o_marker)))//own
            }
        }else{
            map.addMarker(MarkerOptions().position(newCache.locationLL).title(newCache.name).snippet(newCache.code).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))
        }//c++
    }

}// Required empty public constructor
