package model;

public class Courier {

    // опиши поля
    private String login;
    private String password;
    private String firstName;


    public Courier(String name, String password, String firstName) {
        this.login = name;
        this.password = password;
        this.firstName = firstName;
    }

    public Courier(String name, String password) {
        this.login = name;
        this.password = password;
    }

    public Courier () {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {

        this.login = login;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

}
