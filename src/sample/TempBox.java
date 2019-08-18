package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executor;

public class TempBox extends VBox {

    private String styleCSS;

    private int counter = 0;
    private SimpleIntegerProperty counterProperty;



    public TempBox(String tempDir, String url, Pane pane, Executor executor) {
        this.styleCSS = "-fx-background-color: " + GenerateRandomColorRGB.generateColor() + ";";
        this.counterProperty = new SimpleIntegerProperty(counter);
        Label counterLabel = new Label();
        counterProperty.set(++counter);
        counterLabel.textProperty().bind(this.counterProperty.asString());
        counterLabel.setStyle(
                "-fx-background-color: rgba(256,256,256,0.60); \n"+
                        "-fx-font-size: 12pt; \n" +
                        "-fx-border-color: rgb(49, 89, 23); \n" +
                        "-fx-font-family: \"Impact\";"
        );

        long unixTimestamp = Instant.now().getEpochSecond();
        File f = new File(tempDir + "//jHateDropper-" + unixTimestamp);
        f.mkdir();

        final FirstLineService service = new FirstLineService();

        service.setUrl(url);
        service.setDir(f.getPath());
        service.setExecutor(executor);
        service.start();

        Image image3 = new Image(url);
        ImageView imageView2 = new ImageView(image3);

        imageView2.setFitWidth(150);
        imageView2.setFitHeight(150);
        imageView2.setPreserveRatio(true);

        imageView2.setSmooth(true);
        imageView2.setCache(true);

        StackPane layersPane = new StackPane();
        layersPane.getChildren().add(imageView2);
        layersPane.getChildren().add(counterLabel);

        this.getChildren().add(layersPane);

        this.setStyle(styleCSS);
        this.setMinSize(150,150);
        this.setMaxSize(150,150);

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
                counterProperty.set(++counter);

                VBox tar = (VBox)event.getGestureTarget();
                tar.setStyle("-fx-background-color: #555555;");

                if (db.hasUrl()) {
                    success = true;
                    System.out.println(db.getHtml());

                    final FirstLineService service = new FirstLineService();

                    service.setUrl(db.getString());
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

    public TempBox(String tempDir, List<File> fileList, Pane pane){
        this.styleCSS = "-fx-background-color: " + GenerateRandomColorRGB.generateColor() + ";";
        this.counterProperty = new SimpleIntegerProperty(counter);
        Label counterLabel = new Label();

        counterLabel.textProperty().bind(this.counterProperty.asString());
        counterLabel.setStyle(
                "-fx-background-color: rgba(256,256,256,0.60); \n"+
                        "-fx-font-size: 12pt; \n" +
                        "-fx-border-color: rgb(49, 89, 23); \n" +
                        "-fx-font-family: \"Impact\";"
        );

        long unixTimestamp = Instant.now().getEpochSecond();
        File f = new File(tempDir + "//jHateDropper-" + unixTimestamp);
        f.mkdir();

        Image image3 = new Image(fileList.get(0).toURI().toString());

        for (File file:fileList) {
            try {
                FileUtils.moveFileToDirectory(file, f, false);
                counterProperty.set(++counter);
            }catch (IOException e){
                e.printStackTrace();
            }
        }


        ImageView imageView2 = new ImageView(image3);

        imageView2.setFitWidth(150);
        imageView2.setFitHeight(150);
        imageView2.setPreserveRatio(true);

        imageView2.setSmooth(true);
        imageView2.setCache(true);

        StackPane layersPane = new StackPane();
        layersPane.getChildren().add(imageView2);
        layersPane.getChildren().add(counterLabel);

        this.getChildren().add(layersPane);

        this.setStyle(styleCSS);
        this.setMinSize(150,150);
        this.setMaxSize(150,150);

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
                //counterProperty.set(++counter);

                VBox tar = (VBox)event.getGestureTarget();
                tar.setStyle("-fx-background-color: #555555;");

                if (db.hasHtml()) {
                    success = true;
                    System.out.println(db.getHtml());

                }else if (db.hasFiles()){
                    for (File file:db.getFiles()) {
                        try {
                            FileUtils.moveFileToDirectory(file, f, false);
                            counterProperty.set(++counter);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println("Dropped: " + db.getString());

                event.setDropCompleted(success);
                event.consume();
            }
        });


    }



}
