package com.netforceinfotech.tripsplit.search.searchfragment;

/**
 * Created by John on 8/29/2016.
 */
public class CarData {

    public CarData(String tour_id, String start_price, String date_created, String user_id, String cartype, String pax, String space, String trip_group, String age_group_lower, String age_group_upper, String trip, String vehical_type, String depart_address, String country_code, String etd, String eta, String iteinerary, String img_name, String currency, String depart_lat, String depart_lon, String return_eta, String return_etd, String dest_address, String dest_lat, String dest_lon) {
        this.tour_id = tour_id;
        this.start_price = start_price;
        this.date_created = date_created;
        this.user_id = user_id;
        this.cartype = cartype;
        this.pax = pax;
        this.space = space;
        this.trip_group = trip_group;
        this.age_group_lower = age_group_lower;
        this.age_group_upper = age_group_upper;
        this.trip = trip;
        this.vehical_type = vehical_type;
        this.depart_address = depart_address;
        this.country_code = country_code;
        this.etd = etd;
        this.eta = eta;
        this.iteinerary = iteinerary;
        this.image = img_name;
        this.currency = currency;
        this.depart_lat = depart_lat;
        this.depart_lon = depart_lon;
        this.return_eta = return_eta;
        this.return_etd = return_etd;
        this.dest_address = dest_address;
        this.dest_lat = dest_lat;
        this.dest_lon = dest_lon;
    }


    public String tour_id, start_price, date_created, user_id, cartype, pax, space, trip_group, age_group_lower, age_group_upper, trip, vehical_type,
            depart_address, country_code, etd, eta, iteinerary, image, currency, depart_lat, depart_lon, return_eta, return_etd, dest_address, dest_lat, dest_lon;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CarData)) {
            return false;
        }

        CarData that = (CarData) obj;
        return this.tour_id.equals(that.tour_id);
    }
}
