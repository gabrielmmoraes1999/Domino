package modelo;

import java.sql.Date;

/**
 *
 * @author Gabriel Moraes
 */
public class RubricaConfiguracao {
    private int id;
    private Conversao conversao;
    private String codigo;
    private int codEmpresa;
    private String descricao;
    private Date inicioData;
    private boolean situacao;
    private Date situacaoData;
    private int tipo;
    private boolean avisoPrevio;
    private boolean salarioFerias;
    private boolean licencaPremio;
    private boolean fichaFinanceira;
    private boolean dirf;
    private boolean rais;
    private boolean avisoPrevioMedias;
    private boolean salarioMedias;
    private boolean feriasMedias;
    private boolean licencaPremioMedias;
    private boolean saldoSalarioMedias;
    private boolean horas;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Conversao getConversao() {
        return conversao;
    }

    public void setConversao(Conversao conversao) {
        this.conversao = conversao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(int codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getInicioData() {
        return inicioData;
    }

    public void setInicioData(Date inicioData) {
        this.inicioData = inicioData;
    }

    public boolean isSituacao() {
        return situacao;
    }

    public void setSituacao(boolean situacao) {
        this.situacao = situacao;
    }

    public Date getSituacaoData() {
        return situacaoData;
    }

    public void setSituacaoData(Date situacaoData) {
        this.situacaoData = situacaoData;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public boolean isAvisoPrevio() {
        return avisoPrevio;
    }

    public void setAvisoPrevio(boolean avisoPrevio) {
        this.avisoPrevio = avisoPrevio;
    }

    public boolean isSalarioFerias() {
        return salarioFerias;
    }

    public void setSalarioFerias(boolean salarioFerias) {
        this.salarioFerias = salarioFerias;
    }

    public boolean isLicencaPremio() {
        return licencaPremio;
    }

    public void setLicencaPremio(boolean licencaPremio) {
        this.licencaPremio = licencaPremio;
    }

    public boolean isFichaFinanceira() {
        return fichaFinanceira;
    }

    public void setFichaFinanceira(boolean fichaFinanceira) {
        this.fichaFinanceira = fichaFinanceira;
    }

    public boolean isDirf() {
        return dirf;
    }

    public void setDirf(boolean dirf) {
        this.dirf = dirf;
    }

    public boolean isRais() {
        return rais;
    }

    public void setRais(boolean rais) {
        this.rais = rais;
    }

    public boolean isAvisoPrevioMedias() {
        return avisoPrevioMedias;
    }

    public void setAvisoPrevioMedias(boolean avisoPrevioMedias) {
        this.avisoPrevioMedias = avisoPrevioMedias;
    }

    public boolean isSalarioMedias() {
        return salarioMedias;
    }

    public void setSalarioMedias(boolean salarioMedias) {
        this.salarioMedias = salarioMedias;
    }

    public boolean isFeriasMedias() {
        return feriasMedias;
    }

    public void setFeriasMedias(boolean feriasMedias) {
        this.feriasMedias = feriasMedias;
    }

    public boolean isLicencaPremioMedias() {
        return licencaPremioMedias;
    }

    public void setLicencaPremioMedias(boolean licencaPremioMedias) {
        this.licencaPremioMedias = licencaPremioMedias;
    }

    public boolean isSaldoSalarioMedias() {
        return saldoSalarioMedias;
    }

    public void setSaldoSalarioMedias(boolean saldoSalarioMedias) {
        this.saldoSalarioMedias = saldoSalarioMedias;
    }

    public boolean isHoras() {
        return horas;
    }

    public void setHoras(boolean horas) {
        this.horas = horas;
    }
}