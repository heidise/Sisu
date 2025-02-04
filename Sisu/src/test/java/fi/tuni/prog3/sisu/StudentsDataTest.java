/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package fi.tuni.prog3.sisu;

import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author venla
 */
public class StudentsDataTest {
    
    public StudentsDataTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    

    /**
     * Test of fileExists method, of class StudentsData.
     */
    @Test
    public void testFileExists() {
        System.out.println("fileExists");
        Boolean expResult = true;
        Boolean result = StudentsData.fileExists();
        assertEquals(expResult, result);
    }

    /**
     * Test of readFromFile method, of class StudentsData.
     */
    @Test
    public void testReadFromFile() throws Exception {
        System.out.println("readFromFile");
        ArrayList<Student> result = StudentsData.readFromFile();
        System.out.println("Result: " + result);
        assertNotNull(result);
    }

    
}
