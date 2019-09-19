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
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
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
	String appName = "SplitBillApp";
    Document document;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    int cnt = 2;
    int point = 0;		//学生の点数格納
    int pointMax = 0;		//満点
    int dpoint = 0;		//動的テストの点数
    LinkedHashMap<Integer, Node> comList;		//学生のコンポーネント格納
            
    @Override
    public void start(Stage primaryStage) {
		//ここに採点するクラス名を入力
    	SplitBillApp app = new SplitBillApp();
        app.start(primaryStage);
        
        System.out.println("静的テスト開始");
        StaticTest(primaryStage);
    	if (point == pointMax) {
    		System.out.println("静的テスト成功");
    		System.out.println("動的テスト開始");
    		//DynamicTest();
    	}
    	else
    		System.out.println("静的テスト失敗");
    	
    	System.out.println(String.format("【実行対象:%s, 学籍番号:%s, 名前:%s, 点数:%d】", 
    			appName, app.gakuban, app.yourname, (10 * point / pointMax) + dpoint));
    	primaryStage.close();
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
	        	point++;
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
	        if(nodetype.equals(TesteeRoot.getClass().getSimpleName())) {
	        	System.out.println(nodetype + "：OK");
	        	point++;
	        }
	        else {
	        	System.out.println(nodetype + "：NG");
	        	System.out.println(TesteeRoot.getClass().getSimpleName() + "ではありません");
	        	return;
	        }
	        //Alignment
	        PaneHantei(TesterRoot, (Node)TesteeRoot);
	        
	        getCom(TesterRoot, TesteeRoot);
        
		} catch(Exception e) {
			e.printStackTrace();
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
        	System.out.println(TesterAlignment + "：OK");
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
			String TesterNodeName = TesterNode.getNodeName();
			j++;
			Node TesteeNode = TesteeNodes.get(i);
			String TesteeNodeName = null;
			if(i < TesteeNodes.size()) {
				TesteeNodeName = TesteeNode.getClass().getSimpleName();
				comList.put(i+2, TesteeNodes.get(i));
			}
			if(TesterNodeName.equals(TesteeNodeName)) {
				System.out.println(TesterNodeName + "：OK");
				point++;
				//Paneの場合
				if (TesteeNode instanceof Pane) {
					PaneHantei((Element) TesterNode, TesteeNode);
					getCom((Element)TesterNode, (Parent)TesteeNode);
				}
			}
			else {
				System.out.println(TesterNode + "：NG");
				System.out.println(TesteeNode + "ではありません。");
				return;
			}
    	}
    }
    
    
    void DynamicTest() {
    	for(int testcase=1; testcase<10; testcase++) {
	    	try {
	    		File TesterFile = new File("DynamicTester" + testcase + ".xml");
	        	DocumentBuilder builder = factory.newDocumentBuilder();
	            Document Tester = builder.parse(TesterFile);
	            //Scene
		        Element TesterScene = Tester.getDocumentElement();
		        //RootNode
		        Element TesterRoot = (Element) TesterScene.getChildNodes().item(1);
		        String nodetype = TesterRoot.getNodeName();
		        //Component
		        NodeList TesterNodes = TesterRoot.getChildNodes();
		        int j = 1;		//XML調整用
		    	for(int i=0; i<(TesterNodes.getLength()/2); i++) {
					Element TesterNodeElement = (Element) TesterNodes.item(i+j);
					j++;
					if(TesterNodeElement.getAttribute("Input").equals("true")) {
						int num = Integer.parseInt(TesterNodeElement.getAttribute("ID"));
						InputAction(num, TesterNodeElement);
					}
					if(TesterNodeElement.getAttribute("Event").equals("true")) {
						int num = Integer.parseInt(TesterNodeElement.getAttribute("ID"));
						EventAction(num, TesterNodeElement);
					}
					if(TesterNodeElement.getAttribute("Output").equals("true")) {
						int num = Integer.parseInt(TesterNodeElement.getAttribute("ID"));
						if(OutputAction(num, TesterNodeElement)) {
							System.out.println("テストケース" + testcase + "成功");
							dpoint++;
						}
						else {
							System.out.println("テストケース" + testcase + "失敗");
							return;
						}
					}
		    	}
		    	
	    	} catch(Exception e) {
				e.printStackTrace();
			}
    	}
    }
    
    void InputAction(int key, Element TesterElement) {
    	Node TesterNode = comList.get(key);
    	if(TesterNode instanceof TextInputControl) {
    		TextInputControl tic = (TextInputControl) TesterNode;
    		tic.setText(TesterElement.getTextContent());
    	}
    	System.out.println("入力：" + TesterElement.getTextContent());
    	
    }
    
    void EventAction(int key, Element TesterElement) {
    	Node TesterNode = comList.get(key);
    	if(TesterNode instanceof ButtonBase) {
    		ButtonBase bb = (ButtonBase) comList.get(key);
    		bb.fire();
    	}
    }
    
    boolean OutputAction(int key, Element TesterElement) {
    	String TesteeOut = null;
    	Node TesterNode = comList.get(key);
    	//出力がラベル
    	if(TesterNode instanceof Label) {
    		Label label = (Label) TesterNode;
    		TesteeOut = label.getText();
    	}
    	//出力がテキストフィールド
    	else if (TesterNode instanceof TextInputControl) {
    		TextInputControl tic = (TextInputControl) TesterNode;
    		TesteeOut = tic.getText();
    	}
    	
    	System.out.println("出力：" + TesteeOut);
		if(TesteeOut.equals(TesterElement.getTextContent())) {
			return true;
		}
		else {
			System.out.println(TesteeOut + "ではありません。");
			return false;
		}
    }
    
    public static void main(String[] args) {
        Application.launch(args);
    }
}
