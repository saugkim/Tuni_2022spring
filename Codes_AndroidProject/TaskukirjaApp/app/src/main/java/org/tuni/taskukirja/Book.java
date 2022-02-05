package org.tuni.taskukirja;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "library")
public class Book implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name="issue")
    private int numberOfIssue;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name="publish_year")
    private String yearOfPublish;

    @ColumnInfo(name="pages")
    private String numberOfPage;

    @ColumnInfo(name = "purchase_date")
    private String dateOfPurchase;

    public Book(){
    }

    @Ignore
    public Book(int number, String title, String year, String pages, String date) {
        this.numberOfIssue = number;
        this.title = title;
        this.yearOfPublish = year;
        this.numberOfPage = pages;
        this.dateOfPurchase = date;
    }

    public long getId(){
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getNumberOfIssue() {
        return this.numberOfIssue;
    }
    public void setNumberOfIssue(int number) {
        this.numberOfIssue =number;
    }
    public String getNumberOfPage() {
        return this.numberOfPage;
    }
    public void setNumberOfPage(String pages) {
        this.numberOfPage = pages;
    }
    public String getYearOfPublish() {
        return this.yearOfPublish;
    }
    public void setYearOfPublish(String year) {
        this.yearOfPublish = year;
    }
    public String getDateOfPurchase() {
        return this.dateOfPurchase;
    }
    public void setDateOfPurchase(String date) {
        this.dateOfPurchase = date;
    }

}