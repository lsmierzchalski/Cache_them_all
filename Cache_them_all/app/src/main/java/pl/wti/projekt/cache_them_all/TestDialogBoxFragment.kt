package pl.wti.projekt.cache_them_all


import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_test_dialog_box.*
import android.text.method.TextKeyListener.clear
import android.widget.TextView



/**
 * A simple [Fragment] subclass.
 */
class TestDialogBoxFragment : Fragment() {

    lateinit var listItems: Array<String>
    lateinit var checkedItems: BooleanArray
    var mUserItems: ArrayList<Int> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_test_dialog_box, container, false)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        listItems = resources.getStringArray(R.array.types_of_caches)
        checkedItems = BooleanArray(listItems.size)

        for (i in 0 until checkedItems.size) {
            checkedItems[i] = true
            mUserItems.add(i)
            textView_types_of_caches.append(listItems[i]+", ")
        }

        open_dialog.setOnClickListener(View.OnClickListener {
            val mBuilder = AlertDialog.Builder(context)
            mBuilder.setTitle(R.string.title_type_of_caches)
            mBuilder.setMultiChoiceItems(listItems, checkedItems, DialogInterface.OnMultiChoiceClickListener { dialogInterface, position, isChecked ->
                if (isChecked) {
                    mUserItems.add(position)
                } else {
                    mUserItems.remove(Integer.valueOf(position))
                }
            })

            mBuilder.setCancelable(false)
            mBuilder.setPositiveButton("GOOD", DialogInterface.OnClickListener { dialogInterface, which ->
                var item = ""
                for (i in 0 until mUserItems.size) {
                    item = item + listItems[mUserItems.get(i)]
                    if (i != mUserItems.size - 1) {
                        item = item + ", "
                    }
                }
                textView_types_of_caches.setText(item)
            })

            mBuilder.setNegativeButton("not", DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })

            mBuilder.setNeutralButton("FULL", DialogInterface.OnClickListener { dialogInterface, which ->
                textView_types_of_caches.setText("")
                mUserItems.clear()
                for (i in 0 until checkedItems.size) {
                    checkedItems[i] = true
                    mUserItems.add(i)
                    textView_types_of_caches.append(listItems[i]+", ")
                }
            })

            val mDialog = mBuilder.create()
            mDialog.show()
        })

    }

}// Required empty public constructor
