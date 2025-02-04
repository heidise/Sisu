package fi.tuni.prog3.sisu;

import com.google.gson.JsonObject;

/**
 * Interface for extracting data from the Sisu API.
 */
public interface iAPI {
    /**
     * Returns a JsonObject that is extracted from the Sisu API.
     * @param urlString URL for retrieving information from the Sisu API.
     * @return JsonObject.
     */
    JsonObject getJsonObjectFromApi(String urlString);    
}
