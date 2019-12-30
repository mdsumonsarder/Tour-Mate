package com.dualbrotech.tourmate.UI;

import com.dualbrotech.tourmate.WebResponses.DirectionResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by BITM Trainer 601 on 1/17/2018.
 */

public interface DirectionService {

    @GET
    Call<DirectionResponse>getDirections(@Url String urlString);
}
