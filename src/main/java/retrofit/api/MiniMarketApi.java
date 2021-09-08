package retrofit.api;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit.dto.ProductDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MiniMarketApi {

    @GET("/market/api/v1/products")
    Call<List<ProductDto>> getProducts();

    @GET("/market/api/v1/products/{id}")
    Call<ProductDto> getProduct(@Path("id") long id);

    @POST("/market/api/v1/products")
    Call<ProductDto> createProduct(@Body ProductDto product);

    @PUT("/market/api/v1/products")
    Call<ProductDto> updateProduct(@Body ProductDto product);

    @DELETE("/market/api/v1/products/{id}")
    Call<ResponseBody> deleteProduct(@Path("id") long id);

}
