package pl.wti.projekt.cache_them_all;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParseJsonNearsetJavaFragment extends Fragment {

    private EditText mEditTextLatitude;
    private EditText mEditTextLogitude;

    private TextView mTextViewResult;
    public RequestQueue mQueue;

    public ParseJsonNearsetJavaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parse_json_nearset_java, container, false);

        mEditTextLatitude = (EditText) view.findViewById(R.id.editText_latitude);
        mEditTextLogitude = (EditText) view.findViewById(R.id.editText_longitude);

        mTextViewResult = (TextView) view.findViewById(R.id.text_view_result);
        Button buttonParse = (Button) view.findViewById(R.id.button_parse);

        mQueue = Volley.newRequestQueue(getContext());

        buttonParse.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(tryParseDouble(mEditTextLatitude.getText().toString()) && tryParseDouble(mEditTextLogitude.getText().toString())){
                    if(tryParseLatitude(mEditTextLatitude.getText().toString())&&tryParseLongitude(mEditTextLogitude.getText().toString())){
                        double latitude = roundNumber(Double.parseDouble(mEditTextLatitude.getText().toString())*10)/10;
                        double longitude = roundNumber(Double.parseDouble(mEditTextLogitude.getText().toString())*10)/10;
                        mTextViewResult.setText(latitude+ " " + longitude);

                        jsonParse(latitude,longitude);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),"incorrect range",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),"not decimal",Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    boolean tryParseDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    boolean tryParseLatitude(String value) {
        if(Double.parseDouble(value)<=90.0 && Double.parseDouble(value)>=-90.0) {
            return true;
        } else {
            return false;
        }
    }

    boolean tryParseLongitude(String value) {
        if(Double.parseDouble(value)<=180.0 && Double.parseDouble(value)>=-180.0) {
            return true;
        } else {
            return false;
        }
    }

    private double roundNumber(double n){
        return Math.round(n);
    }

    private void jsonParse(double latitude, double longitude){
        String url = "https://opencaching.pl/okapi/services/caches/search/nearest?center="+latitude+"|"+longitude+"&consumer_key=" + getString(R.string.customer_key);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    mTextViewResult.append("\nlen="+ jsonArray.length());
                    //mTextViewResult.setText("");
                    for(int i=0; i<jsonArray.length();i++){
                        mTextViewResult.append((i+1)+". - "+jsonArray.getString(i)+"\n");
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

}
