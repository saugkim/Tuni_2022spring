package org.tuni.project_vision;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

public class ImagePreprocessing {

    public static Bitmap CENTER_CROP_AND_RESCALE(Bitmap srcBitmap, int new_scale) {
        Bitmap dstBitmap;
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();
        int final_size;

        if (width >= height){
            final_size = height;
            dstBitmap = Bitmap.createBitmap(
                    srcBitmap,width/2 - height/2,0, final_size, final_size
            );
        } else {
            final_size = width;
            dstBitmap = Bitmap.createBitmap(
                    srcBitmap, 0,height/2 - width/2, final_size, final_size
            );
        }
        //SaveMediaExternally.STORE_IMAGE(scaledBitmap, "png");
        return scale_bitmap(dstBitmap, new_scale);
    }

    private static Bitmap scale_bitmap(Bitmap srcBitmap, int input_size) {
        Bitmap dstBitmap;

        if (srcBitmap.getWidth() < input_size) {
            dstBitmap = Bitmap.createScaledBitmap(srcBitmap, input_size, input_size, true);
        } else {
            dstBitmap = Bitmap.createScaledBitmap(srcBitmap, input_size, input_size, false);
        }

        return dstBitmap;
    }

    public static Bitmap GET_BITMAP_FROM_URI(Context context, Uri uri) {
        Bitmap bitmap = null;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            if(Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri);
                bitmap = ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.RGBA_F16, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
