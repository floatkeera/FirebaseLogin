package com.teephopk.loginpage;

/**
 * Created by floatkeera on 7/19/17.
 */

import com.google.firebase.auth.*;

public class FirebaseManager {

    public FirebaseUser user;

    public FirebaseManager(){
        user = FirebaseAuth.getInstance().getCurrentUser();
    }


}
