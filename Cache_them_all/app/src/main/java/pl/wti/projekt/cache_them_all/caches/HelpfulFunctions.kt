package pl.wti.projekt.cache_them_all.caches

import android.location.Location
import com.google.android.gms.maps.model.LatLng

/**
 * Created by lukis on 17.05.2018.
 */

public fun locationToString(location : Location): String {
    return location.latitude.toString()+"|"+location.longitude.toString()
}

public fun stringToLocation(stringLocation : String): Location {

    var slati : String = ""
    var slong : String = ""
    var next : Boolean = false
    for (item in stringLocation){
        if(item == '|') next = true
        else if(!next) slati+=item
        else if(next) slong+=item
    }

    var location : Location = Location(stringLocation)
    location.latitude = slati.toDouble()
    location.longitude = slong.toDouble()

    return location
}

public fun stringToLatLng(stringLocation:String):LatLng{
    var slati : String = ""
    var slong : String = ""
    var next : Boolean = false
    for (item in stringLocation){
        if(item == '|') next = true
        else if(!next) slati+=item
        else if(next) slong+=item
    }

    return LatLng(slati.toDouble(),slong.toDouble())
}