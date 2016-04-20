package makasa.dapurkonten.jodohideal.adapter;

/**
 * Created by pr1de on 02/12/15.
 */

import makasa.dapurkonten.jodohideal.R;
import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.AppController;
import makasa.dapurkonten.jodohideal.object.Partner;
import java.util.List;

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

public class ListPartnerAdapter extends BaseAdapter {
    private static String INI = ListPartnerAdapter.class.getSimpleName();
    private Activity activity;
    private LayoutInflater inflater;
    private List<Partner> PartnerItem;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ListPartnerAdapter(Activity activity, List<Partner> partnerItem){
        this.activity = activity;
        this.PartnerItem = partnerItem;
    }
    public void clearAdapter()
    {
        PartnerItem.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getCount(){
        return PartnerItem.size();
    }

    @Override
    public Object getItem(int location) {
        return PartnerItem.get(location);
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
                .findViewById(R.id.thumbnailFoto);
        TextView fullName = (TextView) convertView.findViewById(R.id.txtCocokNama);
        TextView kecocokan = (TextView) convertView.findViewById(R.id.txtCocokPersen);
        TextView detail = (TextView) convertView.findViewById(R.id.txtCocokDetail);
        TextView pID = (TextView) convertView.findViewById(R.id.txtPID);

        // getting movie data for the row
        Partner p = PartnerItem.get(position);

        // thumbnail image
        int time = (int) (System.currentTimeMillis());
        thumbNail.setDefaultImageResId(R.drawable.avatar);
        thumbNail.setImageUrl(p.getUrlFoto()+"?time="+time, imageLoader);

        fullName.setText(p.getFullName());

        // jika nilai berupa integer atau double atau float, maka diubah kestring dengan string valueof
        kecocokan.setText(String.valueOf(p.getKecocokan()) + "%");

        detail.setText(String.valueOf(p.getUmur()) + ", " +
                        p.getGender() + ", " + p.getSuku() + ", " +
                        p.getAgama());

        pID.setText(String.valueOf(p.getpID()));

        /**
         *
         * contoh penggunaan array
         *
         * String genreStr = "";
        for (String str : p.getArray()) {
            genreStr += str + ", ";
        }
        genreStr = genreStr.length() > 0 ? genreStr.substring(0,
                genreStr.length() - 2) : genreStr;
        genre.setText(genreStr);
        **/
        Log.d(INI,"ok");

        return convertView;
    }
}
