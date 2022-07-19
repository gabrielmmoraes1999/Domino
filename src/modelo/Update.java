package modelo;

import java.sql.Date;

/**
 *
 * @author Gabriel Moraes
 */
public class Update {
    private int id;
    private int codPublic;
    private String version;
    private String link;
    private Date dateRelease;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCodPublic() {
        return codPublic;
    }

    public void setCodPublic(int codPublic) {
        this.codPublic = codPublic;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getDateRelease() {
        return dateRelease;
    }

    public void setDateRelease(Date dateRelease) {
        this.dateRelease = dateRelease;
    }
}
