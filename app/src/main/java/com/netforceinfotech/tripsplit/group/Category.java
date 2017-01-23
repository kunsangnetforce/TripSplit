package com.netforceinfotech.tripsplit.group;

/**
 * Created by Netforce on 12/13/2016.
 */

public class Category {
    String name,id;

    public Category(String name, String id) {
        this.name = name;
        this.id = id;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Category)) {
            return false;
        }

        Category that = (Category) obj;
        return this.id.equals(that.id);
    }
}
