/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
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
public class XMLTester extends Application {
    
    Element Box;
    ObservableList<Node> ol;
    Document kadai;
    Map<Integer, String> comList;
            
    @Override
    public void start(Stage primaryStage) throws Exception {
        //ここに採点するクラス名を入力
        TicketCalculator app = new TicketCalculator();
        app.start(primaryStage);
        
        comList = new HashMap<>();
        
         // Documentインスタンスの生成
        DocumentBuilder documentBuilder;
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        
        //シーン
        Scene scene1 = primaryStage.getScene();      // Scene
        Element scene = document.createElement("Scene");
        scene.setAttribute("ID", "0");
        comList.put(0, "scene");
        document.appendChild(scene);
        
        //レイアウトペイン
        Parent root = scene1.getRoot();       
        String nodeType = root.getClass().getSimpleName();
        Box = document.createElement(nodeType);
        Box.setAttribute("ID", "1");
        comList.put(1, nodeType);
        
        ObservableList<Node> ol1 = PaneHantei((Node)root);
        
        //コンポーネント
        int i;
        int cnt = 2;
        //ArrayList<String> comList = new ArrayList<>();
        //Map<String, String> prefs = new HashMap<>();
        for(i=0; i<ol1.size(); i++) {
            String com = ol1.get(i).getClass().getSimpleName();      // コンポーネント名
            Element Comp = document.createElement(com);
            Comp.setAttribute("ID", String.valueOf(cnt));
            comList.put(cnt, com);
            cnt++;
            if(com.endsWith("HBox")) {
                HBox pane2 = (HBox) ol1.get(i);
                Comp.setAttribute("Pos", pane2.getAlignment().toString());
                ObservableList<Node> ol2 = pane2.getChildren();
                
                //コンポーネント2
                int j;
                for(j=0; j<ol2.size(); j++){
                    String com2 = ol2.get(j).getClass().getSimpleName();      // コンポーネント名
                    Element Comp2 = document.createElement(com2);
                    Comp2.setAttribute("ID", String.valueOf(cnt));
                    comList.put(cnt, com2);
                    cnt++;
                    String[] st2 = ol2.get(j).toString().split("'");
                    if(st2.length == 2)     //文字あり
                        Comp2.appendChild(document.createTextNode((st2[1])));         //テキスト追加
                    else if(ol2.get(j).getClass().getSimpleName().endsWith("TextField")) {      //テキストフィールド
                        TextField tf = (TextField)ol2.get(j);
                        Comp2.appendChild(document.createTextNode((tf.getText())));
                    }
                    else if(ol2.get(j).getClass().getSimpleName().endsWith("ComboBox")) {       //コンボボックス
                        ComboBox cb = (ComboBox)ol2.get(j);
                        Comp2.appendChild(document.createTextNode((cb.getItems().toString())));
                    }
                    
                    if(ol2.get(j).getClass().getSimpleName().endsWith("FlowPane")) {
                        FlowPane pane3 = (FlowPane) ol2.get(j);
                        ObservableList<Node> ol3 = pane3.getChildren();
                        
                        //コンポーネント3
                        int k;
                        for(k=0; k<ol3.size(); k++) {
                            String com3 = ol3.get(k).getClass().getSimpleName();      // コンポーネント名
                            Element Comp3 = document.createElement(com3);
                            Comp3.setAttribute("ID", String.valueOf(cnt));
                            comList.put(cnt, com3);
                            cnt++;
                            String[] st3 = ol3.get(k).toString().split("'");
                            if(st3.length == 2)
                                Comp3.appendChild(document.createTextNode((st3[1])));         //テキスト追加
                            Comp2.appendChild(Comp3);
                        }
                    }
                    Comp.appendChild(Comp2);
                }
            }
            
            else {
                String[] st = ol.get(i).toString().split("'");
                if(st.length == 2)
                    Comp.appendChild(document.createTextNode((st[1])));         //テキスト追加
                else if(com.endsWith("TextField")) {      //テキストフィールド
                    TextField tf = (TextField)ol.get(i);
                    Comp.appendChild(document.createTextNode((tf.getText())));
                }
            }
            Box.appendChild(Comp);
        }
        scene.appendChild(Box);
        
        // XMLファイルの作成
        File file = new File("kadai.xml");
        write(file, document);
        StaticTest();
        DynamicTest();
        primaryStage.close();
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
    
    void StaticTest() throws Exception { 
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        FileChooser chooser = new FileChooser();
        chooser.setTitle("XMLファイルを選択");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML files", "*.xml*"));
        File TesterFile = chooser.showOpenDialog(null);
        
        Document Tester = builder.parse(TesterFile);
        kadai = builder.parse("kadai.xml");
        
        int point = 0;
        Element TesterRoot = Tester.getDocumentElement();       //Scene
        Element kadaiRoot = kadai.getDocumentElement();       //Scene
        if(TesterRoot.getNodeName().endsWith(kadaiRoot.getNodeName()))
            System.out.println(TesterRoot.getNodeName() + "：OK");
        NodeList TesterNodeList = TesterRoot.getChildNodes();
        NodeList kadaiNodeList = kadaiRoot.getChildNodes();
        org.w3c.dom.Node TesterNode = TesterNodeList.item(1);
        org.w3c.dom.Node kadaiNode = kadaiNodeList.item(1);
        Element TesterElement = (Element)TesterNode;        //VBox
        Element kadaiElement = (Element)kadaiNode;        //VBox
        if(TesterElement.getNodeName().endsWith(kadaiElement.getNodeName()))
            System.out.println(TesterElement.getNodeName() + "：OK");
        //コンポーネント
        NodeList TesterNodeList2 = TesterElement.getChildNodes();
        NodeList kadaiNodeList2 = kadaiElement.getChildNodes();
        int cnt = 0;
        for(int i=0; i<kadaiNodeList2.getLength(); i++) {
            org.w3c.dom.Node TesterNode2 = TesterNodeList2.item(i);
            org.w3c.dom.Node kadaiNode2 = kadaiNodeList2.item(i);
            if(TesterNode2.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && kadaiNode2.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element TesterElement2 = (Element) TesterNode2;
                Element kadaiElement2 = (Element) kadaiNode2;
                if(TesterElement2.getNodeName().endsWith(kadaiElement2.getNodeName()))
                    System.out.println(TesterElement2.getNodeName() + "：OK");
                if(TesterElement2.getNodeName().endsWith("HBox")) {
                    NodeList TesterNodeList3 = TesterElement2.getChildNodes();
                    NodeList kadaiNodeList3 = kadaiElement2.getChildNodes();
                    for(int j=0; j<kadaiNodeList3.getLength(); j++) {
                        org.w3c.dom.Node TesterNode3 = TesterNodeList3.item(j);
                        org.w3c.dom.Node kadaiNode3 = kadaiNodeList3.item(j);
                        if(TesterNode3.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && kadaiNode3.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            Element TesterElement3 = (Element) TesterNode3;
                            Element kadaiElement3 = (Element) kadaiNode3;
                            if(TesterElement3.getNodeName().endsWith(kadaiElement3.getNodeName()))
                                System.out.println(TesterElement3.getNodeName() + "：OK");
                            if(TesterElement3.getNodeName().endsWith("FlowPane")) {
                                NodeList TesterNodeList4 = TesterElement3.getChildNodes();
                                NodeList kadaiNodeList4 = kadaiElement3.getChildNodes();
                                for(int k=0; k<kadaiNodeList4.getLength(); k++) {
                                    org.w3c.dom.Node TesterNode4 = TesterNodeList4.item(k);
                                    org.w3c.dom.Node kadaiNode4 = kadaiNodeList4.item(k);
                                    if(TesterNode4.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && kadaiNode4.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                                        Element TesterElement4 = (Element) TesterNode4;
                                        Element kadaiElement4 = (Element) kadaiNode4;
                                        if(TesterElement4.getNodeName().endsWith(kadaiElement4.getNodeName()))
                                            System.out.println(TesterElement4.getNodeName() + "：OK");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    void DynamicTest() throws Exception {
        Element kadaiRoot = kadai.getDocumentElement();       //Scene
        NodeList kadaiNodeList = kadaiRoot.getChildNodes();
        org.w3c.dom.Node kadaiNode = kadaiNodeList.item(1);
        Element kadaiElement = (Element)kadaiNode;        //VBox
        //コンポーネント
        NodeList kadaiNodeList2 = kadaiElement.getChildNodes();
        int cnt = 0;
        for(int i=0; i<kadaiNodeList2.getLength(); i++) {
            org.w3c.dom.Node kadaiNode2 = kadaiNodeList2.item(i);
            if(kadaiNode2.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element kadaiElement2 = (Element) kadaiNode2;
                if(kadaiElement2.getNodeName().endsWith("HBox")) {
                    NodeList kadaiNodeList3 = kadaiElement2.getChildNodes();
                    for(int j=0; j<kadaiNodeList3.getLength(); j++) {
                        org.w3c.dom.Node kadaiNode3 = kadaiNodeList3.item(j);
                        if(kadaiNode3.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            Element kadaiElement3 = (Element) kadaiNode3;
                            if((Node)kadaiElement3 instanceof ButtonBase) {
                                ArrayList <String> al = new ArrayList<>();
                                al.add(kadaiElement3.getTextContent());
                                ComboBoxAct(al);
                                
                            }
                            if(kadaiElement3.getNodeName().endsWith("FlowPane")) {
                                NodeList kadaiNodeList4 = kadaiElement3.getChildNodes();
                                for(int k=0; k<kadaiNodeList4.getLength(); k++) {
                                    org.w3c.dom.Node kadaiNode4 = kadaiNodeList4.item(k);
                                    if(kadaiNode4.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                                        Element kadaiElement4 = (Element) kadaiNode4;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    void ComboBoxAct(ArrayList al) {
        
    } 
    
    void ButtonAct() {
        
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