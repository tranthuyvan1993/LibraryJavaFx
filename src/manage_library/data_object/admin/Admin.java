package manage_library.data_object.admin;

import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = false)

public class Admin{
    private Integer id;
    private int status, two_auth, index;
    private String fullname, username, phone, password, email, role;
    private String avatar;
    public Admin() {
    }

    public Admin(String fullname, String username, String phone, String password, String email, String role, int status, int two_auth, String avatar) {
        this.status = status;
        this.fullname = fullname;
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.email = email;
        this.role = role;
        this.two_auth = two_auth;
        this.avatar = avatar;
    }

    public Admin(String fullname, String phone, String role, int status, int two_auth, String avatar) {
        this.fullname = fullname;
        this.status = status;
        this.phone = phone;
        this.two_auth = two_auth;
        this.avatar = avatar;
    }
    public Admin(String password) {
        this.password = password;
    }

    public int getTwo_auth() {
        return two_auth;
    }

    public String getavatar() {
        return avatar;
    }

    public void setavatar(String avatar) {
        this.avatar = avatar;
    }

    public void setTwo_auth(int two_auth) {
        this.two_auth = two_auth;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "index=" + index +
                ", id=" + id +
                ", status=" + status +
                ", two_auth=" + two_auth +
                ", fullname='" + fullname + '\'' +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
