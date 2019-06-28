package com.example.trail2;

public class Friends  {


    public String date;

    public String getReq_type() {
        return req_type;
    }

    public void setReq_type(String req_type) {
        this.req_type = req_type;
    }

    public String req_type;



    public Friends(String date,String req_type) {
        this.date = date;
        this.req_type=req_type;
    }

    public Friends() {
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

}
