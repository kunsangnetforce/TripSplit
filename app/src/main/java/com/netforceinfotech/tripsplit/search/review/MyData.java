package com.netforceinfotech.tripsplit.search.review;
public class MyData {
    String id, imageUrl, name, date, review;
    float rating;

    public MyData(String id, String imageUrl, String name, String date, String review, float rating) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.date = date;
        this.review = review;
        this.rating = rating;
    }

    @Override

    public boolean equals(Object obj) {
        if (!(obj instanceof MyData)) {
            return false;
        }

        MyData that = (MyData) obj;
        return this.id.equals(that.id);
    }
}
