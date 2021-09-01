import java.io.IOException;
import java.util.Properties;

public class PropertyScanner {

    private final Properties properties;

    public PropertyScanner() throws IOException {
        properties = new Properties();
        properties.load(getClass().getResourceAsStream("application.properties"));
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

}