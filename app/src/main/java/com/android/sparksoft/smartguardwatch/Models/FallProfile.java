package com.android.sparksoft.smartguardwatch.Models;

/**
 * Created by Daniel on 10/18/2015.
 */
public class FallProfile {

    private int id;
    private double fallUpperThreshold;
    private double fallLowerThreshold;
    private double fallWindowDuration;
    private int fallCountLower;
    private int fallCountUpper;
    private double residualThreshold;
    private double residualWindowDuration;
    private boolean isActive;
    private String description;



    public FallProfile()
    {

    }

    public FallProfile(int _id, Double _fallUpperThresh, Double _fallLowerThresh, Double _fallWindowDuration,
                       int _fallUpperCount, int _fallLowerCount, Double _residualThresh, Double _residualWindowDuration,
                       Boolean _isActive, String _description)
    {
        id = _id;
        fallUpperThreshold = _fallUpperThresh;
        fallLowerThreshold = _fallLowerThresh;
        fallWindowDuration = _fallWindowDuration;

    }






}
