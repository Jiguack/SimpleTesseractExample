package com.spacepalm.meidreader;

/**
 * Created by GUACK on 2017-06-28.
 */

public class Checksum {
    /* Luhn */
        public static int getNumber(String meid)
        {
            int sum = 0;
            boolean alternate = true;
            for (int i = meid.length() - 1; i >= 0; i--)
            {
                int n = Integer.parseInt(meid.substring(i, i + 1));
                if (alternate)
                {
                    n *= 2;
                    if (n > 9)
                    {
                        n = (n % 10) + 1;
                    }
                }
                sum += n;
                alternate = !alternate;
            }
            return (10 - (sum % 10));
        }
}
