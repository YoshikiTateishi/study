/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;

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

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author yoshi
 */
public class MakeDynamicXML extends Application {
    
    Stage primaryStage;
    Document document;
    ModelAnswer app;
    int cnt = 2;
    
    public void menu(Stage myStage) {
    	app = new ModelAnswer();
    	app.start(primaryStage);
    	
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        //ここに採点するクラス名を入力
        ModelAnswer app;
        app = new ModelAnswer();
        app.start(primaryStage);
        getNodeList();
        // XMLファイルの作成
        File file = new File("DynamicTester.xml");
        write(file, document);
        primaryStage.close();
        System.out.println("XMLを作成しました。");
    }
    
    //ノードリスト取得メソッド
    void getNodeList() throws Exception {
        // Documentインスタンスの生成
        DocumentBuilder documentBuilder;
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        document = documentBuilder.newDocument();
        
        //シーン
        Scene scene1 = primaryStage.getScene();      // Scene
        Element scene = document.createElement("Scene");
        scene.setAttribute("ID", "0");
        document.appendChild(scene);
        
        //レイアウトペイン
        Parent root = scene1.getRoot();
        String nodeType = root.getClass().getSimpleName();
        Element Box = document.createElement(nodeType);
        Box.setAttribute("ID", "1");
        PaneHantei((Node) root, Box);    //POSデータ判定、書き込み
        
        //コンポーネント
        getCom(Box, root);
        scene.appendChild(Box);
    }
    
    //コンポーネント読み込みメソッド
    void getCom(Element cp, Parent p) {
        ObservableList<Node> children = p.getChildrenUnmodifiable();
        if (children != null) {
            for (int i=0; i<children.size(); i++) {
                String com = children.get(i).getClass().getSimpleName();      // コンポーネント名
                Element Comp = document.createElement(com);
                Comp.setAttribute("ID", String.valueOf(cnt));
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
            if (cb.getValue() != null)
                el.appendChild(document.createTextNode(cb.getValue().toString()));
        }
        else {
            String[] st = node.toString().split("'");
            if (st.length == 2)
                el.appendChild(document.createTextNode((st[1]))); 
        }
    }
    
    public static void main(String[] args) {
        Application.launch(args);
    }
}
