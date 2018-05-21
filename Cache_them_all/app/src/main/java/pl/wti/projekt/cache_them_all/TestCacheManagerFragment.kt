package pl.wti.projekt.cache_them_all


import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_test_cache_manager.*
import org.json.JSONException
import org.json.JSONObject
import pl.wti.projekt.cache_them_all.R.string.customer_key
import pl.wti.projekt.cache_them_all.caches.*

/**
 * A simple [Fragment] subclass.
 * Use the [TestCacheManagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TestCacheManagerFragment : Fragment(){

    private lateinit var caches : ArrayList<Cache>
    private lateinit var mQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view : View = inflater!!.inflate(R.layout.fragment_test_cache_manager, container, false)

        //var menagerCache : CacheManager = CacheManager(context, resources.getString(R.string.customer_key),this.context)
        //menagerCache.getCachesForLocationAndShowText(location)

        caches = ArrayList<Cache>()
        mQueue = Volley.newRequestQueue(context)
        showCaches()

        return view
    }

    fun showCaches(){
        val location : Location = Location("loc1")
        location.latitude = 54.3
        location.longitude = 22.3

        var key : String = resources.getString(R.string.customer_key)

        var url : String  = "https://opencaching.pl/okapi/services/caches/search/nearest?center=" + location.latitude + "|" + location.longitude + "&consumer_key=" + key

        val request = JsonObjectRequest(Request.Method.GET, url, null, Listener { response ->
            try {
                text_xd.setText("")
                val jsonArray = response.getJSONArray("results")
                for (i in 0 until jsonArray.length()) {

                    url = "https://opencaching.pl/okapi/services/caches/geocache?cache_code=" + jsonArray.getString(i) + "&consumer_key=" + key

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

                            text_xd.append(newCache.code+" "+newCache.name+" "+ locationToString(newCache.location)+" "+newCache.type+" "+newCache.status+"\n")
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

}// Required empty public constructor
