package com.netforceinfotech.tripsplit.login;

/**
 * Created by Netforce on 10/17/2016.
 */

public class CountryData implements Comparable<CountryData> {
    public String country;
    public String code;

    public CountryData(String country, String code) {
        this.code = code;
        this.country = country;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CountryData)) {
            return false;
        }

        CountryData that = (CountryData) obj;
        return this.country.equals(that.country);
    }

    @Override
    public int compareTo(CountryData countryData) {
        return this.country.compareTo(countryData.country);
    }
}
