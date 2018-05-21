package pl.wti.projekt.cache_them_all.caches

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_test_cache_manager.*
import org.json.JSONException
import org.w3c.dom.Text
import pl.wti.projekt.cache_them_all.R
import pl.wti.projekt.cache_them_all.R.id.text
import pl.wti.projekt.cache_them_all.R.id.text_xd

/**
 * Created by lukis on 17.05.2018.
 */
public class CacheManager {

    var caches : ArrayList<Cache>
    var mQueue: RequestQueue
    val customer_key : String

    constructor(_context: Context, customer_key: String){
        mQueue = Volley.newRequestQueue(_context);
        this.customer_key = customer_key
        caches = ArrayList<Cache>()
    }

    fun getCachesForLocationAndShowText(location : Location){

        var url : String  = "https://opencaching.pl/okapi/services/caches/search/nearest?center=" + location.latitude + "|" + location.longitude + "&consumer_key=" + customer_key

        var result : String = ""

        val request = JsonObjectRequest(Request.Method.GET, url, null, Listener { response ->
            try {
                val jsonArray = response.getJSONArray("results")
                for (i in 0 until jsonArray.length()) {

                    url = "https://opencaching.pl/okapi/services/caches/geocache?cache_code=" + jsonArray.getString(i) + "&consumer_key=" + customer_key

                    //Log.d("->",url);

                    val requestCache = JsonObjectRequest(Request.Method.GET, url, null, Listener { response ->
                        try {
                            var newCache: Cache = Cache(jsonArray.getString(i))
                            newCache.name = response.getString("name").toString()
                            newCache.location = stringToLocation(response.getString("location"))
                            newCache.type = response.getString("type")
                            newCache.status = response.getString("status")
                            caches.add(newCache)

                            Log.d("->", locationToString(newCache.location));

                            //tv.append(newCache.code+" "+newCache.name+" "+ locationToString(newCache.location)+" "+newCache.type+" "+newCache.status+"\n")
                            //tv.append(newCache.code+" "+newCache.name+" "+ locationToString(newCache.location)+" "+newCache.type+" "+newCache.status+"\n")

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

}