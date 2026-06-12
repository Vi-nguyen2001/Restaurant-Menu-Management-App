package fpoly.vinv01.assignmentapi.service;

import java.util.ArrayList;

import fpoly.vinv01.assignmentapi.models.Category;
import fpoly.vinv01.assignmentapi.models.Dish;
import fpoly.vinv01.assignmentapi.models.Response;
import fpoly.vinv01.assignmentapi.models.UploadResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
        @GET("dish/listDish")
        Call<Response<ArrayList<Dish>>> getDishes();

        @POST("dish/add")
        Call<Response<Dish>> addDish(@Body Dish dish);

        @PUT("dish/update/{id}")
        Call<Response<Dish>> updateDish(@Path("id") String id, @Body Dish dish);

        @DELETE("dish/delete/{id}")
        Call<Response<Dish>> deleteDish(@Path("id") String id);

        @Multipart
        @POST("upload/upFile")
        Call<UploadResponse> uploadImage(@Part MultipartBody.Part image);

        @GET("category/listCategory")
        Call<Response<ArrayList<Category>>> getCategories();

        @POST("category/add")
        Call<Response<Category>> addCategory(@Body Category category);

        @PUT("category/update/{id}")
        Call<Response<Category>> updateCategory(@Path("id") String id, @Body Category category);

        @DELETE("category/delete/{id}")
        Call<Response<Category>> deleteCategory(@Path("id") String id);
}
