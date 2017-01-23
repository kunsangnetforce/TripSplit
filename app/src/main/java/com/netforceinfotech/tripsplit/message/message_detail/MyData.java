package com.netforceinfotech.tripsplit.message.message_detail;

/**
 * Created by John on 8/29/2016.
 */
public class MyData {
    String key, id, image_url, message, name;
    Long timestamp;
    boolean you;

    public MyData(String key, String id, String image_url, String message, String name, Long timestamp,boolean you) {
        this.key = key;
        this.id = id;
        this.image_url = image_url;
        this.message = message;
        this.you=you;
        this.name = name;
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
