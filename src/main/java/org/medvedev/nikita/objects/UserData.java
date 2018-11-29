package org.medvedev.nikita.objects;

public class UserData {
    private String login, firstName, secondName, email;

    public String getLogin() {
        return login;
    }

    public UserData setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserData setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getSecondName() {
        return secondName;
    }

    public UserData setSecondName(String secondName) {
        this.secondName = secondName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserData setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        return "Login: "+login+", first name: " + firstName+
                ", second name: "+secondName+", email: "+ email;
    }
}
