/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.io.File;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author yoshi
 */
public class MakeDynamicXML extends Application {
    Element Box;
    ObservableList<Node> ol;
    TicketCalculator app;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        //ここに採点するクラス名を入力
        app = new TicketCalculator();
        app.start(primaryStage);
        
         // Documentインスタンスの生成
        DocumentBuilder documentBuilder;
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        
        Scene scene = primaryStage.getScene();
        Parent root = scene.getRoot();
        ObservableList<Node> ol = PaneHantei((Node) root);
        
        
        
        primaryStage.show();
        // XMLファイルの作成
        File file = new File("DynamicTester.xml");
        write(file, document);
        //primaryStage.close();
        System.out.println("XMLを作成しました。");
    }
    
    boolean write(File file, Document document) {

        // Transformerインスタンスの生成
        Transformer transformer;
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            return false;
        }

        // Transformerの設定
        transformer.setOutputProperty("indent", "yes"); //改行指定
        transformer.setOutputProperty("encoding", "UTF-8"); // エンコーディング

        // XMLファイルの作成
        try {
            transformer.transform(new DOMSource(document), new StreamResult(file));
        } catch (TransformerException e) {
            return false;
        }
        return true;
    }
    
    ObservableList<Node> PaneHantei(Node element0) {
        if(element0.getClass().getSimpleName().endsWith("VBox")) {
            VBox pane = (VBox) element0;
            Box.setAttribute("Pos", pane.getAlignment().toString());       //Pos配置
            ol = pane.getChildren();
        }
        else if(element0.getClass().getSimpleName().endsWith("HBox")) {
            HBox pane = (HBox) element0;
            Box.setAttribute("Pos", pane.getAlignment().toString());
            ol = pane.getChildren();
        }
        else if(element0.getClass().getSimpleName().endsWith("BorderPane")) {
            BorderPane pane = (BorderPane) element0;
            ol = pane.getChildren();
        }
        else if(element0.getClass().getSimpleName().endsWith("GridPane")) {
            GridPane pane = (GridPane) element0;
            ol = pane.getChildren();
        }
        return ol;
    }
    
    public static void main(String[] args) {
        Application.launch(args);
    }
}
