package com.homanhuang.tomtomtest.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Homan on 3/5/2018.
 */

public class FileIO {

    public String createFileName(String prefix, String extension) {
        String x = "";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        x = prefix + timeStamp + "." + extension;
        return x;
    }
}
