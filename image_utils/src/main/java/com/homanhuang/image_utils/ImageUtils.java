package com.homanhuang.image_utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtils {

    //time stamp file name
    public String createFileName(String prefix, String extention) {
        String filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (prefix != null) { filename = prefix + filename; }
        if (extention != null) { filename = filename + extention; }
        return  filename;
    }


}

