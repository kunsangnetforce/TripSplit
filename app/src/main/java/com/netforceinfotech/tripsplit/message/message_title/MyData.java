package com.netforceinfotech.tripsplit.message.message_title;

/**
 * Created by John on 8/29/2016.
 */
public class MyData {
    String key,chat_id, image_url, last_message, name,reg_id;
    long timestamp;
    long count;
    boolean seen;

    public MyData(String key, String chat_id, String image_url, String last_message, String name, long timestamp, boolean seen, long count, String reg_id) {
        this.key = key;
        this.count=count;
        this.reg_id=reg_id;
        this.chat_id = chat_id;
        this.image_url = image_url;
        this.last_message = last_message;
        this.seen=seen;
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
