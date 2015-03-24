package giroresh.mediacenterclient.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import giroresh.mediacenterclient.R;

/** Custom Toasts for nicer user experience
 * blue means info
 * red means error/warning
 * green means success
 *
 * Created by giro on 2015.03.24..
 */
public class MCCToast extends Toast {
    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link android.app.Application}
     *                or {@link android.app.Activity} object.
     */
    public MCCToast(Context context) {
        super(context);
    }

    public static void makeText(Context context, String txt, int duration, int drawable){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.mcctoast, null);
        view.setBackground(view.getResources().getDrawable(drawable));

        TextView tv = (TextView) view.findViewById(R.id.toastTV);
        tv.setText(txt);

        ImageView iv = (ImageView) view.findViewById(R.id.toastIV);

        switch (drawable) {
            case R.drawable.mcctoastblue:
                iv.setImageResource(R.drawable.info);
                break;
            case R.drawable.mcctoastred:
                iv.setImageResource(R.drawable.warn);
                break;
            case R.drawable.mcctoastgreen:
                iv.setImageResource(R.drawable.check);
                break;
            default:
                break;
        }

        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(view);
        toast.show();
    }
}
