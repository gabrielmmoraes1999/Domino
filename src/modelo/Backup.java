package modelo;

import java.sql.Timestamp;

/**
 *
 * @author Gabriel Moraes
 */
public class Backup {
    private int id;
    private Timestamp data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getData() {
        return data;
    }

    public void setData(Timestamp data) {
        this.data = data;
    }
}
