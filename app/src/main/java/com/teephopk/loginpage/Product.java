package com.teephopk.loginpage;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by floatkeera on 6/21/17.
 */

public class Product {

        public String name;
        public String price;
        public String description;

        public Product(String _name, String _price, String _description){
                name = _name;
                description = _description;
                price = _price;
        }

        public Product(){

        }

        public void send(DatabaseReference dataRef){

        }


}

