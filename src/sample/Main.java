package sample;

import javafx.application.Application;
import javafx.concurrent.*;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    private ExecutorService sequentialFirstLineExecutor;
    private final static File configJson = new File("default.json");
    private static ArrayList<BaseBox> myBoxArrayList;
    private String tempDir;

    @Override
    public void init() throws Exception {
        sequentialFirstLineExecutor = Executors.newFixedThreadPool(
                10,
                new FirstLineThreadFactory("box processing")
        );

        LoadConf config = new LoadConf(configJson, sequentialFirstLineExecutor);

        myBoxArrayList = config.getBaseBoxList();
        tempDir = config.getTempDir();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        ArrayList<VBox> boxList = new ArrayList<VBox>();
        boxList.addAll(myBoxArrayList);

        FlowPane rootPane = new FlowPane();
        //rootPane.setStyle("-fx-background-color: "+ GenerateRandomColorRGB.generateColor()+ ";");
        rootPane.setStyle("-fx-background-color: gray;");

        VBox createTempDir = new VBox();
        createTempDir.setStyle("-fx-background-color: "+ GenerateRandomColorRGB.generateColor()+ ";");
        createTempDir.setMinSize(150,150);
        Label tempDirComment = new Label("Prepare directory \n for upload");
        tempDirComment.setStyle(
                "-fx-background-color: rgba(256,256,256,0.40); \n"+
                "-fx-font-size: 12pt; \n" +
                "-fx-border-color: rgb(49, 89, 23); \n" +
                "-fx-font-family: \"Impact\";"
        );

        createTempDir.getChildren().addAll(tempDirComment);

        createTempDir.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                Dragboard db = event.getDragboard();

                if (db.hasUrl()){
                    //System.out.println(db.getHtml());
                    event.acceptTransferModes(TransferMode.COPY);
                }  else {
                    event.consume();
                }
            }
        });

        createTempDir.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;

                if (db.hasUrl()) {
                    success = true;
                    System.out.println(db.getHtml());
                    rootPane.getChildren().add(new TempBox(tempDir, db.getString(), rootPane, sequentialFirstLineExecutor));
                }

                event.setDropCompleted(success);
                event.consume();
            }
        });

        rootPane.getChildren().addAll(createTempDir);
        rootPane.getChildren().addAll(boxList);

        Scene scene = new Scene(rootPane, 1500, 500);
        primaryStage.setTitle("jHate SMM Drag And Drop Poster");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        sequentialFirstLineExecutor.shutdown();
        sequentialFirstLineExecutor.awaitTermination(3, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
