package org.tuni.project_vision;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ImageViewModel extends AndroidViewModel {
    private final LiveData<List<Image>> falseImages;
    LiveData<List<Image>> trueImages;

    LiveData<Integer> total_number;
    LiveData<Integer> correct_number;

    ImageRepository imageRepository;

    public ImageViewModel(@NonNull Application application) {
        super(application);

        imageRepository = new ImageRepository(application);
        falseImages = imageRepository.getIncorrectOnes();
        trueImages = imageRepository.getCorrectOnes();

        total_number = imageRepository.getCounts();
        correct_number = imageRepository.getCountsCorrect();
    }

    public LiveData<List<Image>> getFalseImages() {
        return falseImages;
    }
    public LiveData<List<Image>> getTrueImages() {
        return trueImages;
    }

    public LiveData<Integer> getCounts() {
        return total_number;
    }
    public LiveData<Integer> getCountsCorrect() {
        return correct_number;
    }

}
