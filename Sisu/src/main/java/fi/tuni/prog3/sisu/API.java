package fi.tuni.prog3.sisu;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URL;
 
/**
 * API class is used to fetch data from Kori Api.
 * This class impelents iAPI interface.
 * @author heidi.seppi@tuni.fi
 */
public class API implements iAPI {
    private final String MODULE_URL = "https://sis-tuni.funidata.fi/kori/api/modules/";
    private final String UTA_TUT_URL_PART1 = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=";
    private final String UTA_TUT_URL_PART2 = "&universityId=tuni-university-root-id";
    private final String COURSEUNIT_URL_PART1 = "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=";

    /**
    * Fetches all main degree's data from Kori Api.
    * @param urlString URL address in which the main data will be fetched.
    * @return Returns JsonObject which has Json formed data from Kori Api.
    */
    @Override
    public JsonObject getJsonObjectFromApi(String urlString) {
        try {
            URL url = new URL(urlString);
            String data = new String(url.openStream().readAllBytes());
            JsonElement jsonElement = new JsonParser().parseString(data);
            JsonObject jsonObject = null;
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                jsonObject = jsonArray.get(0).getAsJsonObject();
            }
            else if (jsonElement.isJsonObject()) {
                jsonObject = jsonElement.getAsJsonObject();
            }
            return jsonObject;
        } catch (IOException e) {
            // Return null if IOexception is thrown.
            return null;
        }  
    }
    
    /**
    * Fetches module data with module's groupId from Kori Api.
    * @param groupId Module's groupId.
    * @return Returns JsonObject which has Json formed data from Kori Api.
    */
    public JsonObject getModuleObjectFromApi(String groupId) {
        JsonObject moduleObject;
        if (groupId.contains("otm")) {
            moduleObject = getJsonObjectFromApi(MODULE_URL + groupId);
        }
        else {
            moduleObject = getJsonObjectFromApi(UTA_TUT_URL_PART1 + groupId + UTA_TUT_URL_PART2);
        }
        return moduleObject;
    }
    
    /**
    * Fetches courseUnit's data from Kori Api.
    * @param groupId CourseUnit's groupId.
    * @return Returns JsonObject which has Json formed data from Kori Api.
    */
    public JsonObject getCourseUnitObjectFromApi(String groupId) {
        JsonObject moduleObject = getJsonObjectFromApi(COURSEUNIT_URL_PART1 + groupId + UTA_TUT_URL_PART2);
        
        return moduleObject;
    }
}
