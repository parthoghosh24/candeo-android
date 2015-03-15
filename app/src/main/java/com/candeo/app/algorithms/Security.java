package com.candeo.app.algorithms;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Partho on 14/3/15.
 */
public class Security {
    private static final String HMAC_ALGORITHM="HmacSHA256";
    public static String generateHmac(String secret, String message)
    {
        String hmac="";
        try {
            Mac sha256HMAC = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(),HMAC_ALGORITHM);
            sha256HMAC.init(secretKey);
            hmac= Base64.encodeToString(sha256HMAC.doFinal(message.getBytes()),Base64.NO_WRAP);
        }
        catch (NoSuchAlgorithmException|InvalidKeyException ex)
        {
            ex.printStackTrace();
        }



        return hmac;
    }
}
