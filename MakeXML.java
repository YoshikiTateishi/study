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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yoshi
 */
public class MakeXML extends Application {
    
    CountUpDownApp app;
    //String appName = "CountUpDownApp";
    String nodeType;
    
    @Override
    public void start(Stage primaryStage) {
        app = new CountUpDownApp();
        app.start(primaryStage);
        Scene scene = primaryStage.getScene();      // Scene
        Parent root = scene.getRoot();
        nodeType = root.getClass().getSimpleName();      // VBox
        ObservableList<Node> ol;
        if(nodeType.endsWith("VBox")) {
            VBox pane = (VBox) root;
            ol = pane.getChildren();
        }
        else{
            HBox pane = (HBox) root;
            ol = pane.getChildren();
            
        }
        System.out.println(nodeType);
            //System.out.println(pane.getAlignment());
            //data(nodeType, 1);
            int i;
            ArrayList<String> com = new ArrayList<>();
            Map<String, String> prefs = new HashMap<>();
            for(i=0; i < ol.size(); i++) {
                com.add(ol.get(i).getClass().getSimpleName()); // コンポーネント
                String[] namae = ol.get(i).toString().split("'");
                if(namae.length == 2) {
                    System.out.println(namae[1]);
                    prefs.put(ol.get(i).getClass().getSimpleName(), namae[1]);
                }
                else {
                    prefs.put(ol.get(i).getClass().getSimpleName(), null);
                }
                
                //data(com, 2);
            }
            System.out.println(com);
            data(nodeType, com, prefs);
        
    }
    
    public static void data(String a, ArrayList<String> s, Map<String, String> s2) {
        // Documentインスタンスの生成
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = documentBuilder.newDocument();
          
        // XML文書の作成
        Element scene = document.createElement("Scene");
        //scene.setAttribute("score", "2");
        document.appendChild(scene);
        Element VBox = document.createElement(a);
        //VBox.setAttribute("score", "2");
        //VBox.appendChild(document.createTextNode(s.get(0)));
        for (int i=0; i<s.size(); i++) {
            Element Comp = document.createElement(s.get(i));
            VBox.appendChild(Comp);
            if(s2.get(s.get(i)) != null) {
                Comp.appendChild(document.createTextNode(s2.get(s.get(i))));
            }
            //VBox.appendChild(document.createTextNode(", " + s.get(i)));
        }
        scene.appendChild(VBox);
        
        // XMLファイルの作成
        File file = new File("Tester.xml");
        write(file, document);
    }
    
    public static boolean write(File file, Document document) {

        // Transformerインスタンスの生成
        Transformer transformer = null;
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            return false;
        }

        // Transformerの設定
        transformer.setOutputProperty("indent", "yes"); //改行指定
        transformer.setOutputProperty("encoding", "UTF-8"); // エンコーディング

        // XMLファイルの作成
        try {
            transformer.transform(new DOMSource(document), new StreamResult(file));
        } catch (TransformerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static void main(String[] args) {
        Application.launch(args);
    }
    
}
