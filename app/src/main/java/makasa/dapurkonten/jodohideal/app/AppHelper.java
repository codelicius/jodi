package makasa.dapurkonten.jodohideal.app;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by pr1de on 04/02/16.
 */
public class AppHelper {

    public static void listViewDynamicHeight(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        Log.d("jumlahitem", String.valueOf(listAdapter.getCount()));
        //in px

        int totalHeight = 0;
        int loop = listAdapter.getCount();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            totalHeight += 20;
            Log.d("lsheight", String.valueOf(totalHeight));
        }

        View listItem = listAdapter.getView(1, null, listView);
        listItem.measure(0, 0);
        totalHeight += listItem.getMeasuredHeight();
        totalHeight += 30;

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);

    }
}
