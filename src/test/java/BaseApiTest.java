import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class BaseApiTest {

    private final String token;
    private final String baseUri;
    private final String userName;
    private final PropertyScanner scanner;

    public BaseApiTest() throws IOException {
        scanner = new PropertyScanner();
        token = scanner.getProperty("imgur.auth.token");
        baseUri = scanner.getProperty("imgur.api.url");
        userName = scanner.getProperty("imgur.username");
    }

    public String getToken() {
        return token;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public String getUserName() {
        return userName;
    }

    public PropertyScanner getScanner() {
        return scanner;
    }
}
