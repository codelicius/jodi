package makasa.dapurkonten.jodohideal.adapter;

/**
 * Created by pr1de on 14/01/16.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import makasa.dapurkonten.jodohideal.R;
import makasa.dapurkonten.jodohideal.app.AppController;
import makasa.dapurkonten.jodohideal.object.PencocokanJawaban;


public class ListPencocokanJawaban extends BaseAdapter {
    private static String INI = ListPencocokanJawaban.class.getSimpleName();
    private Activity activity;
    private LayoutInflater inflater;
    private List<PencocokanJawaban> ItemJawaban;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ListPencocokanJawaban (Activity activity,  List<PencocokanJawaban> ItemJawaban){
        this.activity = activity;
        this.ItemJawaban = ItemJawaban;
    }

    @Override
    public int getCount(){
        return ItemJawaban.size();
    }

    @Override
    public Object getItem(int location) {
        return ItemJawaban.get(location);
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
            convertView = inflater.inflate(R.layout.list_kecocokan_jawaban, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView txtPertanyaan = (TextView) convertView.findViewById(R.id.pertanyaan);
        TextView txtSelfAnswer = (TextView) convertView.findViewById(R.id.selfAnswer);
        TextView txtOtherAnswer = (TextView) convertView.findViewById(R.id.otherAnswer);
        TextView lblOther = (TextView) convertView.findViewById(R.id.lblOther);

        // getting movie data for the row
        PencocokanJawaban pj = ItemJawaban.get(position);

        txtPertanyaan.setText(pj.getPertanyaan());
        txtSelfAnswer.setText(pj.getJawabanKamu());
        txtOtherAnswer.setText(pj.getJawabanDia());
        lblOther.setText(pj.getNamaDia());

        Log.d(INI, "ok di adapter");

        return convertView;
    }
}