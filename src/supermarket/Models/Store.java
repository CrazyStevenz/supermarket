package supermarket.Models;

public class Store {
    private int id;
    private String address;
    private String phone;
    private String work_hours;

    public Store(int id, String address, String phone, String work_hours) {
        this.id = id;
        this.address = address;
        this.phone = phone;
        this.work_hours = work_hours;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWorkHours() {
        return work_hours;
    }

    public void setWorkHours(String work_hours) {
        this.work_hours = work_hours;
    }
}
