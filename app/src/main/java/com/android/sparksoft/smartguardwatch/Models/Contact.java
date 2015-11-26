package com.android.sparksoft.smartguardwatch.Models;

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
    private int rank;
    private String schedule;

    public Contact()
    {

    }

    public Contact(int _contactId, String _firstName, String _lastName, String _email, String _mobile, String _relation, int _rank, String _schedule)
    {
        contactId = _contactId;
        firstName = _firstName;
        lastName = _lastName;
        email = _email;
        mobile = _mobile;
        relation = _relation;
        rank = _rank;
        schedule = _schedule;
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

    public String getFullName()
    {
        if(firstName.toLowerCase().equals("null"))
            firstName = "";
        if(lastName.toLowerCase().equals("null"))
            lastName = "";
        return firstName + " " + lastName;
    }
}
