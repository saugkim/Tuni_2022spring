package org.tuni.cameratest;

import android.graphics.Bitmap;

public class ImagePreprocessing {

    public static Bitmap CENTER_CROP_AND_RESCALE(Bitmap srcBitmap, int new_scale) {
        Bitmap dstBitmap;
        int final_length;
        if (srcBitmap.getWidth() >= srcBitmap.getHeight()){
            final_length = srcBitmap.getHeight();
            dstBitmap = Bitmap.createBitmap(
                    srcBitmap,
                    srcBitmap.getWidth()/2 - srcBitmap.getHeight()/2,
                    0,
                    final_length,
                    final_length
            );
        } else {
            final_length = srcBitmap.getWidth();
            dstBitmap = Bitmap.createBitmap(
                    srcBitmap,
                    0,
                    srcBitmap.getHeight()/2 - srcBitmap.getWidth()/2,
                    final_length,
                    final_length
            );
        }
        Bitmap scaledBitmap = scale_bitmap(dstBitmap, new_scale);
        SaveMediaExternally.STORE_IMAGE(scaledBitmap, "png");
        return scaledBitmap;
    }

    private static Bitmap scale_bitmap(Bitmap srcBitmap, int input_size) {
        Bitmap dstBitmap;

        if (srcBitmap.getWidth() < input_size) {
            dstBitmap = Bitmap.createScaledBitmap(srcBitmap, input_size, input_size, false);
        } else {
            dstBitmap = Bitmap.createScaledBitmap(srcBitmap, 400, 400, true);
        }

        return dstBitmap;
    }
}
