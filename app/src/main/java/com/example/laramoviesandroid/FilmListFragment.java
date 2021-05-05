package com.example.laramoviesandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.laramoviesandroid.models.Film;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FilmListFragment extends Fragment {
    protected RecyclerView mRecyclerView;
    private Context mContext;
    private ArrayList<Film> mFilms;
    private FilmListAdapter mFilmAdapter;
    private FragmentManager mParentFragmentManager;

    public FilmListFragment(FragmentManager fm) {
        super(R.layout.fragment_film_list);
        this.mParentFragmentManager = fm;
        Log.i(null, "Film List Fragment created");
    }

    private void sampleFilms() {
        for(int i = 0; i < 100; i++) {
            this.mFilms.add(
                    Film.builder()
                            .setTitle("qwe")
                            .setReleaseDate(new Date())
                            .setGenre(1)
                            .setDuration(180)
                            .build()
            );

            this.mFilms.add(

                    Film.builder()
                            .setTitle("asd")
                            .setReleaseDate(new Date())
                            .setGenre(1)
                            .setDuration(360)
                            .build()
            );
            this.mFilms.add(

                    Film.builder()
                            .setTitle("zxc")
                            .setReleaseDate(new Date())
                            .setGenre(1)
                            .setDuration(540)
                            .build()
            );
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_film_list, container, false);
        this.mFilms = new ArrayList<Film>();
//        this.sampleFilms();
        this.setMemberVariables(v);
        this.getFilmsAndRender();
        return v;

    }

    protected void getFilmsAndRender(){
        String fullRequestURI = getString(R.string.api_url) + "films";
        URL requestURLObject;
        try {
            requestURLObject= new URL(fullRequestURI);
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        Log.i("getfilmsuri", "request URI: "+fullRequestURI);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                fullRequestURI,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("listquery", response.toString());
                        try {
                            JSONArray filmsJSON = response.getJSONArray("films");

                            for(int i = 0; i < filmsJSON.length(); i++) {
                                JSONObject currentFilm = filmsJSON.getJSONObject(i);
                                Log.i("current_film", "current film: " + currentFilm.toString());
//                                parse genre first, to handle null values;
                                String genre;
                                int genreId;
                                try {
                                    genre = currentFilm.getJSONObject("genre").getString("genre");
                                } catch (JSONException e) {
                                    genre = "None";
                                }
                                Log.i(null, "genre id:" + Integer.toString(currentFilm.getInt("genre_id")));

                                Film newFilm = Film.builder(currentFilm.getInt("id"))
                                        .setTitle(currentFilm.getString("film_title"))
                                        .setDuration(currentFilm.getInt("duration"))
                                        .setGenre(currentFilm.getInt("genre_id"))
                                        .setGenre(genre)
                                        .setReleaseDate(
                                                new SimpleDateFormat("yyyy-MM-dd")
                                                        .parse(currentFilm.getString("release_date"))
                                        )
                                        .setPosterURL(currentFilm.getString("poster"))
                                        .setAdditionalInfo(currentFilm.getString("additional_info"))
                                        .setStory(currentFilm.getString("story"))
                                        .build();

                                newFilm.setJson(currentFilm);
                                mFilms.add(newFilm);
                                mFilmAdapter.notifyDataSetChanged();

                                Log.i (null, "new film title:"+ newFilm.getTitle());
                            }

                        } catch (JSONException | ParseException e ) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("volleyError", error.toString());
                    }
                }
        );

        RequestQueue requestQueue= Volley.newRequestQueue(mContext);
        requestQueue.add(request);
    }
    protected void setMemberVariables(View v){
        mContext = getContext();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_film_list);
        mFilmAdapter = new FilmListAdapter(mFilms, mParentFragmentManager);

        // set recycler view configuration
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mRecyclerView.setAdapter(mFilmAdapter);

    }

}