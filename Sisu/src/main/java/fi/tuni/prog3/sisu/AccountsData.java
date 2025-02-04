package fi.tuni.prog3.sisu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains methods about accounts' actions.
 * @author shuang.fan@tuni.fi
 */
public class AccountsData {
    /**
     * Take the error message.
     */
    public static String ErrorMessage;
    
    /**
     * The legal user name rule.
     */
    public static final String VALIDUSER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_-.";
    
    /**
     * The legal password rule.
     */    
    public static final String VALIDPASSWORD = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_-!";
    
    /**
     * The legal student number rule.
     */
    public static final String VALIDID = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    /**
     * This method is used to add a new account to accounts.txt.
     * <p>Format: {id}:{user name}:{password}</p>
     * @param user The new user's name.
     * @param password The new user's password.
     * @throws IOException The file doesn't exist or has other IO error.
     */
    public static void addNewUser(String user, String password, String id) throws IOException {
        if (App.fileExists()) {
            // Check the user doesn't exists.
            checkUser(user, password);
            if (ErrorMessage.equals("user")) {
                try (var file = new BufferedWriter(new FileWriter(App.FILENAME, true))) {
                    file.write(String.format("\n%s:%s:%s", id, user, password));
                    file.close();
                }
                catch (IOException e) {
                    System.out.println("Error: " + e);
                }
            }
        }
        else {
            ErrorMessage = "user";
            try (var file = new BufferedWriter(new FileWriter(App.FILENAME))) {
                file.write(String.format("%s:%s:%s", id, user, password));
                file.close();
            }
            catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
        
        var student = new Student(id);
        var students = new ArrayList<Student>();
        if (StudentsData.fileExists()) {
            students = StudentsData.readFromFile();
        }
        students.add(student);
        StudentsData.writeToFile(students);
        var filename = String.format("src/main/students/%s.txt", id);
        var newEmptyFile = new File(filename);
        newEmptyFile.createNewFile();
    }
    
    /**
     * Check the account's inputs have errors or not. 
     * <p>Set error message as "user", if the user doesn't exist.</p>
     * <p>Set error message as "password", if the password is incorrect.</p>
     * <p>Set error message as "", if the user and password are all correct.</p>
     * @param user The input of user name.
     * @param password The input of user password.
     * @return The id of account.
     * @throws IOException The file has any IO error.
     */
    public static String checkUser(String user, String password) throws IOException {
        var accounts = readFile();
        for (var account : accounts) {
            if (account.getUser().equals(user)) {
                if (account.getPassword().equals(password)) {
                    ErrorMessage = "";
                    System.out.println("The user name and password are valid. :)");
                    return account.getId();
                }
                else {
                    ErrorMessage = "password";
                    System.out.println("The account password is incorrect!");
                }
            }
            else {
                ErrorMessage = "user";
                System.out.println("The account doesn't exist!");
            }
        }
        return null;
    }
    
    // Read the acouunts file, and return the map<user, password>.
    // file line format is id:user:password
    // stored format is Map<user, [password, id]>
    private static List<Account> readFile() throws IOException {
        try (var file = new BufferedReader(new FileReader(App.FILENAME))) {
            var accounts = new ArrayList<Account>();
            var lines = file.lines().toArray();
            for (var l : lines) {
                var line = (String)l;
                var splits = line.split(":");
                var id = splits[0];
                var user = splits[1];
                var password = splits[2];
                var account = new Account(id, user, password);
                accounts.add(account);
            }
            file.close();
            return accounts;
        }
        catch (IOException e) {
            System.out.println("Error: " + e);
        }
        System.out.println("Cannot open "+ App.FILENAME);
        return null;
    }

    /**
     * Update the new password to accounts.txt.
     * @param newPassword the new password.
     * @throws IOException the file exception.
     */
    public static void updatePassword(String newPassword) throws IOException{
        var user = LoginController.USER;
        var accounts = readFile();
        Boolean first = true;
        
        for (var account : accounts) {
            if (account.getUser().equals(user)) {
                account.setPassword(newPassword);
            }
        }
        
        for (var account : accounts) {
            var _id = account.getId();
            var _user = account.getUser();
            var _password = account.getPassword();
            if (first) {
                try (var file = new BufferedWriter(new FileWriter(App.FILENAME))) {
                    file.write(String.format("%s:%s:%s", _id, _user, _password));
                    file.close();
                    first = false;
                }
                catch (IOException e) {
                    System.out.println("Error: " + e);
                }
            }
            else {
                try (var file = new BufferedWriter(new FileWriter(App.FILENAME, true))) {
                    file.write(String.format("\n%s:%s:%s", _id, _user, _password));
                    file.close();
                }
                catch (IOException e) {
                    System.out.println("Error: " + e);
                }
            }
        }        
    }
    
    /**
     * Check an input follows the rule or not.
     * @param input The input string, which needs to check.
     * @param rule The legal rule for input string.
     * @return True, if the input is legal.
     */
    public static Boolean isValidStr(String input, String rule) {
        Character ch;
        for (var i=0; i<input.length(); i++) {
             ch = input.charAt(i);
             if (!rule.contains(ch.toString())) {
                 return false;
             }
        }
        return true;
    }
    
    /**
     * Check a password is the same as stored one in accounts.txt.
     * @param password The given password, which need to be check.
     * @return True, if the password is same as the stored one in accounts.txt.
     * @throws IOException The file exception.
     */
    public static Boolean exists(String password) throws IOException {
        var accounts = readFile();
        var user = LoginController.USER;
        for (var account : accounts) {
            if (account.getUser().equals(user)) {
                return account.getPassword().equals(password);
            }
        }
        return false;
    }
    
    /**
     * Check the student number is unique.
     * @param num the student number as String.
     * @return true, if the student number is unique (not register before)
     * @throws IOException if the file doesn't exist
     */
    public static Boolean isUnique(String num) throws IOException {
        var studentIDs = getIDs();
        return studentIDs.isEmpty() || !studentIDs.contains(num);
    }
    
    // Get the existed ids from the accounts file.
    private static List<String> getIDs() {
        try (var file = new BufferedReader(new FileReader(App.FILENAME))) {
            var ids = new ArrayList<String>();
            var lines = file.lines().toArray();
            for (var l : lines) {
                var line = (String)l;
                var splits = line.split(":");
                ids.add(splits[2]);
            }
            file.close();
            return ids;
        }
        catch (IOException e) {
            System.out.println("Error: " + e);
        }
        System.out.println("Cannot open "+ App.FILENAME);
        return new ArrayList<>();
    }
}
