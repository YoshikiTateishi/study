/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.File;
import java.util.LinkedHashMap;

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
import org.w3c.dom.NodeList;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author yoshi
 */
public class XMLTester extends Application {
    
	AddTaxApp app;
	String appName = "AddTaxApp";
    Stage primaryStage;
    Document document;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    int cnt = 2;
    LinkedHashMap<Integer, Node> comList;
            
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        //ここに採点するクラス名を入力
        app = new AddTaxApp();
        app.start(primaryStage);
        //シーン
        Scene scene1 = primaryStage.getScene();      // Scene
        getNodeList(scene1);
        
        // XMLファイルの作成
        File file = new File("Kadai1.xml");
        write(file, document);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document kadai1 = builder.parse("kadai1.xml");
        System.out.println("Static Test Start");
        StaticTest(kadai1);
        System.out.println("Static Test：Success!");
        //System.out.println("動的テスト開始");
        //DynamicTest();
        //primaryStage.close();
    }
    
    void getNodeList(Scene scene) throws Exception {
        comList = new LinkedHashMap<>();        //IDとノード格納
        
        // Documentインスタンスの生成
        DocumentBuilder documentBuilder;
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        document = documentBuilder.newDocument();
        
        
        Element sceneElement = document.createElement("Scene");
        sceneElement.setAttribute("ID", "0");
        document.appendChild(sceneElement);
        
        //レイアウトペイン
        Parent root = scene.getRoot();
        String nodeType = root.getClass().getSimpleName();
        Element Box = document.createElement(nodeType);
        Box.setAttribute("ID", "1");
        PaneHantei((Node) root, Box);    //POSデータ判定、書き込み
        comList.put(1, (Node) root);
        
        //コンポーネント
        getCom(Box, root);
        sceneElement.appendChild(Box);
    }
    
    //コンポーネント読み込みメソッド
    void getCom(Element cp, Parent p) {
        ObservableList<Node> children = p.getChildrenUnmodifiable();
        if (children != null) {
            for (int i=0; i<children.size(); i++) {
                String com = children.get(i).getClass().getSimpleName();      // コンポーネント名
                Element Comp = document.createElement(com);
                Comp.setAttribute("ID", String.valueOf(cnt));
                comList.put(cnt, children.get(i));
                cnt++;
                addText(Comp, children.get(i));
                cp.appendChild(Comp);
                Node node = children.get(i);
                if (node instanceof Pane) {     //レイアウトペイン判定
                    PaneHantei((Node)node, Comp);
                    getCom(Comp, (Parent) node);
                }
            } 
        }
    }
    
    //XML書き込みメソッド
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
    
    //Posデータ追加メソッド
    void PaneHantei(Node r, Element el) {
        if(r instanceof VBox) {
            VBox pane = (VBox) r;
            el.setAttribute("Pos", pane.getAlignment().toString());       //Pos配置
        }
        else if(r instanceof HBox) {
            HBox pane = (HBox) r;
            el.setAttribute("Pos", pane.getAlignment().toString());
        }
    }
    
    //テキスト追加メソッド
    void addText(Element el, Node node) {
        if (node instanceof TextField) {
            TextField tf = (TextField) node;
            el.appendChild(document.createTextNode(tf.getText()));
        }
        else if(node instanceof ComboBox) {
            ComboBox cb = (ComboBox) node;
            if(cb.getValue() != null)
                el.appendChild(document.createTextNode(cb.getValue().toString()));
        }
        else {
            String[] st = node.toString().split("'");
            if (st.length == 2)
                el.appendChild(document.createTextNode((st[1]))); 
        }
    }
    
    void StaticTest(Document kadai) throws Exception {
        File TesterFile = new File("StaticTester.xml");
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document Tester = builder.parse(TesterFile);
        
        int point = 0;
        int pointMax = 0;
        Element TesterRoot = Tester.getDocumentElement();       //Scene
        Element kadaiRoot = kadai.getDocumentElement();       //Scene
        pointMax++;
        if(TesterRoot.getNodeName().endsWith(kadaiRoot.getNodeName())) {
            System.out.println(TesterRoot.getNodeName() + "：OK");
            point++;
        }
        NodeList TesterNodeList = TesterRoot.getChildNodes();
        NodeList kadaiNodeList = kadaiRoot.getChildNodes();
        org.w3c.dom.Node TesterNode = TesterNodeList.item(1);
        org.w3c.dom.Node kadaiNode = kadaiNodeList.item(1);
        Element TesterElement = (Element)TesterNode;        //VBox
        Element kadaiElement = (Element)kadaiNode;        //VBox
        pointMax++;
        if(TesterElement.getNodeName().endsWith(kadaiElement.getNodeName())) {
            System.out.println(TesterElement.getNodeName() + "：OK");
            point++;
        }
        //コンポーネント
        NodeList TesterNodeList2 = TesterElement.getChildNodes();
        NodeList kadaiNodeList2 = kadaiElement.getChildNodes();
        cnt = 0;
        for(int i=0; i<kadaiNodeList2.getLength(); i++) {
            org.w3c.dom.Node TesterNode2 = TesterNodeList2.item(i);
            org.w3c.dom.Node kadaiNode2 = kadaiNodeList2.item(i);
            if(TesterNode2.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && kadaiNode2.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element TesterElement2 = (Element) TesterNode2;
                Element kadaiElement2 = (Element) kadaiNode2;
                pointMax++;
                if(TesterElement2.getNodeName().endsWith(kadaiElement2.getNodeName())) {
                    System.out.println(TesterElement2.getNodeName() + "：OK");
                    point++;
                }
                if(TesterElement2.getNodeName().endsWith("HBox")) {
                    NodeList TesterNodeList3 = TesterElement2.getChildNodes();
                    NodeList kadaiNodeList3 = kadaiElement2.getChildNodes();
                    for(int j=0; j<kadaiNodeList3.getLength(); j++) {
                        org.w3c.dom.Node TesterNode3 = TesterNodeList3.item(j);
                        org.w3c.dom.Node kadaiNode3 = kadaiNodeList3.item(j);
                        if(TesterNode3.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && kadaiNode3.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            Element TesterElement3 = (Element) TesterNode3;
                            Element kadaiElement3 = (Element) kadaiNode3;
                            pointMax++;
                            if(TesterElement3.getNodeName().endsWith(kadaiElement3.getNodeName())) {
                                System.out.println(TesterElement3.getNodeName() + "：OK");
                                point++;
                            }
                            if(TesterElement3.getNodeName().endsWith("FlowPane")) {
                                NodeList TesterNodeList4 = TesterElement3.getChildNodes();
                                NodeList kadaiNodeList4 = kadaiElement3.getChildNodes();
                                for(int k=0; k<kadaiNodeList4.getLength(); k++) {
                                    org.w3c.dom.Node TesterNode4 = TesterNodeList4.item(k);
                                    org.w3c.dom.Node kadaiNode4 = kadaiNodeList4.item(k);
                                    if(TesterNode4.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && kadaiNode4.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                                        Element TesterElement4 = (Element) TesterNode4;
                                        Element kadaiElement4 = (Element) kadaiNode4;
                                        pointMax++;
                                        if(TesterElement4.getNodeName().endsWith(kadaiElement4.getNodeName())) {
                                            System.out.println(TesterElement4.getNodeName() + "：OK");
                                            point++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println(String.format("【Target:%s, ID:%s, Name:%s, Score:%d】",
        		appName, app.gakuban, app.yourname, 10 * point / pointMax));
    }
     
    void DynamicTest() throws Exception {
    	File DTesterFile = new File("DynamicTester.xml");
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document DTester = builder.parse(DTesterFile);
        
		for(int key : comList.keySet()) {
		    if(comList.get(key) instanceof ButtonBase) {
		        ButtonBase bb = (ButtonBase) comList.get(key);
		        bb.fire();
		    }
		    else if(comList.get(key) instanceof ComboBox) {
		        ComboBox cb = (ComboBox) comList.get(key);
		        ObservableList<Integer> items = cb.getItems();
		        cb.setValue(items.get(1));
		        System.out.println("入力：" + items.get(1));
		    }
		    else if(comList.get(key) instanceof TextInputControl) {
		        TextInputControl tic = (TextInputControl) comList.get(key);
		        System.out.println("出力：" + tic.getText());
		    }
		}
		System.out.println("OK");
		System.out.println("入力：リセット");
		System.out.println("出力：null");
		System.out.println("出力：null");
		System.out.println("出力：null");
		System.out.println("OK");
    }
     
    public static void main(String[] args) {
        Application.launch(args);
    }
}
