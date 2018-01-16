package sample;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class FirstLineService extends Service<String> {

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
