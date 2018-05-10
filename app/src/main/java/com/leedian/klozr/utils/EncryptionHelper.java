package com.leedian.klozr.utils;
import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * EncryptionHelper
 *
 * @author Franco
 */
public class EncryptionHelper {
    public static String des3EncodeECBtoBase64(String key, String data) throws Exception {

        byte[] des = des3EncodeECB(key.getBytes(), data.getBytes());
        return Base64.encodeToString(des, Base64.DEFAULT);
    }

    public static byte[] des3EncodeECB(byte[] key, byte[] data)
            throws Exception {

        Key              desKey;
        DESedeKeySpec    spec       = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        desKey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, desKey);
        return cipher.doFinal(data);
    }
}
