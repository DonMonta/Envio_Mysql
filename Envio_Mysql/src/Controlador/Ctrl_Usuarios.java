/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Consulta_Usuarios;
import Modelo.Usuarios;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

/**
 *
 * @author monta
 */
public class Ctrl_Usuarios {
    Usuarios usuarios;
    Consulta_Usuarios consulta_Usuarios;

    //Variables para la Encriptacion
    private static final String Algoritmo="AES";
    String original_valor ="mi_clave_oculta";
    // Variables para el envio de correo
    private static String remitente="soporte.tecnico.2023.dm@gmail.com";
    private static String clave_remitente="hdcjrpazyljrmkcd";
    public static String destinatario;
    private String emailTo;
    private String titulo;
    private String contenido;
    
     private Properties pro;
     private Session session;
     private MimeMessage correo;
     
     String clave;  

    public Ctrl_Usuarios(Usuarios usuarios, Consulta_Usuarios consulta_Usuarios) {
        this.usuarios = usuarios;
        this.consulta_Usuarios = consulta_Usuarios;
        
    }
    public void Iniciar(){
        pro = new Properties();
    }
    public boolean Guardar(String user,String correo,String pwd){
        usuarios.setNombre(user);
        usuarios.setCorreo(correo);
        String encriptacion = Encriptar(pwd);
        
        usuarios.setPassword(encriptacion);
        if(consulta_Usuarios.Guardar(usuarios)){
            JOptionPane.showMessageDialog(null, "Usuario Registrado");
            return true;
        }
        return false;
    }
    public SecretKeySpec generarClave(String llave){
        try {
            SecretKeyFactory factory= SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(llave.toCharArray(),"salt".getBytes(StandardCharsets.UTF_8),65536,128);
            try {
                SecretKeySpec secretKeySpec = new SecretKeySpec(factory.generateSecret(spec).getEncoded(),Algoritmo);
                return secretKeySpec;
            } catch (InvalidKeySpecException ex) {
                Logger.getLogger(Ctrl_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Ctrl_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public String Encriptar(String valor){
        try{
            SecretKeySpec secretKeySpec = generarClave(original_valor);
            Cipher cipher = Cipher.getInstance(Algoritmo);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            
            byte[] valorBytes = valor.getBytes(StandardCharsets.UTF_8);
            byte[] encrypedBytes = cipher.doFinal(valorBytes);
            return Base64.getEncoder().encodeToString(encrypedBytes);
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public String DesEncriptar(String valor){
        try{
            SecretKeySpec secretKeySpec = generarClave(original_valor);
            Cipher cipher = Cipher.getInstance(Algoritmo);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            
            byte[] valorEncriptadoBytes = Base64.getDecoder().decode(valor);
            byte[] desencrypedBytes = cipher.doFinal(valorEncriptadoBytes);
            return new String(desencrypedBytes,StandardCharsets.UTF_8);
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public boolean Login(String user,String pwd){
        usuarios.setNombre(user);
        String encriptacion = Encriptar(pwd);
        
        usuarios.setPassword(encriptacion);
        if(consulta_Usuarios.Login(usuarios)){
            JOptionPane.showMessageDialog(null, "Logeo Exitoso. Bienvenido "+user);
            return true;
        }
        else{
            
            JOptionPane.showMessageDialog(null, "Error de  Logeo");
            return false;
            
        }
    }
    
    public boolean Enviar(String user){
        usuarios.setNombre(user);
        if(consulta_Usuarios.Recuperacion(usuarios)){
           
                destinatario = usuarios.getCorreo();
                emailTo=destinatario;
                clave = usuarios.getPassword();
                titulo = "Solicitud de Recuperacion de clave";
                contenido = "Tu contraseña es: "+DesEncriptar(clave);
                
                //SMTP
                pro.put("mail.smtp.host", "smtp.gmail.com");
                pro.put("mail.smtp.ssl.trust", "smtp.gmail.com");
                pro.setProperty("mail.smtp.starttls.enable", "true");
                pro.setProperty("mail.smtp.port", "587");
                pro.setProperty("mail.smtp.user",remitente);
                pro.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
                pro.setProperty("mail.smtp.auth", "true");
                
                session = Session.getDefaultInstance(pro);
                
                
                try {
                    correo = new MimeMessage(session);
                    correo.setFrom(new InternetAddress(remitente));
                    correo.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
                    correo.setSubject(titulo);
                    correo.setText(contenido,"ISO-8859-1","html");
                 } catch (Exception ex) {
                       ex.printStackTrace();
                        return false;
                }
                Send();
               
                return true;
               
            
            
        }
        return false;
    }
    public void Send(){
        try{
            Transport transport = session.getTransport("smtp");
            transport.connect(remitente,clave_remitente);
            transport.sendMessage(correo, correo.getRecipients(Message.RecipientType.TO));
            transport.close();
            JOptionPane.showMessageDialog(null, "Correo Enviado");
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
    }
    
   
    
}
