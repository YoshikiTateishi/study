
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class FXTester extends Application {
	
	TicketCalculator app;
	String appName = "TicketCalculator";
    Stage primaryStage;
    Document document;
    int cnt = 2;
    LinkedHashMap<Integer, Node> comList;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        //ここに採点するクラス名を入力
        app = new TicketCalculator();
        app.start(primaryStage);
        getNodeList();
    }
    
    //シーン、レイアウトペイン取得メソッド
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
        //PaneHantei((Node) root, Box);    //POSデータ判定、書き込み
        //コンポーネント
        //getCom(Box, root);
        scene.appendChild(Box);
    }

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		Application.launch(args);
	}

}
