package ir.piana.dev.sample.model;

/**
 * @author Mohammad Rahmati, 6/3/2017 1:40 PM
 */
public class UserModel {
    private String userName;
    private String userPass;

    public UserModel() {
    }

    public UserModel(String userName, String userPass) {
        this.userName = userName;
        this.userPass = userPass;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
}
