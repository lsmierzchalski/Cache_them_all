package pl.wti.projekt.cache_them_all.caches

import android.location.Location
import com.google.android.gms.maps.model.LatLng

/**
 * Created by lukis on 17.05.2018.
 */
public class Cache (var _code : String){

    val code : String = _code
        get() = field
    lateinit var name : String
    lateinit var locationS : String
    lateinit var location : Location
    lateinit var locationLL : LatLng
    lateinit var type : String
    lateinit var status : String

}