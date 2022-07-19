package modelo;

/**
 *
 * @author Gabriel Moraes
 */
public class Novidade {
    private int id;
    private String textoHtml;
    private int sysVersao;
    private boolean visualizada;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTextoHtml() {
        return textoHtml;
    }

    public void setTextoHtml(String textoHtml) {
        this.textoHtml = textoHtml;
    }

    public int getSysVersao() {
        return sysVersao;
    }

    public void setSysVersao(int sysVersao) {
        this.sysVersao = sysVersao;
    }

    public boolean isVisualizada() {
        return visualizada;
    }

    public void setVisualizada(boolean visualizada) {
        this.visualizada = visualizada;
    }
}
