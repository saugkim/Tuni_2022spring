package org.tuni.torchmodel;

import android.graphics.Bitmap;

import org.pytorch.IValue;
import org.pytorch.MemoryFormat;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

//https://learnopencv.com/pytorch-for-beginners-semantic-segmentation-using-torchvision/
public class ImageSegmentation {

    final static int MIN_INPUT_SIZE = 224;
    private static final int CLASS_NUM = 21;

    private static final int DOG = 12;
    private static final int PERSON = 15;
    private static final int SHEEP = 17;

    //ArrayList<String> segmentedClasses = new ArrayList<>();
    //static Set<String> set = new HashSet<String>();

    public static Bitmap RUN_MODEL(Module module, Bitmap srcBitmap) {

        Bitmap mBitmap = ImagePreprocessing.CENTER_CROP_AND_RESCALE(srcBitmap, MIN_INPUT_SIZE);

        // preparing input tensor
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(mBitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB,
                MemoryFormat.CHANNELS_LAST);

        final float[] inputs = inputTensor.getDataAsFloatArray();

        Map<String, IValue> outTensors =
                module.forward(IValue.from(inputTensor)).toDictStringKey();

        // the key "out" of the output tensor contains the semantic masks
        // see https://pytorch.org/hub/pytorch_vision_deeplabv3_resnet101

        final Tensor outputTensor = Objects.requireNonNull(outTensors.get("out")).toTensor();
        final float[] scores = outputTensor.getDataAsFloatArray();

        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        int[] intValues = new int[width * height];
        // go through each element in the output of size [WIDTH, HEIGHT] and
        // set different color for different labels
        for (int j = 0; j < height; j++) {
            for (int k = 0; k < width; k++) {
                int maxi = 0, maxj = 0, maxk = 0;
                double maxnum = -Double.MAX_VALUE;
                for (int i = 0; i < CLASS_NUM; i++) {
                    float score = scores[i * (width * height) + j * width + k];
                    if (score > maxnum) {
                        maxnum = score;
                        maxi = i; maxj = j; maxk = k;
                    }
                }
                intValues[maxj*width + maxk] = DeepLabV3Classes.LABEL_COLORS[maxi];
                //set.add(DeepLabV3Classes.DEEP_LAB_V3_CLASSES[maxi]);

                /*// color coding for person (red), dog (green), sheep (blue) black color for background and other classes
                if (maxi == PERSON)
                    intValues[maxj*width + maxk] = 0xFFFF0000; // red
                else if (maxi == DOG)
                    intValues[maxj*width + maxk] = 0xFF00FF00; // green
                else if (maxi == SHEEP)
                    intValues[maxj*width + maxk] = 0xFF0000FF; // blue
                else
                    intValues[maxj*width + maxk] = 0xFF000000; // black*/
            }
        }

        Bitmap bmpSegmentation = Bitmap.createScaledBitmap(mBitmap, width, height, true);
        Bitmap outputBitmap = bmpSegmentation.copy(bmpSegmentation.getConfig(), true);
        outputBitmap.setPixels(intValues, 0, outputBitmap.getWidth(), 0, 0, outputBitmap.getWidth(), outputBitmap.getHeight());

        return Bitmap.createScaledBitmap(outputBitmap, mBitmap.getWidth(), mBitmap.getHeight(), true);
    }
}
