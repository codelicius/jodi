package makasa.dapurkonten.jodohideal.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import makasa.dapurkonten.jodohideal.R;
import makasa.dapurkonten.jodohideal.app.AppController;
import makasa.dapurkonten.jodohideal.object.AllChat;

/**
 * Created by pr1de on 15/01/16.
 */
public class AllChatAdapter extends BaseAdapter {
    private static String INI = ListPartnerAdapter.class.getSimpleName();
    private Activity activity;
    private LayoutInflater inflater;
    private List<AllChat> AllChatItem;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public AllChatAdapter(Activity activity, List<AllChat> AllChatItem){
        this.activity = activity;
        this.AllChatItem= AllChatItem;
    }

    @Override
    public int getCount(){
        return AllChatItem.size();
    }

    @Override
    public Object getItem(int location) {
        return AllChatItem.get(location);
    }

    @Override
    public long getItemId(int position) {
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

        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.chatThumbnail);
        TextView chatPartner = (TextView) convertView.findViewById(R.id.chatPartner);
        TextView chatPreviewMessage = (TextView) convertView.findViewById(R.id.chatPreviewMessage);
        TextView chatID= (TextView) convertView.findViewById(R.id.chatID);

        // getting movie data for the row
        AllChat ac = AllChatItem.get(position);

        // thumbnail image
        thumbNail.setImageUrl(ac.getPartnerPic(), imageLoader);

        chatPartner.setText(ac.getPartnerName());
        chatPreviewMessage.setText(ac.getMessagePreview());

        chatID.setText(String.valueOf(ac.getChatID()));

        return convertView;
    }
}
