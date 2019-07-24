import java.io.File;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author yoshi
 */
public class FXTester extends Application {
    
	CheckSIDApp app;
	String appName = "CheckSIDApp";
    Document document;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    int cnt = 2;
    LinkedHashMap<Integer, Node> comList;
            
    @Override
    public void start(Stage primaryStage) {
    	System.out.println("静的テスト開始");
    	try {
    		//ここに採点するクラス名を入力
            app = new CheckSIDApp();
            app.start(primaryStage);
            System.out.println("起動：成功");
            //模範解答XML読み込み
            File TesterFile = new File("StaticTester.xml");
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document Tester = builder.parse(TesterFile);
            //Scene
            Element TesterScene = Tester.getDocumentElement();
            
            Scene TesteeScene = primaryStage.getScene();
            //RootNode
            Element TesterRoot = (Element) TesterScene.getChildNodes().item(1);
            String RootPos = TesterRoot.getAttribute("Alignment");
            
            String TesteeRoot = TesteeScene.getRoot().getClass().getSimpleName();
            
            if(TesterRoot.getNodeName().equals(TesteeRoot)) {
            	System.out.println(TesterRoot.getNodeName() + "：OK");
            }
            else {
            	System.out.println(TesterRoot.getNodeName() + "：NG");
            	System.out.println(TesteeRoot + "ではありません");
            }
            
            //getCom(TesterRoot);
            
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	primaryStage.close();
    }
    
    void getCom(Element tr) {
    	NodeList ComList = tr.getChildNodes();
    	for(int i=0; i<ComList.getLength(); i++) {
    		if(ComList.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
    			System.out.println(ComList.item(i));
    	}
    }
    
    public static void main(String[] args) {
        Application.launch(args);
    }
}

