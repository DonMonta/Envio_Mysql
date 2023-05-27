/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author monta
 */
public class Consulta_Usuarios extends Coneccion{
    public boolean Guardar(Usuarios usuarios){
        PreparedStatement ps= null;
        Connection con=(Connection)getConnection();
        String query="INSERT INTO usuarios(nombre_usuario, correo_usuario, password_usuario) "
                + "VALUES (?,?,?)";
        try {
            ps=con.prepareStatement(query);
            ps.setString(1, usuarios.getNombre());
            ps.setString(2, usuarios.getCorreo());
            ps.setString(3, usuarios.getPassword());
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Consulta_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(Consulta_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
    }
    public boolean Login(Usuarios usuarios){
        PreparedStatement ps= null;
        Connection con=(Connection)getConnection();
        ResultSet res = null;
        String query="SELECT * FROM usuarios WHERE nombre_usuario=? and password_usuario=?";
        try {
            ps=con.prepareStatement(query);
            ps.setString(1, usuarios.getNombre());
            ps.setString(2, usuarios.getPassword());
            res=ps.executeQuery();
            if(res.next()){
                usuarios.setId(res.getInt("id_usuario"));
                usuarios.setNombre(res.getString("nombre_usuario"));
                usuarios.setCorreo(res.getString("correo_usuario"));
                usuarios.setPassword(res.getString("password_usuario"));
                return true;
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(Consulta_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(Consulta_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
    }
     public boolean ExisteUsuario(Usuarios usuarios){
        PreparedStatement ps= null;
        Connection con=(Connection)getConnection();
        ResultSet res = null;
        String query="SELECT * FROM usuarios WHERE nombre_usuario=?";
        try {
            ps=con.prepareStatement(query);
            ps.setString(1, usuarios.getNombre());
            res=ps.executeQuery();
            if(res.next()){
                usuarios.setId(res.getInt("id_usuario"));
                usuarios.setNombre(res.getString("nombre_usuario"));
                usuarios.setCorreo(res.getString("correo_usuario"));
                usuarios.setPassword(res.getString("password_usuario"));
                return true;
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(Consulta_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(Consulta_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
    }
      public boolean ExisteCorreoUsuario(Usuarios usuarios){
        PreparedStatement ps= null;
        Connection con=(Connection)getConnection();
        ResultSet res = null;
        String query="SELECT * FROM usuarios WHERE correo_usuario=?";
        try {
            ps=con.prepareStatement(query);
            ps.setString(1, usuarios.getCorreo());
            res=ps.executeQuery();
            if(res.next()){
                usuarios.setId(res.getInt("id_usuario"));
                usuarios.setNombre(res.getString("nombre_usuario"));
                usuarios.setCorreo(res.getString("correo_usuario"));
                usuarios.setPassword(res.getString("password_usuario"));
                return true;
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(Consulta_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(Consulta_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
    }
    public boolean Recuperacion(Usuarios usuarios){
        PreparedStatement ps= null;
        Connection con=(Connection)getConnection();
        ResultSet res = null;
        String query="SELECT * FROM usuarios WHERE nombre_usuario=?";
        try {
            ps=con.prepareStatement(query);
            ps.setString(1, usuarios.getNombre());
            res=ps.executeQuery();
            if(res.next()){
                usuarios.setId(res.getInt("id_usuario"));
                usuarios.setNombre(res.getString("nombre_usuario"));
                usuarios.setCorreo(res.getString("correo_usuario"));
                usuarios.setPassword(res.getString("password_usuario"));
                return true;
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(Consulta_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(Consulta_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
    }
}
