package com.example.android.popmovies.app;

/**
 * Created by hengyang on 07/11/15.
 */
public class Movie {

    private String posterpath;
    private double rating;

    public Movie(String posterpath, double rating){
        super();
        this.posterpath = posterpath;
        this.rating = rating;
    }

    public String getPosterpath(){
        return posterpath;
    }

    public double getRating(){
        return rating;
    }
}
