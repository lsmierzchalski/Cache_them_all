package pl.wti.projekt.cache_them_all


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_cache_details.*
import org.json.JSONException
import android.text.Spannable
import pl.wti.projekt.cache_them_all.R.id.textView
import android.text.Spanned
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray
import org.json.JSONObject
import pl.wti.projekt.cache_them_all.caches.*
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_test_dialog_box.*
import pl.wti.projekt.cache_them_all.R.string.code


/**
 * A simple [Fragment] subclass.
 */
class CacheDetailsFragment : Fragment() {

    private lateinit var cache : Cache
    private lateinit var mQueue: RequestQueue

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_cache_details, container, false)

        //kod skrzynki przyjdzie w intencie
        var code = "OP8B69"
        var bundle = arguments
        if(bundle != null){
            code = bundle.getString("code")
        }

        cache = Cache(code)
        mQueue = Volley.newRequestQueue(context)
        showCache(code)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_logs.setOnClickListener(View.OnClickListener {

            var logsFragment = LogsFragment()

            var bundle = Bundle()
            bundle.putString("code",cache.code)
            logsFragment.arguments = bundle

            var managerFragment = fragmentManager
            managerFragment.beginTransaction()
                    .replace(R.id.relativelayout,logsFragment,logsFragment.tag)
                    .addToBackStack(null)
                    .commit()

        })

    }

        fun showCache(code : String){
        val key : String = resources.getString(R.string.customer_key)

        var url = "https://opencaching.pl/okapi/services/caches/geocache?cache_code=" + code + "&consumer_key=" + key + "&fields=code|name|location|type|status|needs_maintenance|url|owner|gc_code|founds|notfounds|willattends|size2|oxsize|difficulty|terrain|trip_time|trip_distance|rating|rating_votes|recommendations|req_passwd|short_description|short_descriptions|description|hints2|images|preview_image|attr_acodes|attrnames|attribution_note|latest_logs|trackables_count|trackables|alt_wpts|country|state|protection_areas|last_found|last_modified|date_created|date_hidden|internal_id"

        var requestCache = JsonObjectRequest(Request.Method.GET, url, null, Listener { response ->
            try {
                cache.name = response.getString("name").toString()
                cache.locationS = response.getString("location")
                cache.location = stringToLocation(response.getString("location"))
                cache.type = response.getString("type")
                cache.status = response.getString("status")
                cache.url = response.getString("url")
                cache.founds = response.getString("founds")
                cache.notfounds = response.getString("notfounds")
                cache.size = response.getString("size2")
                cache.difficulty = response.getString("difficulty")
                cache.terrain = response.getString("terrain")
                cache.rating = response.getString("rating")
                cache.rating_votes = response.getString("rating_votes")
                cache.recommendations = response.getString("recommendations")
                cache.last_found = response.getString("last_found")
                cache.last_modified = response.getString("last_modified")
                cache.date_created = response.getString("date_created")
                cache.date_hidden = response.getString("date_hidden")

                val json_owner = response.getJSONObject("owner")
                cache.owner_username = json_owner.getString("username")
                cache.owner_profile_url = json_owner.getString("profile_url")

                cache.short_description = response.getString("short_description")
                cache.description = response.getString("description")


                tv_name.setText(cache.name)
                tv_code.setText(cache.code)
                tv_location.setText(cache.locationS)
                tv_type.setText(cache.type)
                tv_status.setText(cache.status)
                tv_url.setText(cache.url)
                tv_founds.setText(cache.founds)
                tv_notfounds.setText(cache.notfounds)
                tv_size.setText(cache.size)
                tv_difficulty.setText(cache.difficulty)
                tv_terrain.setText(cache.terrain)
                tv_rating.setText(cache.rating)
                if(tryParseFloat(cache.rating)) ratingBar.setRating(cache.rating.toFloat())
                tv_rating_votes.setText(cache.rating_votes)
                tv_recommendations.setText(cache.recommendations)
                tv_last_found.setText(cache.last_found)
                tv_last_modified.setText(cache.last_modified)
                tv_date_created.setText(cache.date_created)
                tv_date_hidden.setText(cache.date_hidden)

                tv_owner_username.setText(cache.owner_username)
                tv_owner_profile_url.setText(cache.owner_profile_url)

                val p = URLImageParser(tv_description, context)
                val htmlSpan = Html.fromHtml(cache.description, p, null)

                tv_description.setText(htmlSpan)

                //tv_description.setText(Html.fromHtml(cache.description, Html.FROM_HTML_MODE_COMPACT))

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error -> error.printStackTrace() })
        mQueue.add(requestCache)

    }

}// Required empty public constructor
