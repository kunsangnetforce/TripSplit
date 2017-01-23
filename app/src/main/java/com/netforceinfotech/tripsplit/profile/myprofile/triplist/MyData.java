package com.netforceinfotech.tripsplit.profile.myprofile.triplist;

/**
 * Created by John on 8/29/2016.
 */
public class MyData {
    String trip_id,image,source,destination,departure_date,itinerary;

    public MyData(String trip_id, String image, String source, String destination, String departure_date, String itinerary) {
        this.trip_id = trip_id;
        this.image = image;
        this.source = source;
        this.destination = destination;
        this.departure_date = departure_date;
        this.itinerary = itinerary;
    }

    @Override

    public boolean equals(Object obj) {
        if (!(obj instanceof MyData)) {
            return false;
        }

        MyData that = (MyData) obj;
        return this.trip_id.equals(that.trip_id);
    }
}
