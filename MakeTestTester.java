/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author yoshi
 */
public class MakeTestTester extends Application {
    
    DenominationApp app;
    String nodeType;
    
    public void start(Stage primaryStage)  throws Exception{
        app = new DenominationApp();
        app.start(primaryStage);
        Scene scene = primaryStage.getScene();      // Scene
        Parent root = scene.getRoot();
        
        nodeType = root.getClass().getSimpleName();      // VBox
        ObservableList<Node> ol = null;
        if(nodeType.endsWith("VBox")) {
            VBox pane = (VBox) root;
            ol = pane.getChildren();
        }
        else if(nodeType.endsWith("HBox")) {
            HBox pane = (HBox) root;
            ol = pane.getChildren();
        }
        else if(nodeType.endsWith("BorderPane")) {
            BorderPane pane = (BorderPane) root;
            ol = pane.getChildren();
        }
        else if(nodeType.endsWith("GridPane")) {
            GridPane pane = (GridPane) root;
            ol = pane.getChildren();
        }
        //System.out.println(nodeType);
        //System.out.println(pane.getAlignment());
        //data(nodeType, 1);
        int i;
        ArrayList<String> com = new ArrayList<>();

        //Map<String, String> prefs = new HashMap<>();
        for(i=0; i<ol.size(); i++) {
            com.add(ol.get(i).getClass().getSimpleName()); // コンポーネント
            //data(com, 2);
            if(ol.get(i).getClass().getSimpleName().endsWith("HBox")) {
                HBox pane2 = (HBox) ol.get(i);
                ObservableList<Node> ol2 = pane2.getChildren();
                int j;
                for(j=0; j<ol2.size(); j++){
                    com.add(ol2.get(j).getClass().getSimpleName());
                    String[] namae = ol2.get(j).toString().split("'");
                    if(ol2.get(j).getClass().getSimpleName().endsWith("FlowPane")) {
                        FlowPane pane3 = (FlowPane) ol2.get(j);
                        ObservableList<Node> ol3 = pane3.getChildren();
                        int k;
                        for(k=0; k<ol3.size(); k++) {
                            com.add(ol3.get(k).getClass().getSimpleName());
                        }
                    }
                }
            }
            
            
        }
        //System.out.println(com);
        data(nodeType, com);
    }
    
    public static void data(String a, ArrayList<String> s) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse("Tester.xml");
        
        Element root = document.getDocumentElement();
        System.out.println(root.getNodeName());             // Scene
        NodeList nodeList = root.getChildNodes();
        for(int i = 0; i < nodeList.getLength(); i++) {
            org.w3c.dom.Node node = nodeList.item(i);
            if(node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element element = (Element)node;
                //System.out.println(element.getNodeName());          //VBox
                if(element.getNodeName().equals(a)) {
                    System.out.println(element.getNodeName() + "：OK");
                }
                NodeList nodeList2 = element.getChildNodes();
                int k = 0;
                for(int j = 0; j < nodeList2.getLength(); j++) {
                    org.w3c.dom.Node node2 = nodeList2.item(j);
                    if(node2.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element element2 = (Element)node2;
                        //System.out.println(element2.getNodeName());         // コンポーネント
                        if(element2.getNodeName().equals(s.get(k))) {
                            System.out.println(element2.getNodeName() + "：OK");
                            k++;
                        }
                    }
                }
             }
        }
    }
    
    public static void main(String[] args)  throws Exception{
        Application.launch(args);
    }
}