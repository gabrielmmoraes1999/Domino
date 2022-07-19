package modelo;

/**
 *
 * @author Gabriel Moraes
 */
public class Computer {
    private int id;
    private String manufacturer;
    private String name;
    private String serialNumber;
    private String version;
    private String serialNumberCpu;
    private String nameCpu;
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSerialNumberCpu() {
        return serialNumberCpu;
    }

    public void setSerialNumberCpu(String serialNumberCpu) {
        this.serialNumberCpu = serialNumberCpu;
    }

    public String getNameCpu() {
        return nameCpu;
    }

    public void setNameCpu(String nameCpu) {
        this.nameCpu = nameCpu;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
