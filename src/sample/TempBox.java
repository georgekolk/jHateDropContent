package sample;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.time.Instant;
import java.util.concurrent.Executor;

public class TempBox extends VBox {

    private String styleCSS;


    public TempBox(String tempDir, String url, Pane pane, Executor executor) {
        this.styleCSS = "-fx-background-color: " + GeneracolorRGB.generateColor() + ";";


        long unixTimestamp = Instant.now().getEpochSecond();
        File f = new File(tempDir + "//jHateDropper-" + unixTimestamp);
        //System.out.println(f.getName());
        //System.out.println(f.getPath());
        f.mkdir();
        try {
            HttpDownloadUtility.downloadFile(url, f.getPath());
        }catch (Exception e){
            e.printStackTrace();
        }

        //Image image3 = new Image(url, 100, 0, false, false);
        Image image3 = new Image(url);
        ImageView imageView2 = new ImageView(image3);

        imageView2.setFitWidth(150);
        imageView2.setFitHeight(150);
        imageView2.setPreserveRatio(true);

        imageView2.setSmooth(true);
        imageView2.setCache(true);

        this.getChildren().add(imageView2);
        /*HBox testHBox = new HBox();
        testHBox.setMinSize(50,50);
        testHBox.setStyle("-fx-background-color: "+ GeneracolorRGB.generateColor()+ ";");
        testHBox.getChildren().add(new Label("dir"));

        HBox testHBox2 = new HBox();
        testHBox2.setMinSize(50,50);
        testHBox2.setStyle("-fx-background-color: "+ GeneracolorRGB.generateColor()+ ";");
        testHBox2.getChildren().add(new Label("post now"));

        this.getChildren().addAll(testHBox,testHBox2);*/

        //this.setStyle("-fx-background-color: "+ GeneracolorRGB.generateColor()+ "; \n -fx-border-color: gray; \n -fx-border-insets: 5; \n -fx-border-width: 3;\n -fx-border-style: dashed;");
        this.setStyle(styleCSS);
        this.setMinSize(150,150);

        this.setOnDragOver(new EventHandler<DragEvent>() {
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


        this.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard db = startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(f.getPath());

                db.setContent(content);

                VBox tar = (VBox)event.getSource();
                pane.getChildren().remove(tar);

                event.consume();

            }
        });


        this.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;


                VBox tar = (VBox)event.getGestureTarget();
                tar.setStyle("-fx-background-color: #555555;");

                if (db.hasUrl()) {
                    success = true;
                    System.out.println(db.getHtml());

                    final Main.FirstLineService service = new Main.FirstLineService();

                    service.setUrl(db.getString());
                    //service.setDir(f.getPath().replaceAll("\\\\","//"));
                    service.setDir(f.getPath());
                    service.setExecutor(executor);

                    service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent t) {
                            tar.setStyle(styleCSS);
                        }
                    });

                    service.start();

                }

                System.out.println("Dropped: " + db.getString());

                event.setDropCompleted(success);
                event.consume();
            }
        });
    }

}
