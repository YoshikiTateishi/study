// Exercise 06: Denomination Application
package xml;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class DenominationApp extends Application {

    public static String gakuban = "16EC000"; // 学籍番号を入力すること
    public static String yourname = "千住旭"; // 氏名を入力すること

    TextField tf11;
    Button btn21, btn22;
    Label[] labelN2;
    HBox hbox1, hbox2, hbox3;

    @Override
    public void start(Stage primaryStage) {
        // 金額入力領域
        Label label11 = new Label("金額");
        Label label12 = new Label("千円");
        tf11 = new TextField("0");
        hbox1 = new HBox(label11, tf11, label12);

        // ボタン領域
        btn21 = new Button("金種計算");
        btn21.setOnAction(e -> calcClicked()); // 課題06-2でこのメソッドを実装
        btn22 = new Button("リセット");
        btn22.setOnAction(e -> resetClicked());
        hbox2 = new HBox(btn21, btn22);

        // エラー表示領域
        Label label3 = new Label("金額エラー");
        label3.setTextFill(Color.RED);
        hbox3 = new HBox(label3);

        // 金種枚数表示
        Label[] labelN1 = new Label[4];
        labelN2 = new Label[4];
        HBox[] hboxN = new HBox[4];
        String[] textN = {"10000円札", "5000円札", "2000円札", "1000円札",};
        for (int i = 0; i < 4; i++) {
            labelN1[i] = new Label(textN[i]);
            labelN1[i].setPrefWidth(80);
            labelN2[i] = new Label("0");
            labelN2[i].setPrefWidth(50);
            hboxN[i] = new HBox(labelN1[i], labelN2[i]);
            hboxN[i].setAlignment(Pos.CENTER);
        }

        // 全体レイアウト
        adjustLayout(); // 課題06-1でこのメソッドを実装
        VBox vbox = new VBox(10, hbox1, hbox2, hbox3,
                hboxN[0], hboxN[1], hboxN[2], hboxN[3]);
        vbox.setAlignment(Pos.CENTER);

        // レイアウト表示
        Scene scene = new Scene(vbox, 300, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Denomination Application");
        primaryStage.show();
    }

    // レイアウトの調整
    public void adjustLayout() {
        // 課題06-1のコードを記述
        tf11.setPrefColumnCount(5);
        tf11.setAlignment(Pos.BASELINE_RIGHT);
        hbox1.setAlignment(Pos.CENTER);
        hbox1.setSpacing(10);
        hbox2.setAlignment(Pos.CENTER);
        hbox2.setSpacing(10);
        hbox3.setAlignment(Pos.CENTER);
        hbox3.setVisible(false);
    }

    // 金種計算ボタンのアクション
    public void calcClicked() {
        // 課題06-2のコードを記述
        String input = tf11.getText();
        try {
            int money = Integer.parseInt(input);
            labelN2[0].setText(Integer.toString(money / 10));
            money %= 10;
            labelN2[1].setText(Integer.toString(money / 5));
            money %= 5;
            labelN2[2].setText(Integer.toString(money / 2));
            labelN2[3].setText(Integer.toString(money % 2));
            hbox3.setVisible(false);
        } catch (Exception ex) {
            for (Label la : labelN2) {
                la.setText("0");
            }
            hbox3.setVisible(true);
        }
    }

    // リセットボタンのアクション
    public void resetClicked() {
        tf11.setText("0");
        for (Label la : labelN2) {
            la.setText("0");
        }
        hbox3.setVisible(false);
    }

    public static void main(String[] args) {
        // アプリケーションを起動する
        Application.launch(args);
        System.out.println("完了--DenominationApp");
    }

}
