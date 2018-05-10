package com.leedian.klozr.utils;
import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator
 *
 * @author Franco
 */
public class Validator {
    private static InputFilter[] ip_filters        = new InputFilter[1];
    private static InputFilter   ip_address_filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int destStart, int destEnd) {

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
                    for (String split : splits) {
                        if (Integer.valueOf(split) > 255) {
                            return "";
                        }
                    }
                }
            }
            return null;
        }
    };

    static public boolean isMatchIpAddress(String ip) {

        final Pattern IP_ADDRESS
                = Pattern.compile(
                "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                + "|[1-9][0-9]|[0-9]))");

        Matcher matcher = IP_ADDRESS.matcher(ip);
        return matcher.matches();
    }

    static public boolean isMatchScanData(String data, String[] out) {

        String temp;
        String ip;
        String zipKey;

        int root;
        int ipEnd;

        root = data.indexOf("http://");
        if (root < 0) return false;

        root = root + "http://".length();

        temp = data.substring(root);

        ipEnd = temp.indexOf("/");

        if (ipEnd < 0) { return false; }

        ip = temp.substring(0, ipEnd);

        String str[] = ip.split(":");

        ip = str[0];

        root = data.indexOf("/view/");
        if (root < 0) return false;

        root = root + "/view/".length();

        zipKey = data.substring(root);

        out[0] = ip;

        out[1] = zipKey;

        return true;
    }

    static public InputFilter[] ipFilter() {

        ip_filters[0] = ip_address_filter;
        return ip_filters;
    }
}
