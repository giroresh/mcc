package giroresh.mediacenterclient.helper;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import giroresh.mediacenterclient.R;

/** For text input validation
 * For now it is for Login Class and SetAdminKey
 * Created by giro on 2015.03.20..
 */
public class MCCTextWatcher implements TextWatcher {

    private final View view;

    public MCCTextWatcher(View view) {
        this.view = view;
    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * are about to be replaced by new text with length <code>after</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * have just replaced old text that had length <code>before</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * This method is called to notify you that, somewhere within
     * <code>s</code>, the text has been changed.
     * It is legitimate to make further changes to <code>s</code> from
     * this callback, but be careful not to get yourself into an infinite
     * loop, because any changes you make will cause this method to be
     * called again recursively.
     * (You are not told where the change took place because other
     * afterTextChanged() methods may already have made other changes
     * and invalidated the offsets.  But if you need to know here,
     * you can use {@link android.text.Spannable#setSpan} in {@link #onTextChanged}
     * to mark your place and then look up from here where the span
     * ended up.
     *
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {
        switch (view.getId()) {
            case R.id.serverIP:
                if (doValidation(s.toString())) {
                    view.setBackground(view.getResources().getDrawable(R.drawable.edittextborder));
                } else {
                    view.setBackground(view.getResources().getDrawable(R.drawable.edittextborderred));
                }
                break;
            case R.id.portNr:
                if (doPortValidation(s.toString())) {
                    view.setBackground(view.getResources().getDrawable(R.drawable.edittextborder));
                } else {
                    view.setBackground(view.getResources().getDrawable(R.drawable.edittextborderred));
                }
                break;
            case R.id.setAdminKeyET:
                if (doAdminkeyValidation(s.toString())) {
                    view.setBackground(view.getResources().getDrawable(R.drawable.edittextborder));
                } else {
                    view.setBackground(view.getResources().getDrawable(R.drawable.edittextborderred));
                }
                break;
        }
    }

    /**
     * Password must be at least as strong as the password "admin" is
     * @param txt text to validate
     * @return true if at least 5 characters long and following special characters
     *   ! # %  + ? _
     */
    public boolean doAdminkeyValidation(String txt) {
        return txt.matches("(?=^.{5,}$)(?=.*([\\d]*))(?=.*[a-zA-Z])(?=.*([!#%_+?])*)(?!.*[\\s$;\"']).*$");
    }

    private Boolean doPortValidation(String txt) {
        return txt.matches("^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)$");
    }

    private Boolean doValidation(String txt) {
        Boolean matched = txt.matches("^(?:\\d{1,3}\\.){3}\\d{1,3}$");
        if (!matched) {
            matched = txt.matches("^((http|https)://)?([a-z\\.-]+)\\.([a-z\\.]{2,})+$");
            if (!matched) {
                matched = txt.matches("^([a-z\\.-]+)\\.([a-z\\.]{2,})+\\b(?<!(^\\d{3}\\.{1}))\\b");
                return matched;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
