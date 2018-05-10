package com.leedian.klozr.utils;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.leedian.klozr.AppManager;
import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;

/**
 * DialogUtil
 *
 * @author Franco
 */
public class DialogUtil {
    private AlertDialog dialog;

    public static void showConfirmYesNo(Context context, String strMsg, String strYes, String strNo, DialogInterface.OnClickListener Listener) {

        TextView msg = new TextView(context);
        msg.setText(strMsg);
        msg.setPadding(0, 20, 0, 0);
        msg.setGravity(Gravity.CENTER);
        msg.setTextSize(18);
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(msg);
        builder.setPositiveButton(strYes, Listener)
               .setNegativeButton(strNo, Listener).show();
    }

    public static void showConfirmOnlyConfirm(Context context, String strMsg, String strYes, DialogInterface.OnClickListener Listener) {

        TextView msg = new TextView(context);
        msg.setText(strMsg);
        msg.setPadding(0, 20, 0, 0);
        msg.setGravity(Gravity.CENTER);
        msg.setTextSize(18);
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(msg);
        builder.setPositiveButton(strYes, Listener)
               .show();
    }

    public static void showInputDialog(Context context, String title, String content, String hint, String preText, String strYes, String strNo, int min, int max, MaterialDialog.InputCallback Listener) {

        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)

                .inputType(InputType.TYPE_CLASS_TEXT)
                .inputRange(min, max)
                .positiveText(strYes)
                .negativeText(strNo)
                .input(hint, preText, false, Listener).show();
    }

    protected String showServerUrlInputBox(Context context) {

        String m_Text;

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom)
                .setTitle(AppResource.getString(R.string.please_input_ip_address));

        final EditText input   = new EditText(builder.getContext());
        InputFilter[]  filters = new InputFilter[1];

        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(
                    CharSequence source, int start,
                    int end, Spanned dest, int destStart, int destEnd) {

                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, destStart) +
                                          source.subSequence(start, end) +
                                          destTxt.substring(destEnd);
                    if (!resultingTxt.matches("^\\d{1,3}(\\." +
                                              "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i = 0; i < splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };

        input.setFilters(filters);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (Validator.isMatchIpAddress(s.toString())) {
                    dialog.getButton(AlertDialog.BUTTON1).setEnabled(true);
                } else { dialog.getButton(AlertDialog.BUTTON1).setEnabled(false); }
            }
        });

        input.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        input.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String m_Text = input.getText().toString();
                AppManager.setPrefAddress(m_Text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON1).setEnabled(false);
        return "";
    }
}
