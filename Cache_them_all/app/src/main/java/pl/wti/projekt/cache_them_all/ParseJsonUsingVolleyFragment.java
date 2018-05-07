package pl.wti.projekt.cache_them_all;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
public class ParseJsonUsingVolleyFragment extends Fragment {

    private TextView mTextViewResult;
    public RequestQueue mQueue;

    public ParseJsonUsingVolleyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parse_json_using_volley, container, false);

        mTextViewResult = (TextView) view.findViewById(R.id.text_view_result);
        Button buttonParse = (Button) view.findViewById(R.id.button_parse);

        mQueue = Volley.newRequestQueue(getContext());

        buttonParse.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                jsonParse();
            }
        });

        return view;
    }

    private void jsonParse(){
        String url = "https://api.myjson.com/bins/kp9wz";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("employees");

                    mTextViewResult.setText("");
                    for(int i=0; i<jsonArray.length();i++){
                        JSONObject employee = jsonArray.getJSONObject(i);

                        String firstName = employee.getString("firstname");
                        int age = employee.getInt("age");
                        String mail = employee.getString("mail");

                        mTextViewResult.append(firstName+", "+String.valueOf(age)+", "+mail+"\n\n");
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
