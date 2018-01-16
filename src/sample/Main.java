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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application {


    private ExecutorService sequentialFirstLineExecutor;
    private final static File configJson = new File("default.json");
    private static ArrayList<BaseBox> myBoxArrayList;
    private final static String tempDir = "D://2cs//testTemp";

    @Override
    public void init() throws Exception {
        sequentialFirstLineExecutor = Executors.newFixedThreadPool(
                10,
                new FirstLineThreadFactory("box processing")
        );


        LoadConf config = new LoadConf(configJson, sequentialFirstLineExecutor);

        myBoxArrayList = config.getBaseBoxList();

    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        ArrayList<VBox> boxList = new ArrayList<VBox>();
        boxList.addAll(myBoxArrayList);


        FlowPane rootPane = new FlowPane();
        rootPane.setStyle("-fx-background-color: "+ GeneracolorRGB.generateColor()+ ";");

        VBox createTempDir = new VBox();
        createTempDir.setStyle("-fx-background-color: "+ GeneracolorRGB.generateColor()+ ";");
        createTempDir.setMinSize(150,150);
        Label tempDirComment = new Label("Подготовка папки \n для загрузки");
        tempDirComment.setStyle(


//        "-fx-background-color: #FFFFFF; \n"+
                "-fx-background-color: rgba(256,256,256,0.40); \n"+
                //"-fx-effect: dropshadow(gaussian, red, 50, 0, 0, 0); \n" +
                "-fx-font-size: 12pt; \n" +
                "-fx-border-color: rgb(49, 89, 23); \n" +
                "-fx-font-family: \"Impact\";"


        //this.setStyle("-fx-background-color: "+ GeneracolorRGB.generateColor()+ "; \n -fx-border-color: gray; \n -fx-border-insets: 5; \n -fx-border-width: 3;\n -fx-border-style: dashed;");

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

    static class FirstLineThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final String type;

        public FirstLineThreadFactory(String type) {
            this.type = type;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "LineService-" + poolNumber.getAndIncrement() + "-thread-" + type);
            thread.setDaemon(true);

            return thread;
        }
    }

    public static class FirstLineService extends Service<String> {

        private String url;
        private String dir;

        public final void setUrl(String value){
            this.url = value;
        }
        public final void setDir(String value){
            this.dir = value;
        }

        protected Task createTask() {

            return new Task<String>() {
                protected String call() throws Exception {
                    HttpDownloadUtility.downloadFile(url, dir);

                    System.out.println(url);

                    return null;
                }
            };
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
