package com.netforceinfotech.tripsplit.group.mygroup;

/**
 * Created by John on 8/29/2016.
 */
public class MyData {

    public String category,city,country,image_url,user_id,key,title;
    Long timestamp;

    public MyData(String category, String city, String country, String image_url, String user_id, String key, String title, Long timestamp) {
        this.category = category;
        this.city = city;
        this.country = country;
        this.image_url = image_url;
        this.user_id = user_id;
        this.key = key;
        this.title = title;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MyData)) {
            return false;
        }

        MyData that = (MyData) obj;
        return this.key.equals(that.key);
    }
}
