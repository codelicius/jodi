package makasa.dapurkonten.jodohideal.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import makasa.dapurkonten.jodohideal.R;
import makasa.dapurkonten.jodohideal.app.AppController;
import makasa.dapurkonten.jodohideal.object.Partner;
import makasa.dapurkonten.jodohideal.object.RecentChat;

/**
 * Created by pr1de on 14/01/16.
 */
public class RecentChatAdapter extends BaseAdapter{
    private static String INI = ListPartnerAdapter.class.getSimpleName();
    private Activity activity;
    private LayoutInflater inflater;
    private List<RecentChat> recentChatItem;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public RecentChatAdapter(Activity activity, List<RecentChat> recentChatItem){
        this.activity = activity;
        this.recentChatItem = recentChatItem;
    }

    @Override
    public int getCount(){
        return recentChatItem.size();
    }

    @Override
    public Object getItem(int location) {
        return recentChatItem.get(location);
    }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //set list row here
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_pasangan, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

            TextView recentPpl = (TextView) convertView.findViewById(R.id.txtRecentPpl);
            TextView recentID = (TextView) convertView.findViewById(R.id.txtPartnerID);
            NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnailRightNav);

            RecentChat rc = recentChatItem.get(position);

            recentPpl.setText(rc.getFirstName() +" "+ rc.getLastName());
            recentID.setText(rc.getPartnerID());
            thumbNail.setImageUrl(rc.getPic(), imageLoader);
            return convertView;
        }

}
