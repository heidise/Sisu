/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package fi.tuni.prog3.sisu;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author venla
 */
public class AccountsDataTest {
    
    public AccountsDataTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }


    /**
     * Test of isValidStr method, of class Accounts.
     * Testing if isValidStr method accepts lowercase inputs
     */
    @Test
    public void testIsValidStrLower() {
        System.out.println("isValidStr Lowercase");
        String input = "abcdefg";
        String rule = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_-!";
        Boolean expResult = false;
        System.out.println("Expected:  " + expResult);
        Boolean result = AccountsData.isValidStr(input, rule);
        System.out.println("Result: " + result);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of isValidStr method, of class Accounts.
     * Testing if isValidStr method accepts uppercase inputs
     */
    @Test
    public void testIsValidStrUpper() {
        System.out.println("isValidStr Uppercase");
        String input = "ABCDEFG";
        String rule = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_-!";
        Boolean expResult = true;
        System.out.println("Expected:  " + expResult);
        Boolean result = AccountsData.isValidStr(input, rule);
        System.out.println("Result: " + result);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of isValidStr method, of class Accounts.
     * Testing if isValidStr method accepts numeral inputs
     */
    @Test
    public void testIsValidStrNumer() {
        System.out.println("isValidStr Numeric");
        String input = "1234567";
        String rule = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_-!";
        Boolean expResult = true;
        System.out.println("Expected:  " + expResult);
        Boolean result = AccountsData.isValidStr(input, rule);
        System.out.println("Result: " + result);
        assertEquals(expResult, result);
    }
    
        /**
     * Test of isValidStr method, of class Accounts.
     * Testing if isValidStr method accepts special characters
     */
    @Test
    public void testIsValidStrSpecial() {
        System.out.println("isValidStr Special characters");
        String input = "__--!!";
        String rule = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_-!";
        Boolean expResult = true;
        System.out.println("Expected:  " + expResult);
        Boolean result = AccountsData.isValidStr(input, rule);
        System.out.println("Result: " + result);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of isValidStr method, of class Accounts.
     * Testing if isValidStr method accepts unauthorized characters
     */
    @Test
    public void testIsValidStrFalsey() {
        System.out.println("isValidStr falsey char");
        String input = "HJKLÖÄ";
        String rule = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_-!";
        Boolean expResult = false;
        System.out.println("Expected:  " + expResult);
        Boolean result = AccountsData.isValidStr(input, rule);
        System.out.println("Result: " + result);
        assertEquals(expResult, result);
    }
    
}
