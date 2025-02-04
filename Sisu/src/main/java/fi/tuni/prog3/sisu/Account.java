package fi.tuni.prog3.sisu;

/**
 * This class stores user account.
 * @author shuang.fan@tuni.fi
 */
public class Account {
    private String id;
    private String user;
    private String password;
    
    /**
     * Constructs an Account object that is initialized with the id, user and passowrd.
     * @param id the student number.
     * @param user the user name.
     * @param password the user password.
     */
    public Account(String id, String user, String password) {
        this.id = id;
        this.user = user;
        this.password = password;
    }
    
    /**
     * Set the student number by the given id.
     * @param id the given student number.
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Set the user name by the given input.
     * @param user the given user name.
     */
    public void setUser(String user) {
        this.user = user;
    }
    
    /**
     * Set the user password by the given input.
     * @param password the given user password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Get the student number.
     * @return the student number as String.
     */
    public String getId() {
        return this.id;
    }
    
    /**
     * Get the user name.
     * @return the user name.
     */
    public String getUser() {
        return this.user;
    }
    
    /**
     * Get the user password.
     * @return the user password.
     */
    public String getPassword() {
        return this.password;
    }
}
