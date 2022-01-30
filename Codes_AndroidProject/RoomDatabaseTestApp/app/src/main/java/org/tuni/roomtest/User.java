package org.tuni.roomtest;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "user_table")
public class User implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    public User(){

    }

    @Ignore
    public User(String fname, String lname) {
        this.firstName = fname;
        this.lastName = lname;
    }

    public long getId(){
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }
    public String getLastName() {
        return this.lastName;
    }
    public void setFirstName(String name){
        this.firstName = name;
    }
    public void setLastName(String name) {
        this.lastName = name;
    }
    public void setId(long id){
        this.id = id;
    }
}