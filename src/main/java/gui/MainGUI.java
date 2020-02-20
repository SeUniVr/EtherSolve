package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import parseTree.Contract;

public class MainGUI extends Application {

    public static void main(String[] args) {
        launch(args);
        //MainApp.main(args);
    }

    @Override
    public void start(Stage primaryStage) {
        String input1 = "6080604052348015600f57600080fd5b5060aa60008190555060538060256000396000f3fe608060405260008054600e6014565b01905050005b6000608890509056fea2646970667358221220e73604337ca6440964bb7aaae76c90999a4ba3d1746f922f9b1534366a8051c86473";//6f6c63430006020033";
        String input2 = "6080604052348015600f57600080fd5b5060aa60008190555060538060256000396000f3fe608060405260008054600e6014565b01905050005b6000608890509056fea2646970667358221220e73604337ca6440964bb7aaae76c90999a4ba3d1746f922f9b1534366a8051c864736f6c63430006020033";
        String input3 = "6080604052348015600f57600080fd5b5060aa60008190555060538060256000396000f3fe608060405260008054600e6014565b01905050005b6000608890509056fea264697066735822122084caa862448229c72e485dc6650fe6a76f351622432eb39a9a2e2c9fb30dd5de64736f6c63430006020033";
        String input4 = "60538060256000396000f3fe608060405260008054600e6014565b01905050005b6000608890509056fe";//a2646970667358221220e73";

        Contract c2 = new Contract("c2", input2);

        Pane cfg = CFGPrinter.print(c2);
        StackPane root = new StackPane();
        root.getChildren().add(cfg);
        Scene mainScene = new Scene(root,1024, 768);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

}
