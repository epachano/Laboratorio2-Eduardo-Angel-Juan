package flightroutes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class Grafo {
    
    public ArrayList<Nodo> airports = new ArrayList<>();
    
    final int MAXN = 9999;
    int INF = 999999999;
    float [][]dis = new float[MAXN][MAXN]; 
    int [][]Next = new int[MAXN][MAXN]; 
    
    public Grafo (String data_dir) {
        
        ArrayList<String> SourceAirportCod = new ArrayList <>();
        ArrayList<String> SourceAirportName = new ArrayList <>();
        ArrayList<String> SourceAirportCity = new ArrayList <>();
        ArrayList<String> SourceAirportCountry = new ArrayList <>();
        ArrayList<Float> SourceAirportLatitude = new ArrayList <>();
        ArrayList<Float> SourceAirportLongitude = new ArrayList <>();
        ArrayList<String> DestinationAirportCode = new ArrayList <>();
        ArrayList<String> DestinationAirportName = new ArrayList <>();
        ArrayList<String> DestinationAirportCity = new ArrayList <>();
        ArrayList<String> DestinationAirportCountry = new ArrayList <>();
        ArrayList<Float> DestinationAirportLatitude = new ArrayList <>();
        ArrayList<Float> DestinationAirportLongitude = new ArrayList <>();
        
        try {
            File file = new File(data_dir);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            
            String line = br.readLine();
            
            String[] data = {""};
            while ((line = br.readLine()) != null) {
                try {
                    data = line.split(",");
                    
                    float lat_source = Float.valueOf(data[4]);
                    float lon_source = Float.valueOf(data[5]);

                    float lat_dest = Float.valueOf(data[10]);
                    float lon_dest = Float.valueOf(data[11]);
                    
                    
                    
                    SourceAirportCod.add(data[0]);
                    SourceAirportName.add(data[1]);
                    SourceAirportCity.add(data[2]);
                    SourceAirportCountry.add(data[3]);
                    SourceAirportLatitude.add(lat_source);
                    SourceAirportLongitude.add(lon_source);
                    DestinationAirportCode.add(data[6]);
                    DestinationAirportName.add(data[7]);
                    DestinationAirportCity.add(data[8]);
                    DestinationAirportCountry.add(data[9]);
                    DestinationAirportLatitude.add(lat_dest);
                    DestinationAirportLongitude.add(lon_dest);
                } catch (Exception e) {
                }
              
            }
            br.close();
          }
          catch(IOException ioe) {
            ioe.printStackTrace();
          }
        
        for (int i = 0; i < SourceAirportCod.size(); i++) {
            String code = SourceAirportCod.get(i);
            String name = SourceAirportName.get(i); 
            String city = SourceAirportCity.get(i); 
            String country = SourceAirportCountry.get(i); 
            float lat = SourceAirportLatitude.get(i);
            float lon = SourceAirportLongitude.get(i);
            
            Nodo airport1 = this.addAirport(code, name, city, country, lat, lon);
            
            code = DestinationAirportCode.get(i);
            name = DestinationAirportName.get(i); 
            city = DestinationAirportCity.get(i); 
            country = DestinationAirportCountry.get(i); 
            lat = DestinationAirportLatitude.get(i);
            lon = DestinationAirportLongitude.get(i);
            
            Nodo airport2 = this.addAirport(code, name, city, country, lat, lon);
            
            this.addFlight(airport1, airport2);

        }
        
        this.initialise(this.airports.size(), this.adjMatrix());
        this.floydWarshall(this.airports.size());
    }
    
    public Nodo addAirport (String code, String name, String city, String country, float lat, float lon) {
        Nodo airport = searchAirport(code);
        
        if (airport != null)
            return airport;
        
        airport = new Nodo (code, name, city, country, lat, lon);
        airports.add(airport);
        
        return airport;
    }
    
    public void addFlight (Nodo airport1, Nodo airport2) {
        airport1.flights.add(airport2);
    }
    
    public Nodo searchAirport (String code) {
        for (Nodo airport : this.airports) {
            if (airport.code.equals(code))
                return airport;
        }
        return null;
    }
    
    public ArrayList<Nodo> furthestAirports (String code) {
        Nodo airport = this.searchAirport(code);
        ArrayList<Nodo> airport_list = new ArrayList<>();
        
        if (airport == null)
            return airport_list;
        
        int source_ind = this.airports.indexOf(airport);
        
        Vector<Integer> list = new Vector<>();
        float max = Integer.MAX_VALUE, min = -1;
        int ind = 0;
        
        for (int cont = 0; cont < 10; cont ++) {
            for (int j = 0; j < this.dis[source_ind].length; j++) {
                if (this.dis[source_ind][j] > min & this.dis[source_ind][j] < max & this.dis[source_ind][j] < 999999999) {
                    min = this.dis[source_ind][j];
                    ind = j;
                }
            }
            
            if (min > 0) {
                list.add(ind);
                max = min;
                min = -1;
            }
            
            System.out.println(max);
        }
        
        for (int i: list)
            System.out.println(i);

        for (int i: list) {
            airport_list.add(this.airports.get(i));
        }

        return airport_list;
    }
    
    public ArrayList<Nodo> travel (String code_source, String code_dest) {
        Nodo source = this.searchAirport(code_source);
        Nodo destination = this.searchAirport(code_dest);
        ArrayList<Nodo> airport_list = new ArrayList<>();
        
        if (destination == null | source == null)
            return airport_list;
        
        Vector<Integer> path; 
 
        int source_ind = this.airports.indexOf(source);
        int destination_ind = this.airports.indexOf(destination);

        path = this.constructPath(source_ind, destination_ind); 
        
        for (int i: path) {
            airport_list.add(this.airports.get(i));
        }
        
        return airport_list;
    }
    
    public float[][] adjMatrix () {
        float[][] adjMat = new float [this.airports.size()][this.airports.size()];
        
        for (int i = 0; i < this.airports.size(); i++) {
            for (int j = 0; j < this.airports.size(); j++) {
                adjMat[i][j] = this.airports.get(i).distance(this.airports.get(j));
            }
        }
        
        return adjMat;
    }
    
    public float airportDistance (Nodo source, Nodo destination) {
        int ind1 = this.airports.indexOf(source);
        int ind2 = this.airports.indexOf(destination);
        return this.dis[ind1][ind2];
    }
    

    public void initialise(int V, 
                        float [][] graph) 
    { 

        dis = new float[V][V];
        Next = new int[V][V];

        for(int i = 0; i < V; i++) 
        { 
        for(int j = 0; j < V; j++) 
        { 
            dis[i][j] = graph[i][j]; 

            // No edge between node 
            // i and j 
            if (graph[i][j] >= INF) 
                Next[i][j] = -1; 
            else
                Next[i][j] = j; 
        } 
        }
    } 
    
    // Function construct the shortest 
    // path between u and v 
    Vector<Integer> constructPath(int u, 
                                        int v) 
    { 
        
        System.out.println(Next[u][v]);
        System.out.println(dis[u][v]);
        System.out.println(Next[v][u]);
        System.out.println(dis[v][u]);
        
        // If there's no path between 
        // node u and v, simply return 
        // an empty array 
        if (Next[u][v] == -1) 
            return new Vector<Integer>(); 

        // Storing the path in a vector 
        Vector<Integer> path = new Vector<Integer>(); 
        path.add(u); 

        while (u != v) 
        { 
            u = Next[u][v]; 
            path.add(u); 
        } 
        return path; 
    } 
    
    // Standard Floyd Warshall Algorithm 
    // with little modification Now if we find 
    // that dis[i][j] > dis[i][k] + dis[k][j] 
    // then we modify next[i][j] = next[i][k] 
    void floydWarshall(int V) 
    { 
        for(int k = 0; k < V; k++) 
        { 
        for(int i = 0; i < V; i++) 
        { 
            for(int j = 0; j < V; j++) 
            { 

                // We cannot travel through 
                // edge that doesn't exist 
                if (dis[i][k] == INF || 
                    dis[k][j] == INF) 
                    continue; 

                if (dis[i][j] > dis[i][k] + 
                                dis[k][j]) 
                { 
                    dis[i][j] = dis[i][k] + 
                                dis[k][j]; 
                    Next[i][j] = Next[i][k]; 
                } 
            } 
        } 
        }
    } 
}
