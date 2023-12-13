package base.util;

import base.ProjectSettings;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonUtil {

    public Map<String, Object> getJsonAsATableAndReturnAMap(String fileName, String key) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map> dataMap;

        dataMap = mapper.readValue(new FileReader(ProjectSettings.JSON_DATA_PATH + fileName), HashMap.class);

        for (Map.Entry<String, Map> entry : dataMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }

        return null;
    }

    public JSONObject returnOrderedJsonObject(JSONObject jsonObj) throws NoSuchFieldException, IllegalAccessException {
        Field changeMap = jsonObj.getClass().getDeclaredField("map");
        changeMap.setAccessible(true);
        changeMap.set(jsonObj, new LinkedHashMap<>());
        changeMap.setAccessible(false);

        return jsonObj;
    }

    public String generateStringFromResource(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    public boolean compare(String jsonInput1, String jsonInput2) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readTree(jsonInput1).equals(mapper.readTree(jsonInput2));
    }
}
