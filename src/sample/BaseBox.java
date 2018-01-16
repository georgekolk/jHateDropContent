package sample;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.concurrent.Executor;


public class BaseBox extends VBox {
    private String dir;
    private Label labelElement;
    private String styleCSS;

    public BaseBox(String dir, String label, Executor executor) {
        this.dir = dir;
        this.labelElement = new Label(label);
        this.labelElement.setStyle(
                "-fx-background-color: rgba(256,256,256,0.40); \n"+
                "-fx-font-size: 12pt; \n" +
                "-fx-border-color: rgb(49, 89, 23); \n" +
                "-fx-font-family: \"Impact\";"
        );
        this.getChildren().add(labelElement);
        this.styleCSS = "-fx-background-color: " + GenerateRandomColorRGB.generateColor() + ";";
        this.setStyle(styleCSS);
        this.setMinSize(150,150);

        File file = new File(dir);

        if (!file.exists()) {
            //System.out.print("No Folder");
            file.mkdir();
            //System.out.print("Folder created");
        }

        /*InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File("D:/2cs/unsorted/14033521_1373063572723279_1006711593_n.jpg"));
        }catch (Exception e){
            e.printStackTrace();
        }

        Image image3 = new Image(inputStream);
        ImageView imageView2 = new ImageView(image3);

        imageView2.setFitWidth(150);
        imageView2.setFitHeight(150);
        imageView2.setPreserveRatio(true);

        imageView2.setSmooth(true);
        imageView2.setCache(true);

        this.getChildren().add(imageView2);*/

        this.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                Dragboard db = event.getDragboard();

                if (db.hasUrl()){
                    //System.out.println(db.getHtml());
                    event.acceptTransferModes(TransferMode.ANY);
                }else if(db.hasString()){
                    //System.out.println(db.getString());
                    event.acceptTransferModes(TransferMode.ANY);
                } else {
                    event.consume();
                }
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

                    final FirstLineService service = new FirstLineService();

                    service.setUrl(db.getString());
                    service.setDir(dir);
                    service.setExecutor(executor);

                    service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent t) {
                            tar.setStyle(styleCSS);
                        }
                    });

                    service.start();

                }else if(db.hasString()){

                    try {
                        File sourceDir = new File(db.getString());
                        File destinationDir = new File(dir+ "//" + sourceDir.getName());
                        FileUtils.moveDirectory(sourceDir, destinationDir);
                        tar.setStyle(styleCSS);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //System.out.println("Drop da base" + db.getString());
                }



                event.setDropCompleted(success);
                event.consume();
            }
        });
    }
}