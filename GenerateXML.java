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
import org.w3c.dom.NodeList;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author yoshi
 */

public class GenerateXML extends Application {

    Stage stage1, primaryStage;
    Document document;
    CheckBoxTreeItem<String>[] pane;	//コンポーネントのチェックボックス
    CheckBoxTreeItem<String> PaneBox;
    CheckSIDApp app;	//採点するGUI課題
    Label lb2;
    TextArea ta;
    int cnt;	//ノード取得用
    int pointMax;	//満点

    @Override
    public void start(Stage stage) throws Exception {
    	//初期画面
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
        stage.setScene(scene1);
        stage.setTitle("自動採点用XML生成");
        stage.show();
    }

    //GUI起動
    void startModelAnswer() {
    	primaryStage = new Stage();
    	try {
    		app = new CheckSIDApp();
            app.start(primaryStage);
            getNodeList();
            SelectCom();
    	} catch (Exception e) {
            e.printStackTrace();
    	}
    }

    //レイアウトペイン取得メソッド（チェックボックス作成用）
    void getNodeList() throws Exception {
    	pointMax = 0;
    	cnt = 2;
    	pane = new CheckBoxTreeItem[100];	//100個まで
        // Documentインスタンスの生成
        DocumentBuilder documentBuilder;
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        document = documentBuilder.newDocument();
        //シーン
        Scene scene = primaryStage.getScene();      // Scene
        //レイアウトペイン
        Parent root = scene.getRoot();
        String nodeType = root.getClass().getSimpleName();
        //チェックボックス生成
        pane[0] = new CheckBoxTreeItem<String>("Scene");
        pane[1] = new CheckBoxTreeItem<String>(nodeType);
        pane[1].setExpanded(true);
        pane[1].setSelected(true);
        PaneBox = new CheckBoxTreeItem<String>();
        PaneBox = pane[1];
        //コンポーネント取得
        getCom(root, PaneBox);
    }

    //コンポーネント取得メソッド（チェックボックス作成用）
    void getCom(Parent p, CheckBoxTreeItem<String> pb) {
    	ObservableList<Node> children = p.getChildrenUnmodifiable();
        if (children != null) {
            for (int i=0; i<children.size(); i++) {
                String com = children.get(i).getClass().getSimpleName();      // コンポーネント名
                //テキスト追加処理
                String comText = addText(children.get(i));
                if(comText != null)
                	com = com + " " + comText;
                pane[cnt] = new CheckBoxTreeItem<String>(com);
                pane[cnt].setSelected(true);
                
                pb.getChildren().add(pane[cnt]);
                cnt++;
                Node node = children.get(i);
                if (node instanceof Pane) {     //レイアウトペイン判定
                    pane[cnt-1].setExpanded(true);
                    getCom((Parent) node, pane[cnt-1]);
                }
                else if (node instanceof MenuBar) {     //メニューバー判定
                    pane[cnt-1].setExpanded(true);
                    getCom((Parent) node, pane[cnt-1]);
                }
                else if (node instanceof BarChart) {     //レイアウトペイン判定
                    pane[cnt-1].setExpanded(true);
                    getCom((Parent) node, pane[cnt-1]);
                }
            }
        }
    }
    
    //テキスト追加メソッド
    String addText(Node node) {
        if (node instanceof TextField) {
            TextField tf = (TextField) node;
    		return tf.getText();
        }
        else if(node instanceof ComboBox) {
        	ComboBox cb = (ComboBox) node;
            return cb.getItems().toString();
        }
        else {
            String[] st = node.toString().split("'");
            if (st.length == 2)
            	return st[1];
        }
        return null;
    }

    

    //採点項目選択画面
    void SelectCom() {
    	Label lb = new Label("採点する項目を選択してください");
    	TreeView<String> tree = new TreeView<String>(PaneBox);
        tree.setEditable(true);
        tree.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
        tree.setShowRoot(true);
        Button button = new Button("決定");
        button.setOnAction(e -> {
        	try {
        		setPoint();
				File file = new File("StaticTester.xml");
	            write(file, document);
	            System.out.println("採点項目：");
	        	for(int i=0; i < pane.length; i++) {
	            	if(pane[i] != null) {
	            		if(pane[i].isSelected())
	            			System.out.println(pane[i].getValue());
	                	pane[i].setIndependent(true);
	            		pane[i].setSelected(false);
	            	}
	        	}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        	SelectInput();
        });
        VBox root = new VBox(lb, tree, button);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        stage1.setScene(new Scene(root, 300, 250));
        stage1.show();
    }

    //採点項目書き込みメソッド
    void setPoint() throws Exception {
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
        //レイアウトペイン
        Parent root = scene1.getRoot();
        String nodeType = root.getClass().getSimpleName();
        Element Box = document.createElement(nodeType);
        Box.setAttribute("ID", "1");
        if(pane[1].isSelected()) {
        	Box.setAttribute("Score", "1");
        	pointMax++;
        }
        //Alignment書き込み
        PaneHantei((Node) root, Box);
        //コンポーネント
        getPointCom(Box, root);
        scene.appendChild(Box);
        scene.setAttribute("ScoreMax", String.valueOf(pointMax));
    }
    
    void getPointCom(Element cp, Parent p) {
    	ObservableList<Node> children = p.getChildrenUnmodifiable();
        if (children != null) {
            for (int i=0; i<children.size(); i++) {
                String com = children.get(i).getClass().getSimpleName();      // コンポーネント名
                Element Comp = document.createElement(com);
                Comp.setAttribute("ID", String.valueOf(cnt));
                if(pane[cnt].isSelected()) {
                	Comp.setAttribute("Score", "1");
                	pointMax++;
                }
                //テキスト追加処理
                String comText = addText(children.get(i));
                if(comText != null) {
                	Comp.appendChild(document.createTextNode(comText));
                }
                addEvent(children.get(i), Comp);
                cp.appendChild(Comp);
                cnt++;
                Node node = children.get(i);
                if (node instanceof Pane) {     //レイアウトペイン判定
                    PaneHantei((Node)node, Comp);
                    getPointCom(Comp, (Parent) node);
                }
            }
        }
    }
    
    //Alignment追加メソッド
    void PaneHantei(Node r, Element el) {
        if(r instanceof VBox) {
            VBox pane = (VBox) r;
            el.setAttribute("Alignment", pane.getAlignment().toString());
        }
        else if(r instanceof HBox) {
            HBox pane = (HBox) r;
            el.setAttribute("Alignment", pane.getAlignment().toString());
        }
        else if(r instanceof BorderPane) {
            BorderPane pane = (BorderPane) r;
        }
        else if(r instanceof GridPane) {
            GridPane pane = (GridPane) r;
            el.setAttribute("Alignment", pane.getAlignment().toString());
        }
        else if(r instanceof FlowPane) {
            FlowPane pane = (FlowPane) r;
            el.setAttribute("Alignment", pane.getAlignment().toString());
        }
    }
    
    //イベント取得メソッド
    void addEvent(Node node, Element el) {
    	if(node instanceof ButtonBase) {
    		ButtonBase button = (ButtonBase) node;
    		if (button.getOnAction() != null) {
    			Button EventButton = new Button();
            	EventButton.setOnAction(button.getOnAction());
            	button.setOnAction(e -> {
            		try {
            			if (el.getAttribute("Event").equals("true")) {
            				setTestcase("Input");
            				EventButton.fire();
            				setTestcase("Output");
            			}
    				} catch (Exception e1) {
    					e1.printStackTrace();
    				}
            	});
    		}
        }
    }
    
    //入力選択画面
    void SelectInput() {
    	Label lb = new Label("入力を選択してください");
    	CheckBoxTreeItem<String>[] Inputpane = pane;
    	PaneBox = Inputpane[1];
    	TreeView<String> tree = new TreeView<String>(PaneBox);
        tree.setEditable(true);
        tree.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
        tree.setShowRoot(true);
        Button button = new Button("決定");
        button.setOnAction(e -> {
        	try {
        		System.out.println("入力：");
				setInOut("Input", Inputpane);
				SelectEvent();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        });
        VBox root = new VBox(lb, tree, button);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        stage1.setScene(new Scene(root, 300, 250));
        stage1.show();
    }

    //イベント選択画面
    void SelectEvent() {
    	for (int i=0; i<pane.length; i++) {
    		if (pane[i] != null)
    			pane[i].setSelected(false);
    	}
    	Label lb = new Label("イベントを選択してください");
    	CheckBoxTreeItem<String>[] Eventpane = pane;
    	PaneBox = Eventpane[1];
    	TreeView<String> tree = new TreeView<String>(PaneBox);
        tree.setEditable(true);
        tree.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
        tree.setShowRoot(true);
        Button button = new Button("決定");
        button.setOnAction(e -> {
        	try {
        		System.out.println("イベント：");
				setInOut("Event", Eventpane);
	        	SelectOutput();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        });
        VBox root = new VBox(lb, tree, button);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        stage1.setScene(new Scene(root, 300, 250));
        stage1.show();
    }
    
    //出力選択画面
    void SelectOutput() {
    	for (int i=0; i<pane.length; i++) {
    		if (pane[i] != null)
    			pane[i].setSelected(false);
    	}
    	Label lb = new Label("出力を選択してください");
    	CheckBoxTreeItem<String>[] Eventpane = pane;
    	PaneBox = Eventpane[1];
    	TreeView<String> tree = new TreeView<String>(PaneBox);
        tree.setEditable(true);
        tree.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
        tree.setShowRoot(true);
        Button button = new Button("決定");
        button.setOnAction(e -> {
        	try {
        		System.out.println("出力：");
				setInOut("Output", Eventpane);
				File file = new File("DynamicTester.xml");
	        	write(file, document);
	        	Items();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        });
        VBox root = new VBox(lb, tree, button);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        stage1.setScene(new Scene(root, 300, 250));
        stage1.show();
    }
    
    //入出力書き込みメソッド
    void setInOut(String s, CheckBoxTreeItem<String>[] cbti) throws Exception {
    	pointMax = 0;
    	cnt = 2;
    	Element TesterScene = document.getDocumentElement();
    	Element TesterRoot = (Element) TesterScene.getChildNodes().item(0);
    	getInOutCom(TesterRoot, s, cbti);
    }
    
    void getInOutCom(Element cp, String s, CheckBoxTreeItem<String>[] cbti) {
    	NodeList TesterNodes = cp.getChildNodes();
        for (int i=0; i<TesterNodes.getLength(); i++) {
        	org.w3c.dom.Node TesterNode = TesterNodes.item(i);
        	if(TesterNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
	        	Element TesterElement = (Element) TesterNode;
	        	
	        	if (cbti[cnt].isSelected()) {
	        		System.out.println(cbti[cnt].getValue());
	        		TesterElement.setAttribute(s, "true");
	        	}
	        	cnt++;
	        	if (TesterElement.getChildNodes() != null) {
	        		getInOutCom(TesterElement, s, cbti);
	        		if(s.equals("Event")) {
	        			//addEvent(TesterNode);
	        		}
	        	}
        	}
        }
    }
    
    //テストケース作成画面
    void Items() {
    	for (int i=0; i<pane.length; i++) {
    		if (pane[i] != null)
    			pane[i].setSelected(false);
    	}
    	Label lb1 = new Label("GUIを操作してください");
    	lb2 = new Label();
    	Button btn1 = new Button("再設定");
    	btn1.setOnAction(e -> {
    		SelectInput();
    	});
        Button btn4 = new Button("終了する");
        VBox vb1 = new VBox(30, lb1, lb2, btn1, btn4);
        vb1.setAlignment(Pos.CENTER);
        ta = new TextArea();
        ta.setPrefColumnCount(20);;
        ta.setEditable(false);
        VBox vb2 = new VBox(ta);
        vb2.setAlignment(Pos.CENTER);
        HBox hb = new HBox(40, vb1, vb2);
        hb.setAlignment(Pos.CENTER);
        btn4.setOnAction(e -> {
        	primaryStage.close();
        	stage1.close();
        });

        Scene scene = new Scene(hb, 500, 300);
        stage1.setScene(scene);
        stage1.show();
    }
    
  //テストケースの入出力値取得メソッド
    void setTestcase(String s) throws Exception {
    	pointMax = 0;
    	cnt = 2;
    	//模範解答XML読み込み
        File TesterFile = new File("DynamicTester.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(TesterFile);
        //シーン
        Scene scene1 = primaryStage.getScene();      // Scene
        Element scene = document.getDocumentElement();
        //レイアウトペイン
        Parent root = scene1.getRoot();
        Element Box = (Element) scene.getChildNodes().item(1);
        //コンポーネント
        getTestcasecom(Box, root);
    }
    
    //コンポーネント読み込みメソッド
    void getTestcasecom(Element cp, Parent p) {
    	NodeList TesterNodes = cp.getChildNodes();
        for (int i=0; i<TesterNodes.getLength(); i++) {
        	org.w3c.dom.Node TesterNode = TesterNodes.item(i);
        	if(TesterNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
	        	Element TesterElement = (Element) TesterNode;
	        	
	        	if (TesterElement.getAttribute("Input").equals("true")) {
	        		
	        	}
	        	cnt++;
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

    public static void main(String[] args) {
        Application.launch(args);
    }
}