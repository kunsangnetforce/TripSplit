package com.netforceinfotech.tripsplit.login;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.netforceinfotech.tripsplit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    EditText email, password;
    Button buttonSignIn;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        email = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.passWord);
        buttonSignIn = (Button) view.findViewById(R.id.buttonSignIn);
    }

}
