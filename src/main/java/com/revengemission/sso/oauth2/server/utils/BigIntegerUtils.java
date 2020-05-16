package com.revengemission.sso.oauth2.server.utils;

import java.math.BigInteger;

public class BigIntegerUtils {
    public static byte[] toBytesUnsigned(BigInteger bigInt) {
        int bitlen = bigInt.bitLength();
        bitlen = bitlen + 7 >> 3 << 3;
        byte[] bigBytes = bigInt.toByteArray();
        if (bigInt.bitLength() % 8 != 0 && bigInt.bitLength() / 8 + 1 == bitlen / 8) {
            return bigBytes;
        } else {
            int startSrc = 0;
            int len = bigBytes.length;
            if (bigInt.bitLength() % 8 == 0) {
                startSrc = 1;
                --len;
            }

            int startDst = bitlen / 8 - len;
            byte[] resizedBytes = new byte[bitlen / 8];
            System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
            return resizedBytes;
        }
    }

    private BigIntegerUtils() {
    }
}
