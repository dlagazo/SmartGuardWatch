package com.android.sparksoft.smartguardwatch.Models;

/**
 * Created by Daniel on 10/18/2015.
 */
public class Place {

    private int placeId;
    private String placeName;
    private String placeLat;
    private String placeLong;


    public Place()
    {

    }

    public Place(int _placeId, String _placeName, String _placeLat, String _placeLong)
    {
        placeId = _placeId;
        placeName = _placeName;
        placeLat = _placeLat;
        placeLong = _placeLong;
    }

    public int getId()
    {
        return placeId;
    }

    public String getPlaceName()
    {
        return placeName;
    }
    public String getPlaceLat()
    {
        return placeLat;
    }
    public String getPlaceLong()
    {
        return placeLong;
    }




}
