package crk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Mifan {

    private static String md52Str(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (int b : bArr) {
            int i2 = b;
            if (i2 < 0) {
                i2 += 256;
            }
            if (i2 < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(i2));
        }
        return sb.toString();
    }


    public static String md5(String str) {
        try {
            return md52Str(MessageDigest.getInstance("MD5").digest(str.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}