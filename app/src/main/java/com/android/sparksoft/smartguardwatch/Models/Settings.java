package com.android.sparksoft.smartguardwatch.Models;

/**
 * Created by Daniel on 10/18/2015.
 */
public class Settings {
    private String key;
    private String value;

    public Settings(String _key, String _value)
    {
        key = _key;
        value = _value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }


}
