package org.tuni.torchmodel;

import android.graphics.Bitmap;

import org.pytorch.IValue;
import org.pytorch.MemoryFormat;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

public class ObjectDetection {

    final static int MIN_INPUT_SIZE = 224;

    public static String RUN_MODEL(Module module, Bitmap srcBitmap) {

        Bitmap mBitmap = ImagePreprocessing.CENTER_CROP_AND_RESCALE(srcBitmap, MIN_INPUT_SIZE);

        // preparing input tensor
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(mBitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB,
                MemoryFormat.CHANNELS_LAST);

        // running the model
        final Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();

        // getting tensor content as java array of floats
        final float[] scores = outputTensor.getDataAsFloatArray();

        // searching for the index with maximum score
        float maxScore = -Float.MAX_VALUE;
        int maxScoreIdx = -1;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }
        return ImageNetClasses.IMAGENET_CLASSES[maxScoreIdx];
    }
}
