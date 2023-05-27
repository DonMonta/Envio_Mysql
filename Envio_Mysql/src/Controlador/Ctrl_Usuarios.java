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
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

import javax.net.ssl.X509TrustManager;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
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
    private static String clave_remitente="zjayvubdpmuinmtr";
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
        if(consulta_Usuarios.ExisteUsuario(usuarios)){
             JOptionPane.showMessageDialog(null, "EL Usuario Ya Existe Ingrese Otro usuario");
             return false;
        }
        else if(consulta_Usuarios.ExisteCorreoUsuario(usuarios)){
             JOptionPane.showMessageDialog(null, "EL Correo Ya Existe Ingrese Otro correo");
             return false;
        }
        else if(!ValidarCorreo(correo)){
             JOptionPane.showMessageDialog(null, "EL Correo Ingresado No es Valido");
             return false;
        }
        else if(consulta_Usuarios.Guardar(usuarios)){
            JOptionPane.showMessageDialog(null, "Usuario Registrado");
            return true;
        }
        return false;
    }
    public boolean CorreoCorrexto(String correos){
        try {
            String cod = generarCodigoVerificacion();
           String correoVer = "soporte.tecnico.2023.dm@gmail.com";
            String dominio = correos.substring(correos.indexOf("@")+1);
            
            //Configurar propidades para la conexion SMTP
            Properties pros = new Properties();
             pros.put("mail.smtp.host", "smtp." + dominio);
             pros.put("mail.smtp.port", "587");
             pros.put("mail.smtp.starttls.enable", "true");
             pros.put("mail.smtp.auth", "true");
             pros.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
             
             //Configurar un TrustManager presonalizado para aceptar todos los certificados SSL
             // Configurar un TrustManager personalizado para aceptar todos los certificados SSL
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
             //Obtener una instancia de SSLContext  y Configuara el TrustManager personalizado
             SSLContext sSLContext = SSLContext.getInstance("SSL");
             sSLContext.init(null, trustAllCerts, new java.security.SecureRandom());
             SSLContext.setDefault(sSLContext);
             Session session= Session.getInstance(pros,new Authenticator(){
                 protected PasswordAuthentication getPasswordAuthentication(){
                  return new PasswordAuthentication("soporte.tecnico.2023.dm@gmail.com","zjayvubdpmuinmtr");
                 }
             });
             //Crear  el mensaje de correo electronico para la verificacion
             Message mensaje = new MimeMessage(session);
             mensaje.setFrom(new InternetAddress(correoVer));
             mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correos));
             mensaje.setSubject("Validacion de correo electronico");
             mensaje.setText("Correo Verificado. Tu codigo de Verificacion es: " + cod);
             
             //Enviar el mensaje 
             
             Transport.send(mensaje);
             // Validar Codigo de Verificacion
             String codingres = JOptionPane.showInputDialog("Ingrese el codigo de verificacion enviado al correo "+correos);
             while(codingres !=null){
                 if(codingres.equals(cod)){
                     JOptionPane.showMessageDialog(null, "Codigo de verificacion correcto. El correo es Valido");
                     return true;
                 }else{
                     JOptionPane.showMessageDialog(null, "Codigo de verificacion incorrecto");
                 }
             }
             return false;
             
            
        } catch (Exception e) {
            return false;
        }
        
    }
    public String generarCodigoVerificacion(){
        Random random= new Random();
        int codigo = 100000 + random.nextInt(900000);
        return String.valueOf(codigo);
    }
    
    public boolean ValidarCorreo(String correos){
        boolean valido = false;
        try {
            InternetAddress internetAddress = new InternetAddress(correos);
            internetAddress.validate();
            valido=true;
            if(valido){
                valido=CorreoCorrexto(correos);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error "+e);
        }
        return valido;
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
