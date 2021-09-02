import java.io.IOException;
import java.util.List;

import io.restassured.internal.common.assertion.Assertion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import retrofit.api.MiniMarketApi;
import retrofit.dto.Category;
import retrofit.dto.ProductDto;
import retrofit.utils.RetrofitGetter;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ApiTest {

    private final MiniMarketApi api;

    public ApiTest() throws IOException {
        Retrofit retrofit = new RetrofitGetter().getInstance();
        api = retrofit.create(MiniMarketApi.class);
    }

    @Test
    @DisplayName("Создание продукта")
    void testCreateProduct() throws IOException {

        ProductDto dto = ProductDto.builder()
                .title("Potato")
                .categoryTitle(Category.FOOD.getTitle())
                .price(25L)
                .build();

        Response<ProductDto> response = api.createProduct(dto).execute();
        Long id = response.body().getId();

        ProductDto actually = api.getProduct(id).execute().body();

        assertEquals(dto.getTitle(), actually.getTitle());
        assertEquals(dto.getPrice(), actually.getPrice());
        assertEquals(dto.getCategoryTitle(), actually.getCategoryTitle());

        api.deleteProduct(id).execute();
    }

    @Test
    @DisplayName("Создание продукта с пустым id")
    void testCreateProductWithNullId() throws IOException {

        ProductDto dto = ProductDto.builder()
                .id(0L)
                .title("Carrot")
                .categoryTitle(Category.FOOD.getTitle())
                .price(30L)
                .build();

        Response<ProductDto> response = api.createProduct(dto).execute();

        new AssertionError("Id must be null for new entity");
    }

    @Test
    @DisplayName("Получение продукта")
    void testGetProduct() throws IOException {

        ProductDto dto = ProductDto.builder()
                .id(2L)
                .title("Bread")
                .categoryTitle(Category.FOOD.getTitle())
                .price(25L)
                .build();

        Response<ProductDto> response = api.getProduct(dto.getId()).execute();
        Long id = response.body().getId();

        ProductDto actually = api.getProduct(id).execute().body();

        assertEquals(dto.getTitle(), actually.getTitle());
        assertEquals(dto.getPrice(), actually.getPrice());
        assertEquals(dto.getCategoryTitle(), actually.getCategoryTitle());

    }

    @Test
    @DisplayName("Получение продукта с несуществующим id")
    void testGetProductWithWrongId() throws IOException {

        ProductDto dto = ProductDto.builder()
                .id(10L)
                .title("Milk")
                .categoryTitle(Category.FOOD.getTitle())
                .price(95L)
                .build();

        Response<ProductDto> response = api.getProduct(dto.getId()).execute();

        new AssertionError("Unable to find product with id: " + dto.getId());
    }

    @Test
    @DisplayName("Изменение продукта")
    void testUpdateProduct() throws IOException {

        ProductDto dto = ProductDto.builder()
                .id(2L)
                .title("Bread")
                .categoryTitle(Category.FOOD.getTitle())
                .price(45L)
                .build();

        Response<ProductDto> response = api.updateProduct(dto).execute();
        Long id = response.body().getId();

        ProductDto actually = api.getProduct(id).execute().body();

        assertEquals(dto.getPrice(), actually.getPrice());
    }

    @Test
    @DisplayName("Изменение продукта со значением null в id")
    void testUpdateProductWithNullId() throws IOException {

        ProductDto dto = ProductDto.builder()
                .id(null)
                .title("Bread")
                .categoryTitle(Category.FOOD.getTitle())
                .price(45L)
                .build();

        Response<ProductDto> response = api.updateProduct(dto).execute();

        new AssertionError("Id must be not null for new entity");
    }
    @Test
    @DisplayName("Изменение продукта с несуществующим id")
    void testUpdateProductWithWrongId() throws IOException {

        ProductDto dto = ProductDto.builder()
                .id(10L)
                .title("Bread")
                .categoryTitle(Category.FOOD.getTitle())
                .price(45L)
                .build();

        Response<ProductDto> response = api.updateProduct(dto).execute();

        new AssertionError("Product with id: \" + p.getId() + \" doesn't exist");
    }

    @Test
    @DisplayName("Удаление продукта")
    void testDeleteProduct() throws IOException {

        ProductDto dto = ProductDto.builder()
                .title("Potato")
                .categoryTitle(Category.FOOD.getTitle())
                .price(25L)
                .build();

        Response<ProductDto> response = api.createProduct(dto).execute();

        Long id = response.body().getId();

        api.deleteProduct(id).execute();

        api.getProduct(id).execute();

        new AssertionError("Unable to find product with id: " + dto.getId());
    }
}


