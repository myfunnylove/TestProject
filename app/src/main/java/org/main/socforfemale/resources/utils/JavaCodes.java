package org.main.socforfemale.resources.utils;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Sarvar on 06.09.2017.
 */

public class JavaCodes {

    public static TextWatcher EditTelephoneCodeWatcher = new TextWatcher() {
        // int len = 0;
        String text = "";
        boolean editingBefore = false;
        boolean editingOnChanged = false;
        boolean editingAfter = false;

        public void afterTextChanged(Editable str) {
            if (!editingAfter && editingBefore && editingOnChanged) {
                editingAfter = true;
                str.replace(0, str.length(), text);
                // str.append(text);

                editingBefore = false;
                editingOnChanged = false;
                editingAfter = false;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            if (!editingBefore) {
                editingBefore = true;
                // text = clearText(s.toString());

            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            String d = " ";
            if (!editingOnChanged && editingBefore) {
                editingOnChanged = true;
                text = Functions.INSTANCE.clearText(s.toString());
                if (text.length() == 3 || text.length() == 4
                        || text.length() == 5) {
                    text = text.substring(0, 2) + d
                            + text.substring(2, text.length());
                } else if (text.length() == 6) {
                    text = text.substring(0, 2) + d
                            + text.substring(2, text.length() - 1) + d
                            + text.substring(text.length() - 1, text.length());
                } else if (text.length() == 7) {
                    text = text.substring(0, 2) + d
                            + text.substring(2, text.length() - 2) + d
                            + text.substring(text.length() - 2, text.length());
                } else if (text.length() == 8 || text.length() == 9) {
                    text = text.substring(0, 2) + d + text.substring(2, 5) + d
                            + text.substring(5, 7) + d
                            + text.substring(7, text.length());
                }


            }

        }
    };
}
