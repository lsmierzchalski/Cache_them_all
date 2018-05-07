package pl.wti.projekt.cache_them_all


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_second.*
import okhttp3.*
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 */
class SecondFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view:View = inflater!!.inflate(R.layout.fragment_second, container, false)

        Toast.makeText(context, "Please wait", Toast.LENGTH_SHORT).show()

        var url: String = "https://opencaching.pl/okapi/services/caches/search/nearest?center=54.3|22.3&consumer_key=" + getString(R.string.customer_key)

        var client : OkHttpClient = OkHttpClient()

        var request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response){
                if(response.isSuccessful){
                    val myResponse : String = response.body()?.string().toString()

                    activity.runOnUiThread(object : Runnable {
                        override fun run() {
                            text_data.setText(myResponse)
                        }
                    })
                }
            }
        })

        // Inflate the layout for this fragment
        return view
    }

}// Required empty public constructor
