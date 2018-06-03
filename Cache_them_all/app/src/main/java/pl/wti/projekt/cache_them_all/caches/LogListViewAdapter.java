package pl.wti.projekt.cache_them_all.caches;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pl.wti.projekt.cache_them_all.R;

/**
 * Created by lukis on 03.06.2018.
 */

public class LogListViewAdapter extends ArrayAdapter<LogCache>{

    private List<LogCache> logList;
    private Context mCtx;

    public LogListViewAdapter(List<LogCache> logList, Context context) {
        super(context, R.layout.logcache_listview, logList);
        this.logList = logList;
        this.mCtx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        //getting the layoutinflater
        LayoutInflater inflater = LayoutInflater.from(mCtx);

        //creating a view with our xml layout
        View listViewItem = inflater.inflate(R.layout.logcache_listview, null, true);

        //getting text views
        TextView textViewType = listViewItem.findViewById(R.id.tv_log_type);
        TextView textViewDate= listViewItem.findViewById(R.id.tv_log_date);
        TextView textViewUsername = listViewItem.findViewById(R.id.tv_log_username);
        TextView textViewComment = listViewItem.findViewById(R.id.tv_log_comment);

        //Getting the hero for the specified position
        LogCache log = logList.get(position);

        //setting hero values to textviews
        textViewType.setText(log.type);
        textViewDate.setText(log.date);
        textViewUsername.setText(log.username);
        textViewComment.setText(Html.fromHtml(log.comment));

        //returning the listitem

        return listViewItem;
    }
}
