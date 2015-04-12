package giroresh.mediacenterclient.helper;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/** For now its just a custom ArrayAdapter that does nothing more then we default one
 * Created by giro on 2015.04.10..
 */
public class MCCArrayAdapter extends ArrayAdapter<String> {
    private int resource;
    Context context;
    private List<String> items;

    public MCCArrayAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.resource = resource;
        this.context = context;
        this.items = items;
    }
}
