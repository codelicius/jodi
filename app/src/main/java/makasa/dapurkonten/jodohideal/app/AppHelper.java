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
        int firstItem = 380;
        int lstItem = 380;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            Log.d("lsheight", String.valueOf(totalHeight));
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = firstItem + lstItem + totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);

    }
}
