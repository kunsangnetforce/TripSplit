package com.netforceinfotech.tripsplit.NavigationView.message.contactlist;

import android.Manifest;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.netforceinfotech.tripsplit.R;

import java.util.ArrayList;
import java.util.List;

public class ContactlistFragment extends Fragment

{

    // ArrayList
    ArrayList<SelectUser> selectUsers;
    List<SelectUser> temp;
    // Contact List
    ListView listView;
    // Cursor to load contacts list
    Cursor phones, email;
    // Pop up
    ContentResolver resolver;
    SearchView search;
    SelectUserAdapter adapter;
    Context context;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
         view = inflater.inflate(R.layout.activity_contactlist, container, false);
        context = getActivity();

        setuptoolbar(view);

        listView = (ListView) view.findViewById(R.id.contacts_list);

        search = (SearchView) view.findViewById(R.id.searchView);

        if (weHavePermissionToReadContacts())
        {
            readcontact(view);
            System.out.println("permission is available");
        }
        else
        {
            requestReadContactsPermissionFirst();
            System.out.println("permission is available");
        }

        return view;

    }

    private void setuptoolbar(View view)
    {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        ImageView home = (ImageView) getActivity().findViewById(R.id.homeButton);

        ImageView icon = (ImageView) getActivity().findViewById(R.id.image_appicon);

        home.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }




    private void readcontact(View v) {


        selectUsers = new ArrayList<SelectUser>();
        resolver = getActivity().getContentResolver();


        phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        LoadContact loadContact = new LoadContact();
        loadContact.execute();



        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                adapter.filter(newText);
                return false;
            }
        });
    }

    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            // Get Contact list from Phone

            if (phones != null)
            {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0)
                {
                    Toast.makeText(getActivity(), "No contacts in your contact list.", Toast.LENGTH_LONG).show();
                }

                while (phones.moveToNext())
                {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String EmailAddr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));


                    SelectUser selectUser = new SelectUser();
                    //selectUser.setThumb(bit_thumb);
                    selectUser.setPhone(phoneNumber.toString());
                    selectUser.setEmail(id.toString());
                    selectUser.setName(name.toString());
                   // selectUser.setChecked(false);
                    selectUsers.add(selectUser);

                }
            }
            else
            {
                Log.e("Cursor close 1", "----------------");
            }
            phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new SelectUserAdapter(selectUsers,getActivity());
            listView.setAdapter(adapter);

            // Select item on listclick
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {

                    Log.e("search", "here---------------- listener");

                  //  SelectUser data = SelectUser.get(i);
                }
            });

            listView.setFastScrollEnabled(true);
        }
    }





    private void requestReadContactsPermissionFirst() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(getActivity(), "We need permission so you can text your friends.", Toast.LENGTH_LONG).show();
            requestForResultContactsPermission();
        }
        else {
            requestForResultContactsPermission();
        }
    }

    private void requestForResultContactsPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();

            readcontact(view);
        }
        else {
            Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean weHavePermissionToReadContacts()
    {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

}

