package bean;

public class SchoolAdmin {
    private String adminID;
    private String password;
    private String name;

    public SchoolAdmin() {
    }

    public SchoolAdmin(String adminID, String password, String name) {
        this.adminID = adminID;
        this.password = password;
        this.name = name;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SchoolAdmin{" +
                "adminID='" + adminID + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
