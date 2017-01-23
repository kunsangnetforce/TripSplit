package com.netforceinfotech.tripsplit.NavigationView.message.contactlist;

import android.graphics.Bitmap;

import java.util.HashSet;

/**
 * Created by John on 9/21/2016.
 */
public class SelectU
{

     HashSet<String> emails;
     HashSet<String> phones;
     HashSet<String> addresses;
     HashSet<String> names;
    Bitmap thumb;
    private String contactId;
    private boolean checked = false;


    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public HashSet<String> getName(){

        return names;
    }

    public void setName(String name) {

        this.names.add(name);
    }

    public HashSet<String> getPhone() {
        return phones;
    }

    public void setPhone(String phone) {

        this.phones.add(phone);
    }

    public HashSet<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(String address) {

        this.addresses.add(address);
    }

    public void setEmails(String email) {

        this.emails.add(email);
    }

    public HashSet<String> getEmails() {
        return emails;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
