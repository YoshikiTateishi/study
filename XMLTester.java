package xml;


import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yoshi
 */
public class XMLTester extends Application {
    
    AddTaxApp app;
    String appName = "CountUpDownApp";
    String nodeType;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        app = new AddTaxApp();
        app.start(primaryStage);
        Scene scene = primaryStage.getScene();      // Scene
        Parent root = scene.getRoot();
        nodeType = root.getClass().getSimpleName();      // VBoxまたはHBox
        //HBox pane = (HBox) root;
        VBox pane = (VBox) root;
        int i;
        ArrayList<String> com = new ArrayList<>();
        ObservableList<javafx.scene.Node> ol = pane.getChildren();
        for(i=0; i < ol.size(); i++) {
            com.add(pane.getChildren().get(i).getClass().getSimpleName()); // コンポーネント
        }
        data(nodeType, com);
    }
    
    void data(String a, ArrayList<String> s) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse("Tester.xml");
        
        Element root = document.getDocumentElement();
        System.out.println(root.getNodeName());             // Scene
        NodeList nodeList = root.getChildNodes();
        for(int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                //System.out.println(element.getNodeName());          //VBox
                if(element.getNodeName().equals(a)) {
                    System.out.println(element.getNodeName() + "：OK");
                }
                NodeList nodeList2 = element.getChildNodes();
                int k = 0;
                for(int j = 0; j < nodeList2.getLength(); j++) {
                    Node node2 = nodeList2.item(j);
                    if(node2.getNodeType() == Node.ELEMENT_NODE) {
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
    
    
    public static void main(String[] args) {
        Application.launch(args);
    }
    
}