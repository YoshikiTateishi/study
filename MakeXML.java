/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.layout.*;
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

/**
 *
 * @author yoshi
 */
public class MakeXML extends Application {
    
    TicketCalculator app;
    String nodeType;
    
    public void start(Stage primaryStage) {
        app = new TicketCalculator();
        app.start(primaryStage);
        Scene scene = primaryStage.getScene();      // Scene
        Parent root = scene.getRoot();       
        nodeType = root.getClass().getSimpleName();      // VBox
        ObservableList<Node> ol = null;
        ArrayList<String> subcom = new ArrayList<>();
        if(nodeType.endsWith("VBox")) {
            VBox pane = (VBox) root;
            subcom.add(pane.getAlignment().toString());
            ol = pane.getChildren();
        }
        else if(nodeType.endsWith("HBox")) {
            HBox pane = (HBox) root;
            subcom.add(pane.getAlignment().toString());
            ol = pane.getChildren();
        }
        else if(nodeType.endsWith("BorderPane")) {
            BorderPane pane = (BorderPane) root;
            subcom.add("");
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
                subcom.add(pane2.getAlignment().toString());
                ObservableList<Node> ol2 = pane2.getChildren();
                int j;
                for(j=0; j<ol2.size(); j++){
                    com.add(ol2.get(j).getClass().getSimpleName());
                    String[] st2 = ol2.get(j).toString().split("'");
                    if(st2.length == 2)
                        subcom.add(st2[1]);
                    else
                        subcom.add("");
                    if(ol2.get(j).getClass().getSimpleName().endsWith("FlowPane")) {
                        FlowPane pane3 = (FlowPane) ol2.get(j);
                        ObservableList<Node> ol3 = pane3.getChildren();
                        int k;
                        for(k=0; k<ol3.size(); k++) {
                            com.add(ol3.get(k).getClass().getSimpleName());
                            String[] st3 = ol3.get(k).toString().split("'");
                            if(st3.length == 2)
                                subcom.add(st3[1]);
                            else
                                subcom.add("");
                        }
                    }
                }
            }
            else {
                String[] st = ol.get(i).toString().split("'");
                if(st.length == 2)
                    subcom.add(st[1]);
                else
                    subcom.add("");
            }
        }
        //System.out.println(com);
        data(nodeType,com,subcom);
    }
    
        public static void data(String a, ArrayList<String> s, ArrayList<String> ss) {
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
        scene.setAttribute("score", "1");
        document.appendChild(scene);
        Element Box = document.createElement(a);
        Box.appendChild(document.createTextNode(ss.get(0)));       //Pos配置
        Box.setAttribute("score", "1");
        //VBox.appendChild(document.createTextNode(s.get(0)));
        for (int i=0;  i<s.size(); i++) {
            Element Comp = document.createElement(s.get(i));
            Comp.setAttribute("score", "1");
            if(!ss.get(i+1).isEmpty())      //空判定
                Comp.appendChild(document.createTextNode(ss.get(i+1)));         //テキスト追加
            System.out.println(s.get(i));
            //System.out.println(Comp);
            if(s.get(i).endsWith("HBox")) {
                int j;
                for(j=i+1; j<s.size(); j++) {
                    if(s.get(j).endsWith("HBox")) 
                        break;
                    Element Comp2 = document.createElement(s.get(j));
                    Comp2.setAttribute("score", "1");
                    if(!ss.get(j+1).isEmpty())      //空判定
                        Comp2.appendChild(document.createTextNode(ss.get(j+1)));         //テキスト追加
                    //System.out.println(Comp2);
                    Comp.appendChild(Comp2);
                    System.out.println(s.get(j));
                    if(s.get(j).endsWith("FlowPane")) {
                        int k;
                        for(k=j+1; k<s.size(); k++) {
                            if(s.get(k).endsWith("HBox")) 
                                break;
                            Element Comp3 = document.createElement(s.get(k));
                            if(!ss.get(k+1).isEmpty())      //空判定
                                Comp3.appendChild(document.createTextNode(ss.get(k+1)));         //テキスト追加
                            Comp2.appendChild(Comp3);
                            System.out.println(s.get(k));
                        }
                        j = k-1;
                    }
                }
                i = j-1;
            }
            //System.out.println(Comp);
            Box.appendChild(Comp);
            //VBox.appendChild(document.createTextNode(", " + s.get(i)));
        }
        scene.appendChild(Box);
        
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
    
    public static void aaa() {
        
    }
    public static void main(String[] args) {
        Application.launch(args);
    }
}
