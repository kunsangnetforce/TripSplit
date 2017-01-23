package com.netforceinfotech.tripsplit.group.searchgroup;

/**
 * Created by John on 8/29/2016.
 */
public class MyData {

    public String group_id, title, city, country, category_id, image, category_name;

    public MyData(String group_id, String title, String city, String country, String category_id, String image, String category_name) {
        this.group_id = group_id;
        this.title = title;
        this.city = city;
        this.country = country;
        this.category_id = category_id;
        this.image = image;
        this.category_name = category_name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MyData)) {
            return false;
        }

        MyData that = (MyData) obj;
        return this.group_id.equals(that.group_id);
    }
}
