package modelo;

import database.Firebird;
import database.Sybase;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import view.TelaErro;

/**
 *
 * @author Gabriel Moraes
 */
public class EmpresaDAO {
    public void inserir(ArrayList<Empresa> emp, int idConversao) {
        try {
            String sql = "INSERT INTO empresa (id_conversao, codigo, razao_social, cnpj, inicio_das_atividades) VALUES(?,?,?,?,?)";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            
            for(Empresa e : emp) {
                pstm.setInt(1, idConversao);
                pstm.setInt(2, e.getCodigo());
                pstm.setString(3, e.getRazaoSocial());
                pstm.setString(4, e.getCnpj());
                pstm.setDate(5, e.getInicioDasAtividades());
                pstm.execute();
            }
            
            Firebird.conn.commit();
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
    
    public Empresa carregarPorId(int id) {
        Empresa e = new Empresa();
        try {
            String sql = "SELECT * FROM empresa WHERE id = ? ORDER BY id ASC";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                e.setId(rs.getInt("id"));
                e.setCodigo(rs.getInt("codigo"));
                e.setRazaoSocial(rs.getString("razao_social"));
                e.setCnpj(rs.getString("cnpj"));
                e.setInicioDasAtividades(rs.getDate("inicio_das_atividades"));
            }
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return e;
    }
    
    public ArrayList<Empresa> carregarPorConversao(int idConversao) {
        ArrayList<Empresa> emp = new ArrayList<>();
        try {
            String sql = "SELECT * FROM empresa WHERE id_conversao = ? ORDER BY id ASC";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()) {
                Empresa e = new Empresa();
                e.setId(rs.getInt("id"));
                e.setCodigo(rs.getInt("codigo"));
                e.setRazaoSocial(rs.getString("razao_social"));
                e.setCnpj(rs.getString("cnpj"));
                e.setInicioDasAtividades(rs.getDate("inicio_das_atividades"));
                emp.add(e);
            }
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return emp;
    }
    
    public ArrayList<Empresa> carregarPorConversaoOrdenarPorCodEmpresa(int idConversao) {
        ArrayList<Empresa> emp = new ArrayList<>();
        try {
            String sql = "SELECT * FROM empresa WHERE id_conversao = ? ORDER BY codigo";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()) {
                Empresa e = new Empresa();
                e.setId(rs.getInt("id"));
                e.setCodigo(rs.getInt("codigo"));
                e.setRazaoSocial(rs.getString("razao_social"));
                e.setCnpj(rs.getString("cnpj"));
                e.setInicioDasAtividades(rs.getDate("inicio_das_atividades"));
                emp.add(e);
            }
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return emp;
    }
    
    public int retornaID(int codigo, int idConversao) {
        int id = 0;
        
        try {
            String sql = "SELECT * FROM empresa WHERE codigo = ? AND id_conversao = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, codigo);
            pstm.setInt(2, idConversao);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                id = rs.getInt("id");
            }
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return id;
    }
    
    public String retornaCNPJ(int codigo, int idConversao) {
        String cnpj = null;
        try {
            String sql = "SELECT * FROM empresa WHERE codigo = ? AND id_conversao = ? ORDER BY id ASC";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, codigo);
            pstm.setInt(2, idConversao);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()) {
                cnpj = rs.getString("cnpj");
            }
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return cnpj;
    }
    
    public Date retornaData(int codigo, int idConversao) {
        Date data = null;
        try {
            String sql = "SELECT * FROM empresa WHERE codigo = ? AND id_conversao = ? ORDER BY id ASC";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, codigo);
            pstm.setInt(2, idConversao);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()) {
                data = rs.getDate("inicio_das_atividades");
            }
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return data;
    }
    
    public int retornarCodigo(int idConversao, String CNPJ) {
        int codigo = 0;
        try {
            String sql = "SELECT * FROM EMPRESA WHERE ID_CONVERSAO = ? AND CNPJ = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            pstm.setString(2, CNPJ);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                codigo = rs.getInt("CODIGO");
            }
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return codigo;
    }
    
    public ArrayList<Empresa> buscarEmpresasDominio(ArrayList<String> listaCnpj) {
        ArrayList<Empresa> emp = new ArrayList<>();
        try {
            Statement stment = Sybase.conn.createStatement();
            
            //Ler a lista
            for(String cnpj : listaCnpj) {
                Empresa e = new Empresa();
                boolean one = true;
                cnpj = cnpj.replaceAll("\\.", "");
                cnpj = cnpj.replaceAll("/", "");
                cnpj = cnpj.replaceAll("-", "");

                int codEmpresa = 0;
                String cgce = null;
                String nome = null;
                Date dataInicioAtividades = null;

                //Localizar Empresas Dominio
                String qry = "SELECT * FROM bethadba.geempre WHERE cgce_emp = '"+cnpj+"'";
                ResultSet rs = stment.executeQuery(qry);
                while(rs.next()) {
                    codEmpresa = rs.getInt("codi_emp");
                    cgce = rs.getString("cgce_emp");
                    nome = rs.getString("nome_emp");

                    qry = "SELECT * FROM bethadba.geempre WHERE codi_emp = "+codEmpresa;
                    rs = stment.executeQuery(qry);
                    while(rs.next()) {
                        dataInicioAtividades = rs.getDate("dtinicio_emp");
                    }
                    one = false;
                }

                if(one) {
                    qry = "SELECT * FROM bethadba.FOVSERVICOS WHERE CGC = '"+cnpj+"'";
                    rs = stment.executeQuery(qry);
                    while(rs.next()) {
                        codEmpresa = rs.getInt("CODI_EMP");
                        cgce = rs.getString("CGC");
                        nome = rs.getString("NOME");

                        qry = "SELECT * FROM bethadba.geempre WHERE codi_emp = "+codEmpresa;
                        rs = stment.executeQuery(qry);
                        while(rs.next()) {
                            dataInicioAtividades = rs.getDate("dtinicio_emp");
                        }
                        one = false;
                    }
                }

                if(one) {
                    //Localizar Filiais Dominio com filtro
                    qry = "SELECT * FROM bethadba.fofiliais WHERE cgc = '"+cnpj+"'";
                    rs = stment.executeQuery(qry);
                    while(rs.next()) {
                        codEmpresa = rs.getInt("codi_emp");
                        cgce = rs.getString("cgc");
                        nome = rs.getString("nome");

                        qry = "SELECT * FROM bethadba.geempre WHERE codi_emp = "+codEmpresa;
                        rs = stment.executeQuery(qry);
                        while(rs.next()) {
                            dataInicioAtividades = rs.getDate("dtinicio_emp");
                        }
                    }
                }
                
                e.setCodigo(codEmpresa);
                e.setRazaoSocial(nome);
                if(cgce == null) {
                    e.setCnpj(cnpj);
                } else {
                    e.setCnpj(cgce);
                }
                
                e.setInicioDasAtividades(dataInicioAtividades);
                emp.add(e);
                //gravarArq.println(codEmpresa+"|"+nome+"|"+cgce+"|"+dataInicioAtividades);
            }
        } catch (SQLException ex) {
            new TelaErro(9, ex.getStackTrace()).setVisible(true);
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        
        return emp;
    }
    
    public void deletar(int idConversao) {
        try {
            String sql = "DELETE FROM empresa WHERE id_conversao = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            pstm.execute();
            Firebird.conn.commit();
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
    
    public boolean verificarRubricasDaEmpresa(String codEmpresa) {
        boolean verificar = false;
        try {
            Sybase sb = new Sybase();
            sb.connect();
            Statement stment = Sybase.conn.createStatement();
            String qry = "SELECT * FROM bethadba.foparmto WHERE codi_emp = "+codEmpresa;
            ResultSet rs = stment.executeQuery(qry);
            if(rs.next()) {
                if(rs.getInt("codi_emp") == rs.getInt("codi_emp_eve")) {
                    verificar = true;
                }
            }
            sb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return verificar;
    }
}
