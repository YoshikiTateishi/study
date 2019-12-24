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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author yoshi
 */
public class MakeStaticXML extends Application {
    
    Stage stage1, stage2, primaryStage;
    Document document;
    CheckBoxTreeItem<String>[] pane, InputPane, EventPane;
    CheckBoxTreeItem<String> PaneBox;
    SplitBillApp app;
    int cnt;
    int pointMax;
    int NameCount;
    
    @Override
    public void start(Stage stage) throws Exception {
        stage1 = stage;
        Label label1 = new Label("オブジェクト指向プログラミング課題自動採点用XML作成プログラム");
        HBox hbox1 = new HBox(30, label1);
        hbox1.setAlignment(Pos.CENTER);
        hbox1.setPadding(new Insets(15));
        Button btn1 = new Button("レイアウトを取得する");
        btn1.setOnAction(e -> startModelAnswer());
        HBox hbox2 = new HBox(30, btn1);
        hbox2.setAlignment(Pos.CENTER);
        VBox pane1 = new VBox(60, hbox1, hbox2);
        Scene scene1 = new Scene(pane1, 400, 200);
        stage1.setScene(scene1);
        stage1.setTitle("自動採点用XML生成");
        stage1.show();
    }
    
    //GUI起動メソッド
    void startModelAnswer() {
    	primaryStage = new Stage();
    	try {
    		app = new SplitBillApp();
            app.start(primaryStage);
            getNodeList();
            SelectCom();
    	} catch (Exception e) {
            e.printStackTrace();
    	}
    }
    
    //シーン、レイアウトペイン取得メソッド
    void getNodeList() throws Exception {
    	pointMax = 0;
    	cnt = 2;
        // Documentインスタンスの生成
        DocumentBuilder documentBuilder;
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        document = documentBuilder.newDocument();
        //シーン
        Scene scene1 = primaryStage.getScene();      // Scene
        Element scene = document.createElement("Scene");
        scene.setAttribute("ID", "0");
        document.appendChild(scene);
        pointMax++;
        //レイアウトペイン
        Parent root = scene1.getRoot();
        String nodeType = root.getClass().getSimpleName();
        Element Box = document.createElement(nodeType);
        Box.setAttribute("ID", "1");
        
        pane = new CheckBoxTreeItem[1000];
        pane[0] = new CheckBoxTreeItem<String>("Scene");
        pane[1] = new CheckBoxTreeItem<String>(nodeType);
        pane[1].setExpanded(true);		// 初期画面で子ノードを表示させる
        //pane[1].setIndependent(true);		// 子ノードの非依存定義
        pane[1].setSelected(true);
        PaneBox = new CheckBoxTreeItem<String>();
        PaneBox = pane[1];
        
        PaneHantei((Node) root, Box);    //POSデータ判定、書き込み
        //コンポーネント
        getCom(Box, root, PaneBox);
        scene.appendChild(Box);
        pointMax++;
        scene.setAttribute("ScoreMax", String.valueOf(pointMax));
    }
    
    //コンポーネント読み込みメソッド
    void getCom(Element cp, Parent p, CheckBoxTreeItem<String> pb) {
    	ObservableList<Node> children = p.getChildrenUnmodifiable();
        if (children != null) {
            for (int i=0; i<children.size(); i++) {
                String com = children.get(i).getClass().getSimpleName();      // コンポーネント名
                Element Comp = document.createElement(com);
                Comp.setAttribute("ID", String.valueOf(cnt));
                pane[cnt] = new CheckBoxTreeItem<String>(com);
                //テキスト追加処理
                if (children.get(i) instanceof TextField) {
                    TextField tf = (TextField) children.get(i);
                    Comp.appendChild(document.createTextNode(tf.getText()));
                    pane[cnt] = new CheckBoxTreeItem<String>(com + " " + tf.getText());
                }
                else if(children.get(i) instanceof ComboBox) {
                    ComboBox cb = (ComboBox) children.get(i);
                    Comp.appendChild(document.createTextNode(cb.getItems().toString()));
                    pane[cnt] = new CheckBoxTreeItem<String>(com + " " + cb.getItems().toString());
                }
                else {
                    String[] st = children.get(i).toString().split("'");
                    if (st.length == 2) {
                        Comp.appendChild(document.createTextNode((st[1])));
                        pane[cnt] = new CheckBoxTreeItem<String>(com + " " + st[1]);
                    }
                }
                cp.appendChild(Comp);
                //pane[cnt].setIndependent(true);
                //pane[cnt].setSelected(true);
                pb.getChildren().add(pane[cnt]);
                cnt++;
                Node node = children.get(i);
                if (node instanceof Pane) {     //レイアウトペイン判定
                    PaneHantei((Node)node, Comp);
                    pane[cnt-1].setExpanded(true);
                    getCom(Comp, (Parent) node, pane[cnt-1]);
                }
                pointMax++;
            }
        }
    }
    
    //採点項目選択メソッド
    void SelectCom() {
    	// XMLファイルの作成
        File file = new File("StaticTester.xml");
        write(file, document);
    	//stage2 = new Stage();
    	Label lb = new Label("採点する項目を選択してください");
    	TreeView tree = new TreeView(PaneBox);
        tree.setEditable(true);
        tree.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
        //tree.setRoot(pane);
        tree.setShowRoot(true);
        Button button = new Button("決定");
        button.setOnAction(e -> Items());
        VBox root = new VBox(lb, tree, button);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        stage1.setScene(new Scene(root, 300, 250));
        stage1.show();
    }
    
    
    void Items() {
        for(int i=0; i < pane.length; i++) {
        	if(pane[i] != null && pane[i].isSelected())
        		System.out.println(pane[i]);
        }
     
        //primaryStage.close();
        Button btn = new Button("キャプチャ");
        VBox vb = new VBox(btn);
        NameCount = 1;        
        btn.setOnAction(e -> {
			try {
				getNodeList();
				File file = new File("TestTester" + NameCount + ".xml");
		        write(file, document);
		        NameCount++;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
        
        vb.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vb, 400, 200);
        stage1.setScene(scene);
        stage1.show();
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
    
    //Alignment追加メソッド
    void PaneHantei(Node r, Element el) {
        if(r instanceof VBox) {
            VBox pane = (VBox) r;
            el.setAttribute("Alignment", pane.getAlignment().toString());       //Pos配置
            pointMax++;
        }
        else if(r instanceof HBox) {
            HBox pane = (HBox) r;
            el.setAttribute("Alignment", pane.getAlignment().toString());
            pointMax++;
        }
    }
    
    //テキスト追加メソッド
    void addText(Element el, Node node) {
        if (node instanceof TextField) {
            TextField tf = (TextField) node;
            if(tf.isDisable())
            	el.appendChild(document.createTextNode(tf.getText()));
        }
        else if(node instanceof ComboBox) {
            ComboBox cb = (ComboBox) node;
            el.appendChild(document.createTextNode(cb.getItems().toString()));
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
