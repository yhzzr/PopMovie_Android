package com.example.android.popmovies.app;

/**
 * Created by hengyang on 07/11/15.
 */
public class InsertionSort {

    public static void sort(Movie[] movies){

        for(int i=1;i<movies.length;i++){
            for(int j=i; j>0; j--){
                if(movies[j].getRating() > movies[j-1].getRating()){
                    Movie temp;
                    temp = movies[j];
                    movies[j] = movies[j-1];
                    movies[j-1] = temp;
                }
            }
        }
    }
}
