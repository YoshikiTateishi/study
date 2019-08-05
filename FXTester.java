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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author yoshi
 */
public class FXTester extends Application {
	String appName = "CheckSIDApp";
    Document document;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    int cnt = 2;
    int point = 0;		//学生の点数格納
    int pointMax = 0;		//満点
    LinkedHashMap<Integer, Node> comList;
            
    @Override
    public void start(Stage primaryStage) {
		//ここに採点するクラス名を入力
    	CheckSIDApp app = new CheckSIDApp();
        app.start(primaryStage);
        
        System.out.println("静的テスト開始");
        StaticTest(primaryStage);
    	
    	if (point == pointMax) {
    		System.out.println("静的テスト成功");
    		System.out.println("動的テスト開始");
    		DynamicTest();
    	}
    	else
    		System.out.println("静的テスト失敗");
    	
    	System.out.println(String.format("【実行対象:%s, 学籍番号:%s, 名前:%s, 点数:%d】", 
    			appName, app.gakuban, app.yourname, 10 * point / pointMax));
    	primaryStage.close();
    }
    
    void StaticTest(Stage primaryStage) {
    	try {
	    	//模範解答XML読み込み
	        File TesterFile = new File("StaticTester.xml");
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document Tester = builder.parse(TesterFile);
	        
	        //Scene
	        Element TesterScene = Tester.getDocumentElement();
	        pointMax = Integer.parseInt(TesterScene.getAttribute("ScoreMax"));
	        
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
	        if(nodetype.equals(TesteeRoot.getClass().getSimpleName())) {
	        	System.out.println(nodetype + "：OK");
	        	point++;
	        }
	        else {
	        	System.out.println(nodetype + "：NG");
	        	System.out.println(TesteeRoot.getClass().getSimpleName() + "ではありません");
	        	return;
	        }
	        
	        String TesterAlignment = TesterRoot.getAttribute("Alignment");
	        VBox pane = (VBox) TesteeRoot;
	        String TesteeAlignment = pane.getAlignment().toString();
	        if(TesterAlignment.equals(TesteeAlignment)) {
	        	System.out.println(TesterAlignment + "：OK");
	        	point++;
	        }
	        else {
	        	System.out.println(TesterAlignment + "：NG");
	        	System.out.println(TesteeAlignment + "ではありません。");
	        	return;
	        }
	        
	        getCom(TesterRoot, TesteeRoot);
        
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    void DynamicTest() {
    	
    }
    
    void getCom(Element tr, Parent r) {
    	NodeList TesterNodes = tr.getChildNodes();
    	ObservableList<Node> TesteeNodes = r.getChildrenUnmodifiable();
    	int j = 1;		//XML調整用
    	for(int i=0; i<(TesterNodes.getLength()/2); i++) {
			String TesterNode = TesterNodes.item(i+j).getNodeName();
			j++;
			String TesteeNode = null;
			if(i < TesteeNodes.size()) {
				TesteeNode = TesteeNodes.get(i).getClass().getSimpleName();
			}
			if(TesterNode.equals(TesteeNode)) {
				System.out.println(TesterNode + "：OK");
				point++;
			}
			else {
				System.out.println(TesterNode + "：NG");
				System.out.println(TesteeNode + "ではありません。");
				return;
			}
    	}
    }
    
    public static void main(String[] args) {
        Application.launch(args);
    }
}
