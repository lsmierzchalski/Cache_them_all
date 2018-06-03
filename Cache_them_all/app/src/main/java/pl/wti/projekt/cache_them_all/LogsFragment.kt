package pl.wti.projekt.cache_them_all


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_cache_details.*
import kotlinx.android.synthetic.main.fragment_logs.*
import org.json.JSONException
import pl.wti.projekt.cache_them_all.caches.Cache
import pl.wti.projekt.cache_them_all.caches.LogCache
import pl.wti.projekt.cache_them_all.caches.LogListViewAdapter


/**
 * A simple [Fragment] subclass.
 */
class LogsFragment : Fragment() {

    private lateinit var mQueue: RequestQueue

    private lateinit var logList : ArrayList<LogCache>;

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        logList = ArrayList<LogCache>();

        //kod skrzynki przyjdzie w intencie
        var code = "OP8B69"
        var bundle = arguments
        if(bundle != null){
            code = bundle.getString("code")
        }

        mQueue = Volley.newRequestQueue(context)
        showLogs(code)

        return inflater!!.inflate(R.layout.fragment_logs, container, false)
    }

    fun showLogs(code: String){
        val key : String = resources.getString(R.string.customer_key)

        var url = "https://opencaching.pl/okapi/services/logs/logs?cache_code=" + code + "&consumer_key=" + key

        var requestCache = JsonArrayRequest(Request.Method.GET, url, null, Listener { response ->
            try {
                for (i in 0 until response.length()) {

                    val json = response.getJSONObject(i)
                    var log = LogCache()
                    log.type = json.getString("type")
                    log.date = json.getString("date")
                    val json2 = json.getJSONObject("user")
                    log.username = json2.getString("username")
                    log.comment = json.getString("comment")

                    logList.add(log)

                }
                var adapter = LogListViewAdapter(logList, context)

                log_list.adapter = adapter

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error -> error.printStackTrace() })
        mQueue.add(requestCache)
    }

}// Required empty public constructor
