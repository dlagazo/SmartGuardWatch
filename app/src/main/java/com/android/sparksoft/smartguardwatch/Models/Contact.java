package com.android.sparksoft.smartguardwatch.Models;

import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Daniel on 10/18/2015.
 */
public class Contact {

    private int contactId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String relation;
    private int rank, type;
    private String schedule;
    private int callOutside;

    public Contact()
    {

    }

    public Contact(int _contactId, String _firstName, String _lastName, String _email, String _mobile,
                   String _relation, int _rank, String _schedule, int _type, int _canCallOutside)
    {
        contactId = _contactId;
        firstName = _firstName;
        lastName = _lastName;
        email = _email;
        mobile = _mobile;
        relation = _relation;
        rank = _rank;
        schedule = _schedule;
        type = _type;
        callOutside = _canCallOutside;
    }

    public int getId()
    {
        return contactId;
    }

    public String getFirstName()
    {
        return firstName;
    }
    public String getLastName()
    {
        return lastName;
    }
    public String getEmail()
    {
        return email;
    }
    public String getMobile()
    {
        return mobile;
    }
    public String getRelation()
    {
        return relation;
    }


    public int getRank()
    {
        return rank;
    }
    public String getSchedule() {return schedule;}
    public int getType() { return type; }
    public String getFullName()
    {
        if(firstName.toLowerCase().equals("null"))
            firstName = "";
        if(lastName.toLowerCase().equals("null"))
            lastName = "";
        return firstName + " " + lastName;
    }

    public int getCallOutside()
    {


        return callOutside;
    }

    public String getContactDetails()
    {
        return getFullName() + " canContactOutside:" + getCallOutside();
    }

    public Boolean isInSchedule(String sched, int hour)
    {
        String[] getTimes = sched.split(" ");
        int startHr = 0, endHr = 0;

        for (String str:getTimes)
        {
            if(str.contains("-"))
            {
                String[] times = str.split("-");
                String strStartHr = times[0].split(":")[0];
                String strEndHr = times[1].split(":")[0];
                startHr = Integer.parseInt(strStartHr);
                endHr = Integer.parseInt(strEndHr);
                //Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
                Log.d("LOG_CONTACT","Start hr: " + strStartHr);
                Log.d("LOG_CONTACT", "End hr: " + strEndHr);

                if((hour >= startHr && hour < endHr) || (hour > startHr && hour <= endHr) )
                {
                    return true;

                }
                else
                    return false;
            }
        }
        return false;

    }

    public Boolean canCall()
    {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        Log.d("LOG_CONTACT", "Day of the week: " + day);
        Log.d("LOG_CONTACT", hour + ":" + min);


            String[] parsedSched = getSchedule().split(",");

            if(getSchedule() == "")
            {
                return true;
            }

            for(String sched:parsedSched)
            {
                try {
                    //sched.replaceAll(" ", "");
                    if(day == 1)
                    {
                        if(sched.contains("SUN"))
                        {
                            if(isInSchedule(sched, hour))
                            {
                                Log.d("LOG_CONTACT", "Call " + getFullName());
                            }

                        }
                    }
                    else if(day == 2)
                    {
                        if(sched.contains("MON"))
                        {
                            if(isInSchedule(sched, hour))
                            {
                                Log.d("LOG_CONTACT", "Call " + getFullName());
                            }
                        }
                    }
                    else if(day == 3)
                    {
                        if(sched.contains("TUE"))
                        {
                            if(isInSchedule(sched, hour))
                            {
                                Log.d("LOG_CONTACT", "Call " + getFullName());
                            }


                        }
                    }

                }
                catch (Exception ex) {

                }
            }

        return true;
    }
}
