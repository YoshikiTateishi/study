// Exercise 08: Ticket Calculator
package xml;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.geometry.*;

public class TicketCalculator extends Application {

    public static String gakuban = "16EC000"; // 学籍番号を入力すること
    public static String yourname = "千住旭"; // 氏名を入力すること

    ComboBox<Integer> cbTicket, cbCount;
    TextField tfBottom;
    Button btnReset;

    @Override
    public void start(Stage primaryStage) {
        // Create the Top
        Label lblTop = new Label("チケット計算機");
        lblTop.setFont(new Font(18));
        lblTop.setUnderline(true);
        HBox hbTop = new HBox(lblTop);
        hbTop.setAlignment(Pos.CENTER);

        // Create the Center
        Label lblTicket = new Label("料金");
        cbTicket = new ComboBox<>();
        cbTicket.getItems().addAll(170, 200, 240, 280, 310);
        cbTicket.setOnAction(e -> ticketSelected());
        Label lblCount = new Label("枚数");
        cbCount = new ComboBox<>();
        cbCount.getItems().addAll(1, 2, 3, 4, 5);
        cbCount.setOnAction(e -> ticketSelected());
        HBox hbCenter = new HBox(20, lblTicket, cbTicket, lblCount, cbCount);
        hbCenter.setAlignment(Pos.CENTER);

        // Create the Bottom
        Label lblBottom = new Label("金額");
        tfBottom = new TextField();
        tfBottom.setEditable(false);
        tfBottom.setPrefColumnCount(6);
        tfBottom.setAlignment(Pos.BASELINE_RIGHT);
        btnReset = new Button("リセット");
        btnReset.setOnAction(e -> reset());
        HBox hbBottom = new HBox(20, lblBottom, tfBottom, btnReset);
        hbBottom.setAlignment(Pos.CENTER);

        // Create the Layout
        BorderPane pane = new BorderPane(hbCenter);
        pane.setPadding(new Insets(20));
        pane.setTop(hbTop);
        pane.setBottom(hbBottom);

        // Create and show the Scene
        Scene scene = new Scene(pane, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ticket Calculator");
        primaryStage.show();
    }

    public void ticketSelected() {
        if (cbTicket.getValue() != null && cbCount.getValue() != null) {
            tfBottom.setText(Integer.toString(
                    cbTicket.getValue() * cbCount.getValue()));
        }
    }

    public void reset() {
        tfBottom.setText(null);
        cbTicket.setValue(null);
        cbCount.setValue(null);
    }

    public static void main(String[] args) {
        Application.launch(args);
        System.out.println("完了--TicketCalculator");
    }

}
