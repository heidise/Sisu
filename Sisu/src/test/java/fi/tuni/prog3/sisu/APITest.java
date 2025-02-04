/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package fi.tuni.prog3.sisu;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author venla
 */
public class APITest {
    
    public APITest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }

    /**
     * Test of getJsonObjectFromApi method, of class API.
     */
    @Test
    public void testGetJsonObjectFromApi() {
        System.out.println("getJsonObjectFromApi");
        String urlString = "https://sis-tuni.funidata.fi/kori/api/module-search?curriculumPeriodId=uta-lvv-2021&universityId=tuni-university-root-id&moduleType=DegreeProgramme&limit=1000";
        API instance = new API();
        JsonObject result = instance.getJsonObjectFromApi(urlString);
        // Assert that the JSON object is not null.
        assertNotNull(result);
    }

    /**
     * Test of getModuleObjectFromApi method, of class API.
     */
    @Test
    public void testGetModuleObjectFromApi() {
        System.out.println("getModuleObjectFromApi");
        String groupId = "otm-87fb9507-a6dd-41aa-b924-2f15eca3b7ae";
        API instance = new API();
        JsonObject result = instance.getModuleObjectFromApi(groupId);
        // Assert that the JSON object is not null.
        assertNotNull(result);
    }
    
}
