package pl.wti.projekt.cache_them_all.caches

import android.location.Location
import com.google.android.gms.maps.model.LatLng

/**
 * Created by lukis on 17.05.2018.
 */
public class Cache (_code : String){

    val code : String = _code
        get() = field
    lateinit var name : String
    lateinit var locationS : String
    lateinit var location : Location
    lateinit var locationLL : LatLng
    lateinit var type : String
    lateinit var status : String
    lateinit var url : String
    lateinit var founds : String
    lateinit var notfounds : String
    lateinit var size : String
    lateinit var difficulty : String
    lateinit var terrain : String
    lateinit var rating : String
    lateinit var rating_votes : String
    lateinit var recommendations : String
    lateinit var last_found : String
    lateinit var last_modified : String
    lateinit var date_created : String
    lateinit var date_hidden : String
    lateinit var owner_username : String
    lateinit var owner_profile_url : String
    lateinit var hint : String
    lateinit var short_description : String
    lateinit var description : String
    lateinit var images : ArrayList<CacheManager>
    lateinit var attrnames : ArrayList<String>
    lateinit var latest_logs : ArrayList<LogCache>
    lateinit var country : String
    lateinit var state : String
    lateinit var attribution_note : String

}