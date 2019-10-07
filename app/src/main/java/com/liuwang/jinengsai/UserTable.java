package com.liuwang.jinengsai;

public class UserTable {
    int Id;
    int tempture;
    String gettime;

    public UserTable() {
    }

    public UserTable(int id, int tempture, String gettime) {
        Id = id;
        this.tempture = tempture;
        this.gettime = gettime;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getTempture() {
        return tempture;
    }

    public void setTempture(int tempture) {
        this.tempture = tempture;
    }

    public String getGettime() {
        return gettime;
    }

    public void setGettime(String gettime) {
        this.gettime = gettime;
    }
}
