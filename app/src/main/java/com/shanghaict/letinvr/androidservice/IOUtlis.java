package com.shanghaict.letinvr.androidservice;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018\10\15 0015.
 */

public class IOUtlis {
    public IOUtlis() {
    }

    public static String readFully(InputStream inputStream) throws IOException {
        if(inputStream == null) {
            return "";
        } else {
            BufferedInputStream bufferedInputStream = null;
            ByteArrayOutputStream byteArrayOutputStream = null;

            try {
                bufferedInputStream = new BufferedInputStream(inputStream);
                byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                boolean var4 = false;

                int available;
                while((available = bufferedInputStream.read(buffer)) >= 0) {
                    byteArrayOutputStream.write(buffer, 0, available);
                }

                String var5 = byteArrayOutputStream.toString();
                return var5;
            } finally {
                if(bufferedInputStream != null) {
                    bufferedInputStream.close();
                }

            }
        }
    }
}
