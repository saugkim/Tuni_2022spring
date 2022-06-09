package org.tuni.project_vision;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName="image_table")
public class Image {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name="uri")
    private String imageUri;

    @ColumnInfo(name="label")
    private String label;

    @ColumnInfo(name="filename")
    private String filename;

    @ColumnInfo(name="longitude")
    private float longitude;

    @ColumnInfo(name="latitude")
    private float latitude;

    @ColumnInfo(name="isCorrect")
    private Integer isCorrect;

    public Image() {
        longitude = -200;
        latitude = -200;
    }

    @Ignore
    public Image(String filename, String imageUri, String label){
        this.filename = filename;
        this.label = label;
        this.imageUri = imageUri;
        longitude = -200;
        latitude = -200;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setIsCorrect(Integer correct) {
        isCorrect = correct;
    }

    public int getId() {
        return id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getLabel() {
        return label;
    }

    public String getFilename() {
        return filename;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public Integer isCorrect() {
        return isCorrect;
    }

}
