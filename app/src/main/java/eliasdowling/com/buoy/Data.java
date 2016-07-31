package eliasdowling.com.buoy;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

/**
 * Created by elias on 6/4/2016.
 */
public class Data {
    private Date dateMain;
    private String fileName;
    private String date;
    private String windSpeed;
    private String windDir;
    private String waveHgt;
    private String domPeriod;
    private String avgPeriod;
    private String waveDir;
    private String airTemp;
    private String waterTemp;
    private String pressure;
    private String tide;

    public String getAirTemp() {
        return airTemp;
    }

    public void setAirTemp(String airTemp) {
        this.airTemp = airTemp;
    }

    public String getWaterTemp() {
        return waterTemp;
    }

    public void setWaterTemp(String waterTemp) {
        this.waterTemp = waterTemp;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getTide() {
        return tide;
    }

    public void setTide(String tide) {
        this.tide = tide;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDir() {
        return windDir;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    public String getWaveHgt() {
        return waveHgt;
    }

    public void setWaveHgt(String waveHgt) {
        this.waveHgt = waveHgt;
    }

    public String getDomPeriod() {
        return domPeriod;
    }

    public void setDomPeriod(String domPeriod) {
        this.domPeriod = domPeriod;
    }

    public String getAvgPeriod() {
        return avgPeriod;
    }

    public void setAvgPeriod(String avgPeriod) {
        this.avgPeriod = avgPeriod;
    }

    public String getWaveDir() {
        return waveDir;
    }

    public void setWaveDir(String waveDir) {
        this.waveDir = waveDir;
    }

    public Data() {

    }

    public Data(String file) {
        this.fileName = file.toUpperCase();
    }


    public String printDate(String[] data){
        String dateStr = data[0]+"-"+data[1]+"-"+data[2]+" "+data[3]+":"+data[4];
        //System.out.println(dateStr);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("y-M-d h:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            dateMain = sdf.parse(dateStr);
        }catch(ParseException p){
            p.printStackTrace();
        }
        //System.out.println(dateMain.toString());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        // Printing the date
        //System.out.println(dateFormatter.format(dateMain));
        return dateFormatter.format(dateMain);
    }

    public String[] retrieveCurrent() {
        //can be refactored....
        Scanner s = null;
        try {
            URL url = new URL("http://www.ndbc.noaa.gov/data/realtime2/" + this.fileName + ".txt");
            s = new Scanner(url.openStream());
        }catch(IOException e){
            e.printStackTrace();
        }

        String line;
        int currentLineNumber = 0;

        do{
            currentLineNumber += 1;
            line = s.nextLine();
        } while (line != null && currentLineNumber < 3);

        return dataParse(line);
    }

    public Data(Data d){
        this.dateMain = d.dateMain;
        this.fileName = d.fileName;
        this.date = d.date;
        this.windSpeed = d.windSpeed;
        this.windDir = d.windDir;
        this.waveHgt = d.waveHgt;
        this.domPeriod = d.domPeriod;
        this.avgPeriod = d.avgPeriod;
        this.waveDir = d.waveDir;
        this.airTemp = d.airTemp;
        this.waterTemp = d.waterTemp;
        this.pressure = d.pressure;
        this.tide = d.tide;
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

    public Data setAll(String[] data,boolean past){
        if(past) this.date = printDate(data);
        else this.date = printDate(data);
        NumberFormat formatter = new DecimalFormat("#0.00");
        //pres = 12 air = 13 water = 14 tide = 18
        if(!data[5].equals("MM")) {
            this.windDir = degreeToDir(data[5]);
        }if(!data[6].equals("MM")) {
            this.windSpeed = String.valueOf(formatter.format(Double.parseDouble(data[6])*2.23));
        }if(!data[8].equals("MM")) {
            this.waveHgt = String.valueOf(formatter.format(Double.parseDouble(data[8])*3.28));
        }if(!data[9].equals("MM")) {
            this.domPeriod = data[9];
        }if(!data[10].equals("MM")) {
            this.avgPeriod = data[10];
        }if(!data[11].equals("MM")) {
            this.waveDir = degreeToDir(data[11]);
        }if(!data[12].equals("MM")) {
            this.pressure = data[12];
        }
        //Multiply by 9, then divide by 5, then add 32
        if(!data[13].equals("MM")) {
            this.airTemp = String.valueOf(formatter.format((Double.parseDouble(data[13])*9)/5+32));
        }if(!data[14].equals("MM")) {
            this.waterTemp = String.valueOf(formatter.format((Double.parseDouble(data[14])*9)/5+32));
        }if(!data[18].equals("MM")) {
            this.tide = degreeToDir(data[11]);
        }
        return this;
    }
    @Override
    public String toString(){
        String out = "";
        if(this.waveHgt!=null){
            out += "Wave height: "+this.waveHgt+" feet\n";
        }if(this.waveDir!=null){
            out += "Wave direction: "+ this.waveDir+"\n";
        }if(this.domPeriod!=null){
            out +="Dominant period: "+ this.domPeriod+" sec.\n";
        }if(this.avgPeriod!=null){
            out += "Average period: "+ this.avgPeriod+" sec.\n";
        }if(this.windSpeed!=null){
            out += "Wind speed: "+this.windSpeed+" MPH\n";
        }if(this.windDir!=null){
            out += "Wind direction: "+this.windDir+"\n";
        }if(this.pressure!=null){
            out += "Pressure: "+this.pressure+" hPa\n";
        }if(this.airTemp!=null){
            out += "Air temperature: "+this.airTemp+" \u2109\n";
        }if(this.waterTemp!=null){
            out += "Water temperature: "+this.waterTemp+" \u2109\n";
        }if(this.tide!=null){
            out += "Tide: "+this.tide+" ft";
        }
        return "Date: " + this.date +"\n"+ out;
    }

    public String degreeToDir(String degree){
        double deg = Double.parseDouble(degree);
        String dir = "";
        if(deg>=348.75 || deg<=11.25){
            dir = "N";
        }else if(deg>11.25 && deg<=33.75){
            dir = "NNE";
        }else if(deg>33.75 && deg<=56.25){
            dir = "NE";
        }else if(deg>56.25 && deg<=78.75){
            dir = "ENE";
        }else if(deg>78.75 && deg<=101.25){
            dir = "E";
        }else if(deg>101.25 && deg<=123.75){
            dir = "ESE";
        }else if(deg>123.75 && deg<=146.25){
            dir = "SE";
        }else if(deg>146.25 && deg<=168.75){
            dir = "SSE";
        }else if(deg>168.75 && deg<=191.25){
            dir = "S";
        }else if(deg>191.25 && deg<=213.75){
            dir = "SSW";
        }else if(deg>213.75 && deg<=236.25){
            dir = "SW";
        }else if(deg>236.25 && deg<=258.75){
            dir = "WSW";
        }else if(deg>258.75 && deg<=281.25){
            dir = "W";
        }else if(deg>281.25 && deg<=303.75){
            dir = "WNW";
        }else if(deg>303.75 && deg<=326.25){
            dir = "NW";
        }else{
            dir = "NNW";
        }
        return dir+"("+degree+"\u00B0)";
    }


}
