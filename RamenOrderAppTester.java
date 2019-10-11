package xml;

import java.util.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.paint.Paint;

public class RamenOrderAppTester extends Application {

    RamenOrderApp app;
    String appName = "RamenOrderApp";
    Stage myStage, primaryStage;
    Button button1, button2, button3, button4;
    TextArea textarea5;
    LinkedHashMap<String, Node> sceneGraph;
    ArrayList<String> nodeKeys;

    @Override
    public void start(Stage stage) {
        myStage = stage;
        Label label0 = new Label("ラーメン注文アプリ・テストプログラム");
        HBox hbox0 = new HBox(10, label0);
        hbox0.setAlignment(Pos.CENTER);
        hbox0.setPadding(new Insets(10));

        button1 = new Button("テスト開始");
        Label label1 = new Label("未実行");
        button1.setOnAction(e -> startTest(label1));
        HBox hbox1 = new HBox(10, button1, label1);
        hbox1.setAlignment(Pos.CENTER);

        button2 = new Button("静的テスト");
        button2.setDisable(true);
        Label label2 = new Label("未実行");
        button2.setOnAction(e -> staticTest(label2));
        HBox hbox2 = new HBox(10, button2, label2);
        hbox2.setAlignment(Pos.CENTER);

        button3 = new Button("動的テスト");
        button3.setDisable(true);
        Label label3 = new Label("未実行");
        button3.setOnAction(e -> dynamicTest(label3));
        HBox hbox3 = new HBox(10, button3, label3);
        hbox3.setAlignment(Pos.CENTER);

        button4 = new Button("テスト終了");
        button4.setOnAction(e -> finishTest());
        HBox hbox4 = new HBox(10, button4);
        hbox4.setAlignment(Pos.CENTER_RIGHT);

        textarea5 = new TextArea();
        textarea5.setEditable(false);
        HBox hbox5 = new HBox(10, textarea5);

        VBox vbox = new VBox(20, hbox0, hbox1, hbox2, hbox3, hbox4, hbox5);
        vbox.setPadding(new Insets(10));
        Scene myScene = new Scene(vbox, 400, 500);
        myStage.setScene(myScene);
        myStage.setTitle(this.getClass().getSimpleName());
        myStage.show();
    }

    void startTest(Label label) {
        primaryStage = new Stage();
        try {
            app = new RamenOrderApp();
            app.start(primaryStage);
            label.setText("成功");
            button1.setDisable(true);
            button2.setDisable(false);
            myStage.toFront();
        } catch (Exception ex) {
            label.setText("エラー");
            ex.printStackTrace();
        }
    }

    void staticTest(Label label) {
        try {
            Scene scene = primaryStage.getScene();
            Parent root = scene.getRoot();
            sceneGraph = new LinkedHashMap<>();
            sceneGraph.put("0", root);
            getSubgraph("0", root);
            label.setText("成功");
            button2.setDisable(true);
            button3.setDisable(false);

            nodeKeys = new ArrayList<>();
            String text = "";
            for (String key : sceneGraph.keySet()) {
                Node node = sceneGraph.get(key);
                if (node instanceof StackPane) {
                } else {
                    nodeKeys.add(key);
                    text += key + ":" + node.toString() + "\n";
                }
            }
            textarea5.setText(text);
        } catch (Exception ex) {
            label.setText("エラー");
            ex.printStackTrace();
        }
    }

    void getSubgraph(String id, Parent root) {
        ObservableList<Node> children = root.getChildrenUnmodifiable();
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                Node node = children.get(i);
                if (node instanceof Parent) {
                    String newId = id + i;
                    sceneGraph.put(newId, node);
                    getSubgraph(newId, (Parent) node);
                }
            }
        }
    }

    void dynamicTest(Label label) {
        String action = "042";
        String check = "041";
        Random random = new Random();
        String text = "";
        try {
            for (int i = 0; i < 100; i++) {
                int r = random.nextInt(nodeKeys.size());
                String key = nodeKeys.get(r);
                String done = actNode(sceneGraph.get(key));
                if (done != null) {
                    text += key + " " + done + "\n";
                    if (key.equals(action)) {
                        break;
                    }
                }
            }
            String done = actNode(sceneGraph.get(check));
            text += check + " " + done + "\n";
            textarea5.setText(text);
            if (!done.contains("1350")) {
                label.setText("成功");
                label.setTextFill(Paint.valueOf("black"));
            } else {
                label.setText("失敗");
                label.setTextFill(Paint.valueOf("red"));
            }
        } catch (Exception ex) {
            label.setText("エラー");
            ex.printStackTrace();
        }
    }

    String actNode(Node node) {
        String result = node.getClass().getSimpleName();
        if (node instanceof ButtonBase) {
            ButtonBase bb = (ButtonBase) node;
            bb.fire();
            if (bb instanceof CheckBox) {
                return " " + bb.getText() + " " + ((CheckBox) bb).isSelected();
            } else {
                return " " + bb.getText() + " " + true;
            }
        } else if (node instanceof TextInputControl) {
            return " " + ((TextInputControl) node).getText();
        }
        return null;
    }

    void finishTest() {
        if (primaryStage != null) {
            primaryStage.close();
        }
        myStage.close();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
