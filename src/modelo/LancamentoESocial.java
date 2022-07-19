package modelo;

/**
 *
 * @author Gabriel Moraes
 */
public class LancamentoESocial {
    private String periodo;
    private String cnpjSimples;
    private String cnpjCompleto;
    private String CPF;
    private String matricula;
    private String codRubrica;
    private String referencia;
    private String valor;

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getCnpjSimples() {
        return cnpjSimples;
    }

    public void setCnpjSimples(String cnpjSimples) {
        this.cnpjSimples = cnpjSimples;
    }

    public String getCnpjCompleto() {
        return cnpjCompleto;
    }

    public void setCnpjCompleto(String cnpjCompleto) {
        this.cnpjCompleto = cnpjCompleto;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCodRubrica() {
        return codRubrica;
    }

    public void setCodRubrica(String codRubrica) {
        this.codRubrica = codRubrica;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
