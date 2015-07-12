package com.example.android.popmovies.app;

/**
 * Created by hengyang on 07/11/15.
 */
public class Movie {

    private String posterpath;
    private String title;
    private String release_date;
    private String plot;
    private double rating;

    public Movie(String posterpath, String title, String release_date, String plot, double rating){
        super();
        this.posterpath = posterpath;
        this.title = title;
        this.release_date = release_date;
        this.plot = plot;
        this.rating = rating;
    }

    public String getPosterpath()   {return posterpath;}
    public String getTitle()        {return title; }
    public String getRelease_date() {return release_date;}
    public String getPlot()         {return plot; }
    public double getRating()       {return rating;}
}
