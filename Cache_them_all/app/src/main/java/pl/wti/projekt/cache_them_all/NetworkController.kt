package pl.wti.projekt.cache_them_all

import android.text.TextUtils
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import android.app.Application
import com.android.volley.Request


/**
 * Created by lukis on 18.05.2018.
 */
class NetworkController : Application() {

    private lateinit var mRequestQueue: RequestQueue

    val requestQueue: RequestQueue
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(applicationContext)
            }
            return mRequestQueue
        }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun <T> addToRequestQueue(req: Request<T>, tag: String) {
        req.setTag(if (TextUtils.isEmpty(tag)) TAG else tag)
        requestQueue.add(req)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.setTag(TAG)
        requestQueue.add(req)
    }

    fun cancelPendingRequests(tag: Any) {
        if (mRequestQueue != null) {
            mRequestQueue!!.cancelAll(tag)
        }
    }

    companion object {

        private val TAG = NetworkController::class.java.simpleName

        @get:Synchronized
        var instance: NetworkController? = null
            private set
    }

}