package flightroutes;

import java.util.ArrayList;

public class Nodo {
    public String code;
    public String name;
    public String city;
    public String county;
    public float lat;
    public float lon;
    public ArrayList <Nodo> flights = new ArrayList<>();
    
    public Nodo (String code, String name, String city, String country, float lat, float lon) {
        this.code = code;
        this.name = name;
        this.city = city;
        this.county = country;
        this.lat = lat;
        this.lon = lon;
    }
    
    public float distance (Nodo aeropuerto) {
        if (this.code.equals(aeropuerto.code))
            return 0;
        
        if (!this.flights.contains(aeropuerto))
            return 999999999;
        
        float R = 6371;
        float dif_lat = this.lat  - aeropuerto.lat;
        float dif_lon = this.lon  - aeropuerto.lon;
        float a = (float) (Math.sin(dif_lat/2)*Math.sin(dif_lat/2) + Math.cos(aeropuerto.lat) * Math.cos(this.lat) * Math.cos(dif_lat) * Math.cos(dif_lat));
        float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)));
        
        return R * c;
    }
}
