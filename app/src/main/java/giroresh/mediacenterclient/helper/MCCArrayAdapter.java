package giroresh.mediacenterclient.helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import giroresh.mediacenterclient.R;
import giroresh.mediacenterclient.playlistItems.filetypes.PlaylistItems;

/** customized ArrayAdapter
 * it shows the right image according to the actual file type
 * Created by giro on 2015.04.13..
 */
public class MCCArrayAdapter extends ArrayAdapter<PlaylistItems> {
    private int resource;
    private Context context;
    private List<PlaylistItems> items;

    public MCCArrayAdapter(Context context, int resource, List<PlaylistItems> items) {
        super(context, resource, items);
        this.resource = resource;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.playlistItemTV);
        tv.setCompoundDrawablePadding(5);
        PlaylistItems playlistItem = items.get(position);
        if (playlistItem.getType() == 100) {
            tv.setText(playlistItem.getID() + " | " + playlistItem.getLabel());
            tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mp3, 0, 0, 0);
        } else if ((100 < playlistItem.getType()) && (playlistItem.getType() < 300)) {
            tv.setText(playlistItem.getID() + " | " + playlistItem.getLabel());
            tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rom, 0, 0, 0);
        } else if (playlistItem.getType() == 300) {
            tv.setText(playlistItem.getID() + " | " + playlistItem.getLabel());
            tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mp4, 0, 0, 0);
        } else {
            tv.setText(playlistItem.getID() + " | " + playlistItem.getLabel());
            tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ufo, 0, 0, 0);
        }
        return convertView;
    }
}
