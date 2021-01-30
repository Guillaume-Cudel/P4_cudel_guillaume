package com.example.mareu;

public class Utils {

    public static String pad(int input){
        String str = "";
        if (input >= 10){
            str = Integer.toString(input);
        } else {
            str = "0" + Integer.toString(input);
        }
        return str;
    }
}
