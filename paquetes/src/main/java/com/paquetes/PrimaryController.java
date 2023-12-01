package com.paquetes;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class PrimaryController implements Initializable {
    //Variables de la BD
    Connection cnx=null;
    String Base="DATOS"; //Your Base Data name
    String srv="localhost:25000"; // Your Server name
    String usr="root";  //Your user
    String pass="root"; //Your password
    String cxnString="jdbc:db2://"+srv+"/"+Base+":retrieveMessagesFromServerOnGetMessage=true;";

    //Variables del codigo
    int id;
    String Nom1, Nom2, LNom1, LNom2, Plan;
    String Name1, Name2, LName1, LName2, Type;
    ArrayList<String> IdPlan = new ArrayList<String>();
    ArrayList<String> Nombre1 = new ArrayList<String>();
    ArrayList<String> Nombre2 = new ArrayList<String>();
    ArrayList<String> Apellido1 = new ArrayList<String>();
    ArrayList<String> Apellido2 = new ArrayList<String>();

    @FXML
    private ListView <String> lista;
    
    @FXML
    private TextField name1;

    @FXML
    private TextField name2;

    @FXML
    private TextField apellido1;

    @FXML
    private TextField apellido2;

    @FXML
    private ComboBox <String> Types;

    @FXML
    private void OnSearch() throws IOException {
        id=lista.getSelectionModel().getSelectedIndex();
        Nom1=Nombre1.get(id);
        Nom2=Nombre2.get(id);
        LNom1=Apellido1.get(id);
        LNom2=Apellido2.get(id);
        ReadOne();
        name1.setText(Name1);
        name2.setText(Name2);
        apellido1.setText(LName1);
        apellido2.setText(LName2);      
        Types.setValue(Type);
    }

    @FXML
    private void OnEdit() throws IOException {
        id=lista.getSelectionModel().getSelectedIndex();
        Name1=Nombre1.get(id);
        Name2=Nombre2.get(id);
        LName1=Apellido1.get(id);
        LName2=Apellido2.get(id);
        Plan = Types.getSelectionModel().getSelectedItem();
            if ("Pareja".equals(Plan)) {
                Plan = "2";
            }
            if ("Individual".equals(Plan)) {
                Plan = "1";
            }
            if ("Familia".equals(Plan)) {
                Plan = "3";
            }
        Nom1=name1.getText();
        Nom2=name2.getText();
        LNom1=apellido1.getText();
        LNom2=apellido2.getText();
        UPDATE();
        initialize(null, null);
    }

    @FXML
    private void OnDelete() throws IOException {
        id=lista.getSelectionModel().getSelectedIndex();
        Nom1=Nombre1.get(id);
        Nom2=Nombre2.get(id);
        LNom1=Apellido1.get(id);
        LNom2=Apellido2.get(id);
        Delete();
        name1.clear();
        name2.clear();
        apellido1.clear();
        apellido2.clear();
        initialize(null, null);
    }

    @FXML
    private void OnAdd() throws IOException {
        if(Types.getSelectionModel().getSelectedItem()!=null && !name1.getText().isEmpty() && !apellido1.getText().isEmpty()){
            Nom1=name1.getText();
            Nom2=name2.getText();
            LName1=apellido1.getText();
            LName2=apellido2.getText();
            Plan = Types.getSelectionModel().getSelectedItem();
            if ("Pareja".equals(Plan)) {
                Plan = "2";
            }
            if ("Individual".equals(Plan)) {
                Plan = "1";
            }
            if ("Familia".equals(Plan)) {
                Plan = "3";
            }
            ADD();
            name1.clear();
            name2.clear();
            apellido1.clear();
            apellido2.clear();
            initialize(null, null);
            
        }
        else{
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Algun campo esta vacio");
            alert.showAndWait();
        }  
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Connection();
        Read();
        Insertar();
    }

    public void Insertar(){
        lista.getItems().clear();
        Types.getItems().clear();
        for(int i=0;i<Nombre1.size();i++){
            lista.getItems().addAll(Nombre1.get(i)+" "+Nombre2.get(i)+" "+Apellido1.get(i)+" "+Apellido2.get(i));
        }
        Types.getItems().addAll(IdPlan);
    }

    //Base de datos
    public void Connection(){
        String msg="";
        try{
            Class.forName("com.ibm.db2.jcc.DB2Driver");
            cnx=DriverManager.getConnection(cxnString, usr, pass);
        }
        catch(Exception ex){
            msg="Error";
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Informacion");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        }
    }

    public void Read(){
        Nombre1.clear();
        Nombre2.clear();
        Apellido1.clear();
        Apellido2.clear();
        IdPlan.clear();
        try{
            String query="SELECT * FROM Clientes INNER JOIN Planes ON Clientes.IdPlan=Planes.IdPlan;";
            Statement cmd=cnx.createStatement();
            ResultSet Clientes=cmd.executeQuery(query);
            while(Clientes.next()){
                Nombre1.add(Clientes.getString("nombre1"));
                Nombre2.add(Clientes.getString("nombre2"));
                Apellido1.add(Clientes.getString("apellido1"));
                Apellido2.add(Clientes.getString("apellido2"));
            }
            String query2="SELECT * FROM Planes;";
            Statement cmd2=cnx.createStatement();
            ResultSet Planes=cmd2.executeQuery(query2);
            while(Planes.next()){
                IdPlan.add(Planes.getString("Nombre"));
            }
            Clientes.close();
            cmd.close();
            Planes.close();
            cmd2.close();
            cnx.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (cnx != null) {
                    cnx.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void Delete() {
        try {
            if (cnx == null || cnx.isClosed()) {
                Connection();
            }
    
            String query = "DELETE FROM Clientes WHERE nombre1 = '"+Nom1+"' AND nombre2 = '"+Nom2+"' AND apellido1 = '"+LNom1+"' AND apellido2 = '"+LNom2+"';";
    
            try (Statement cmd = cnx.createStatement()) {
                int filasAfectadas = cmd.executeUpdate(query);
                System.out.println("Se eliminaron " + filasAfectadas + " filas.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
        }
    }
    
    public void UPDATE(){
        try {
            if (cnx == null || cnx.isClosed()) {
                Connection();
            }
            String query = "UPDATE Clientes SET nombre1 = '"+Nom1+"', nombre2 = '"+Nom2+"', apellido1 = '"+LNom1+"', apellido2 = '"+LNom2+"', IdPlan = '"+Plan+"' WHERE nombre1 = '"+Name1+"' AND nombre2 = '"+Name2+"' AND apellido1 = '"+LName1+"' AND apellido2 = '"+LName2+"';";
    
            try (Statement cmd = cnx.createStatement()) {
                int filasAfectadas = cmd.executeUpdate(query);
                System.out.println("Se cambiaron " + filasAfectadas + " filas.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
        }
    }

    public void ReadOne() {
        try {
            // Verificar si la conexión está cerrada y abrirla si es necesario
            if (cnx == null || cnx.isClosed()) {
                Connection(); // Asume que Connection() es un método que abre la conexión
            }
    
            String query = "SELECT * FROM Clientes INNER JOIN Planes ON Clientes.IdPlan=Planes.IdPlan WHERE nombre1 = '"+Nom1+"' AND nombre2 = '"+Nom2+"' AND apellido1 = '"+LNom1+"' AND apellido2 = '"+LNom2+"';";
            
            try (Statement cmd = cnx.createStatement();
                 ResultSet result = cmd.executeQuery(query)) {
    
                if (result.next()) {
                    // Verificar si hay resultados antes de intentar obtener valores
                    Name1 = result.getString("nombre1");
                    Name2 = result.getString("nombre2");
                    LName1 = result.getString("apellido1");
                    LName2 = result.getString("apellido2");
                    Type = result.getString("Nombre");
                    System.out.println(Type);
                } else {
                    System.out.println("No se encontraron resultados para la consulta.");
                }
            }
        } catch (SQLException ex) {
            // Manejar excepciones específicas de SQL
            ex.printStackTrace();
        } finally {
            // No cierres la conexión aquí; deja que la gestión de conexiones se realice en un nivel superior
        }
    }

    public void ADD() {
        try {
            String query = "INSERT INTO Clientes (nombre1, nombre2, apellido1, apellido2, IdPlan) VALUES ('" + Nom1 + "','" + Nom2 + "','" + LName1 + "','" + LName2 + "','" + Plan + "')";
            
            try (Connection cnx = DriverManager.getConnection(cxnString, usr, pass);
                 Statement cmd = cnx.createStatement()) {
                
                // Utilizar executeUpdate para consultas de inserción
                int filasAfectadas = cmd.executeUpdate(query);
                System.out.println("Se insertaron " + filasAfectadas + " filas.");
    
            } 
    
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
