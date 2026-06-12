package fpoly.vinv01.assignmentapi.service;

import retrofit2.converter.gson.GsonConverterFactory;

public class HttpRequest {

    private ApiService service;

    public static final String BASE_URL = "http://10.24.7.243:3000/";

    public HttpRequest() {
        service = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    public ApiService callApi() {
        return service;
    }

}
