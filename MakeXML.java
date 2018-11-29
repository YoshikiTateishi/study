/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.io.File;
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
public class MakeXML extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        //ここに採点するクラス名を入力
        RamenOrderApp app = new RamenOrderApp();
        app.start(primaryStage);
        
         // Documentインスタンスの生成
        DocumentBuilder documentBuilder;
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        
        //シーン
        Scene scene1 = primaryStage.getScene();      // Scene
        Element scene = document.createElement("Scene");
        scene.setAttribute("ID", "0");
        document.appendChild(scene);
        
        //レイアウトペイン
        Parent root = scene1.getRoot();       
        String nodeType = root.getClass().getSimpleName();
        Element Box = document.createElement(nodeType);
        ObservableList<Node> ol = null;
        //ペイン一覧
        if(nodeType.endsWith("VBox")) {
            VBox pane = (VBox) root;
            Box.setAttribute("ID", "1");
            Box.setAttribute("Pos", pane.getAlignment().toString());       //Pos配置
            ol = pane.getChildren();
        }
        else if(nodeType.endsWith("HBox")) {
            HBox pane = (HBox) root;
            Box.setAttribute("ID", "1");
            Box.setAttribute("Pos", pane.getAlignment().toString());
            ol = pane.getChildren();
        }
        else if(nodeType.endsWith("BorderPane")) {
            BorderPane pane = (BorderPane) root;
            Box.setAttribute("ID", "1");
            ol = pane.getChildren();
        }
        else if(nodeType.endsWith("GridPane")) {
            GridPane pane = (GridPane) root;
            Box.setAttribute("ID", "1");
            ol = pane.getChildren();
        }
        
        //コンポーネント
        int i;
        int cnt = 2;
        //ArrayList<String> comList = new ArrayList<>();
        //Map<String, String> prefs = new HashMap<>();
        for(i=0; i<ol.size(); i++) {
            String com = ol.get(i).getClass().getSimpleName();      // コンポーネント名
            Element Comp = document.createElement(com);
            Comp.setAttribute("ID", String.valueOf(cnt));
            cnt++;
            if(com.endsWith("HBox")) {
                HBox pane2 = (HBox) ol.get(i);
                Comp.setAttribute("Pos", pane2.getAlignment().toString());
                ObservableList<Node> ol2 = pane2.getChildren();
                
                //コンポーネント2
                int j;
                for(j=0; j<ol2.size(); j++){
                    String com2 = ol2.get(j).getClass().getSimpleName();      // コンポーネント名
                    Element Comp2 = document.createElement(com2);
                    Comp2.setAttribute("ID", String.valueOf(cnt));
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
        
        primaryStage.close();
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
    
    void StaticTest() throws Exception { 
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        FileChooser chooser = new FileChooser();
        chooser.setTitle("XMLファイルを選択");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML files", "*.xml*"));
        File TesterFile = chooser.showOpenDialog(null);
        
        Document Tester = builder.parse(TesterFile);
        Document kadai = builder.parse("kadai.xml");
        
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
                    for(int j=0; j<kadaiNodeList.getLength(); j++) {
                        //ここから
                    }
                }
            }
        }
    }
    
    public static void main(String[] args) {
        Application.launch(args);
    }
}
