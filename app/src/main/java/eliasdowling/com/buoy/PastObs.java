package eliasdowling.com.buoy;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by elias on 7/28/2016.
 */
class PastObs {
    private final String buoy;
    public PastObs(String b){
        this.buoy = b.substring(0,5);
    }

    public ArrayList<Data> pastObs(){
        Scanner s = null;
        try {
            URL url = new URL("http://www.ndbc.noaa.gov/data/realtime2/" + this.buoy + ".txt");
            s = new Scanner(url.openStream());
        }catch(IOException e){
            e.printStackTrace();
        }

        ArrayList<Data> datArr = new ArrayList<>();
        Data d = new Data(this.buoy);

        for(int i=0;i<15;i++){
            if(i<=1) s.nextLine();
            else{
                String[] x = d.dataParse(s.nextLine());
                datArr.add(new Data(d.setAll(x,true)));
                //System.out.println(datArr.get(i+3).getWindSpeed());
            }
        }

        //now we have arrayList of String arrays of past data
        //method to take string array and return data object
        return datArr;
    }

    /**
     * Parses out data from long string
     * @param str string parsed from NDBC file
     * @return data array of data
     */
    public String[] dataParse(String str){
        //removes spaces
        String[] data = str.split(" ");
        //array -> arraylist
        List<String> list = new ArrayList<>(Arrays.asList(data));
        //removes all spaces
        list.removeAll(Arrays.asList(""," "));
        //back to array....
        data = new String[list.size()];
        data = list.toArray(data);
        return data;
    }


}
