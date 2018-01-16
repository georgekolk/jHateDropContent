package sample;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executor;


public class LoadConf {

    private ArrayList<BaseBox> destinationBoxes = new ArrayList<>();
    private String tempDir = "C://Temp";

    public LoadConf(File configFile, Executor executor){

        try {
            Scanner s = new Scanner(configFile);
            StringBuilder builder = new StringBuilder();

            while (s.hasNextLine()) builder.append(s.nextLine());

            JSONParser pars = new JSONParser();

            try {

                Object obj = pars.parse(builder.toString());
                JSONObject overallConfig = (JSONObject) obj;


                if(overallConfig.containsKey("destination")){
                    JSONArray destination = (JSONArray)overallConfig.get("destination");

                    for (int i = 0; i < destination.size(); i++) {
                        JSONObject destinationPoint = (JSONObject) destination.get(i);
                        destinationBoxes.add(new BaseBox((String)destinationPoint.get("dir"), (String)destinationPoint.get("label"), executor));
                    }
                }

                if(overallConfig.containsKey("tempDir")){
                    this.tempDir = (String)overallConfig.get("tempDir");
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //return destinationBoxes;
    }


    public ArrayList<BaseBox> getBaseBoxList(){
        return this.destinationBoxes;
    }


    public String getTempDir(){
        return this.tempDir;
    }


}
