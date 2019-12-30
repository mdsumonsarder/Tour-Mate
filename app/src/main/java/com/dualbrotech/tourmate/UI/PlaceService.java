package com.dualbrotech.tourmate.UI;


import com.dualbrotech.tourmate.WebResponses.PlaceResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Arif Rahman on 1/16/2018.
 */

public interface PlaceService {
    @GET()
    Call<PlaceResponse> getplaceResponse(@Url String urlString);
}
