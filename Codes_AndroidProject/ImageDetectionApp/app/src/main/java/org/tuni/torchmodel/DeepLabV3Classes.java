package org.tuni.torchmodel;

public class DeepLabV3Classes {

    public static String[] DEEP_LAB_V3_CLASSES = new String[]{
        "background",
        "aeroplane",
        "bicycle",
        "bird",
        "boat",
        "bottle",
        "bus",
        "car",
        "cat",
        "chair",
        "cow",
        "dining table",
        "dog",
        "horse",
        "motorbike",
        "person",
        "potted plant",
        "sheep",
        "sofa",
        "train",
        "tv/monitor"
    };
    //Opaque white :0xFFFFFFFF. Transparent White : 0x00FFFFFF
    public static int[] LABEL_COLORS = new int[]{
        0xFF000000,
        0xFF800000,
        0xFF008000,
        0xFF808000,
        0xFF000080,
        0xFF800080,
        0xFF008080,
        0xFF808080,
        0xFF400000,
        0xFFC00000,
        0xFF408000,
        0xFFC08000,
        0xFF400080,
        0xFFC00080,
        0xFF408080,
        0xFFC08080,
        0xFF004000,
        0xFF804000,
        0xFF00C000,
        0xFF80C000,
        0xFF004080,
    };

    /*
    (0, 0, 0),
    (128, 0, 0), (0, 128, 0), (128, 128, 0), (0, 0, 128), (128, 0, 128),
     (0, 128, 128), (128, 128, 128), (64, 0, 0), (192, 0, 0), (64, 128, 0),
     (192, 128, 0), (64, 0, 128), (192, 0, 128), (64, 128, 128), (192, 128, 128),
     (0, 64, 0), (128, 64, 0), (0, 192, 0), (128, 192, 0), (0, 64, 128)]) */
}
