import java.io.File;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author yoshi
 */
public class FXTester extends Application {

	String appName = "AddTaxApp";
	AddTaxApp app;		//テストするプログラミング課題
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    int point = 0;		//学生の点数格納
    int pointMax = 0;		//満点
    int dpoint = 0;		//動的テストの点数
    int tcsize = 12;		//テストケースの数
    LinkedHashMap<Integer, Node> comList;		//学生のコンポーネント格納
	LinkedHashMap<Integer, Element> InputList;		//入力コンポーネント格納
	int Eventnum = 0;		//イベントID
	LinkedHashMap<Integer, Element> OutputList;		//出力コンポーネント格納

    @Override
    public void start(Stage primaryStage) {
		//ここに採点するクラス名を入力
    	app = new AddTaxApp();
        app.start(primaryStage);

        System.out.println("静的テスト開始");
        StaticTest(primaryStage);
        System.out.println(point);
    	if (point == pointMax) {
    		System.out.println("静的テスト成功");
    		DynamicTest();
    	}
    	else
    		System.out.println("静的テスト失敗");
    	printScore();
    	//primaryStage.close();
    }

    void StaticTest(Stage primaryStage) {
    	comList = new LinkedHashMap<>();
    	try {
	    	//模範解答XML読み込み
	        File TesterFile = new File("StaticTester.xml");
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document Tester = builder.parse(TesterFile);
	        //Scene
	        Element TesterScene = Tester.getDocumentElement();
	        pointMax = Integer.parseInt(TesterScene.getAttribute("ScoreMax"));
	        //起動確認
	        Scene TesteeScene = primaryStage.getScene();
	        if(TesteeScene != null) {
	        	System.out.println("起動：OK");
	        }
	        else {
	        	System.out.println("起動：NG");
	        	return;
	        }
	        //RootNode
	        Element TesterRoot = (Element) TesterScene.getChildNodes().item(1);
	        Parent TesteeRoot = TesteeScene.getRoot();
	        String nodetype = TesterRoot.getNodeName();
	        comList.put(1, (Node) TesteeRoot);
	        //比較
	        if(TesterRoot.getAttribute("Score").equals("1")) {
		        if(nodetype.equals(TesteeRoot.getClass().getSimpleName())) {
	        		PaneHantei(TesterRoot, (Node)TesteeRoot);
		        }
		        else {
		        	System.out.println(nodetype + "：NG");
		        	System.out.println(TesteeRoot.getClass().getSimpleName() + "ではありません");
		        	return;
		        }
	        }
	        getCom(TesterRoot, TesteeRoot);

		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
    }

	void PaneHantei(Element TesterElement, Node TesteeNode) {
		String TesterAlignment = TesterElement.getAttribute("Alignment");
        String TesteeAlignment = null;
        //VBox
        if(TesteeNode instanceof VBox) {
        	VBox pane = (VBox) TesteeNode;
	        TesteeAlignment = pane.getAlignment().toString();
        }
        //HBox
        else if (TesteeNode instanceof HBox) {
        	HBox pane = (HBox) TesteeNode;
	        TesteeAlignment = pane.getAlignment().toString();
        }
        //比較
    	if(TesterAlignment.equals(TesteeAlignment)) {
        	System.out.println(TesteeNode.getClass().getSimpleName() + "：OK");
    		point++;
        }
        else {
        	System.out.println(TesterAlignment + "：NG");
        	System.out.println(TesteeAlignment + "ではありません。");
        	return;
        }
    }

    //コンポーネント取得メソッド
    void getCom(Element TesterElement, Parent TesteeParent) {
    	NodeList TesterNodes = TesterElement.getChildNodes();
    	ObservableList<Node> TesteeNodes = TesteeParent.getChildrenUnmodifiable();

    	int j = 1;		//XML調整用
    	for(int i=0; i<(TesterNodes.getLength()/2); i++) {
    		org.w3c.dom.Node TesterNode = TesterNodes.item(i+j);
    		j++;
			String TesterNodeName = TesterNode.getNodeName();
			Node TesteeNode = TesteeNodes.get(i);
			String TesteeNodeName = null;
			if(i < TesteeNodes.size()) {
				TesteeNodeName = TesteeNode.getClass().getSimpleName();
				Element aa = (Element) TesterNode;
				comList.put(Integer.parseInt(aa.getAttribute("ID")), TesteeNodes.get(i));
			}

			if(TesterNodeName.equals(TesteeNodeName)) {
				System.out.println(TesterNodeName + "：OK");
				if(TesterElement.getAttribute("Score").equals("1"))
	        		point++;

			}
			else {
				System.out.println(TesterNodeName + "：NG");
				System.out.println(TesteeNodeName + "ではありません。");
				return;
			}

			//Paneの場合
			if (TesteeNode instanceof Pane) {
				//PaneHantei((Element) TesterNode, TesteeNode);
				getCom((Element)TesterNode, (Parent)TesteeNode);
			}
    	}
    }

    void DynamicTest() {
    	System.out.println("動的テスト開始");
    	for(int testcase=1; testcase<=tcsize; testcase++) {
    		InputList = new LinkedHashMap<>();
        	OutputList = new LinkedHashMap<>();
	    	try {
	    		File TesterFile = new File("DynamicTester" + testcase + ".xml");
	        	DocumentBuilder builder = factory.newDocumentBuilder();
	            Document Tester = builder.parse(TesterFile);
	            //Scene
		        Element TesterScene = Tester.getDocumentElement();
		        //RootNode
		        Element TesterRoot = (Element) TesterScene.getChildNodes().item(1);
		        getXMLNode(TesterRoot);

		        //動的テスト開始
		    	InputAction(InputList);
		    	EventAction(Eventnum);
		    	if(OutputAction(OutputList)) {
					System.out.println("テストケース" + testcase + "成功");
					dpoint++;
				}
				else {
					System.out.println("テストケース" + testcase + "失敗");
					return;
				}
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    		return;
			}
    	}
    }

    //子ノード取得メソッド（XML）
    void getXMLNode(Element element) {
    	try {
    		//Component
            NodeList TesterNodes = element.getChildNodes();
        	for(int i=0; i<(TesterNodes.getLength()); i++) {
        		org.w3c.dom.Node TesterNode = TesterNodes.item(i);
        		if(TesterNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
    				Element TesterNodeElement = (Element) TesterNode;
    				//レイアウトペインの場合
    				if(TesterNode.getChildNodes() != null) {
    					getXMLNode(TesterNodeElement);
    				}

    				int num = Integer.parseInt(TesterNodeElement.getAttribute("ID"));
    				if(TesterNodeElement.getAttribute("Input").equals("true")) {
    					InputList.put(num, TesterNodeElement);
    				}
    				if(TesterNodeElement.getAttribute("Event").equals("true")) {
    					Eventnum = num;
    				}
    				if(TesterNodeElement.getAttribute("Output").equals("true")) {
    					OutputList.put(num, TesterNodeElement);
    				}
        		}
        	}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    //入力値セットメソッド
    void InputAction(LinkedHashMap<Integer, Element> list) {
    	for(int key : list.keySet()) {
    		Node TesteeNode = comList.get(key);
    		String TesteeIn = null;
        	if(TesteeNode instanceof TextInputControl) {
        		TextInputControl tic = (TextInputControl) TesteeNode;
        		tic.setText(list.get(key).getTextContent());
        		TesteeIn = tic.getText();
        	}
        	if(TesteeNode instanceof RadioButton) {
        		RadioButton rb = (RadioButton) TesteeNode;
        		if(list.get(key).getTextContent().equals("1")){
        			rb.setSelected(true);
        			TesteeIn = rb.getText();
        		}
        		else {
        			rb.setSelected(false);
        		}
        	}
        	if(TesteeNode instanceof CheckBox) {
        		CheckBox cb = (CheckBox) TesteeNode;
        		if(list.get(key).getTextContent().equals("1")){
        			cb.setSelected(true);
        			TesteeIn = cb.getText();
        		}
        		else {
        			cb.setSelected(false);
        		}
        	}
        	if (TesteeIn != null)
        		System.out.println("入力：" + TesteeIn);
    	}
    }

    //イベント動作メソッド
    void EventAction(int key) {
    	Node TesterNode = comList.get(key);
    	if(TesterNode instanceof ButtonBase) {
    		ButtonBase bb = (ButtonBase) comList.get(key);
    		bb.fire();
    	}
    }

    //出力値取得メソッド
    boolean OutputAction(LinkedHashMap<Integer, Element> list) {
    	String TesteeOut = null;
		for (int key: list.keySet()) {
			Node TesterNode = comList.get(key);
			Dialog c = null;
			if (c instanceof Alert) {
				Alert alert = (Alert) c;
	    		TesteeOut =  alert.getAlertType().toString();
	    	}
        	//出力がラベル
        	else if(TesterNode instanceof Label) {
        		Label label = (Label) TesterNode;
        		TesteeOut = label.getText();
        	}
        	//出力がテキストフィールド
        	else if (TesterNode instanceof TextInputControl) {
        		TextInputControl tic = (TextInputControl) TesterNode;
        		TesteeOut = tic.getText();
        	}

        	System.out.println("出力：" + TesteeOut);
    		if(!TesteeOut.equals(list.get(key).getTextContent())) {
    			System.out.println(TesteeOut + "ではありません。");
    			return false;
    		}
    	}
    	return true;
    }

    void printScore() {
    	System.out.println(String.format("【実行対象:%s, 学籍番号:%s, 名前:%s, 点数:%d】",
    			appName, app.gakuban, app.yourname, (10 * (point + dpoint) / (pointMax + tcsize))));
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}