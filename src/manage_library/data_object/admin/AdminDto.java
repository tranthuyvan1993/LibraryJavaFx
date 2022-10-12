package manage_library.data_object.admin;

public class AdminDto {
    private int id, index, two_auth;
    private String fullname, username, phone, email, role, avatar, status;

    public AdminDto() {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public AdminDto(int index, int id ,int two_auth, String status, String fullname, String username, String phone, String email, String role, String avatar) {
        this.index = index;
        this.id = id;
        this.two_auth = two_auth;
        this.status = status;
        this.fullname = fullname;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.avatar = avatar;
    }

    public AdminDto(int id,int two_auth, String status, String fullname, String username, String phone, String email, String role, String avatar) {
        this.id = id;
        this.two_auth = two_auth;
        this.status = status;
        this.fullname = fullname;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "AdminDto{" +
                "id=" + id +
                ", index=" + index +
                ", two_auth=" + two_auth +
                ", status=" + status +
                ", fullname='" + fullname + '\'' +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTwo_auth() {
        return two_auth;
    }

    public void setTwo_auth(int two_auth) {
        this.two_auth = two_auth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
