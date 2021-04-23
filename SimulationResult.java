
import java.util.ArrayList;




public class SimulationResult{
    
    private int N;
    private ArrayList<int[][]> temporalAgents = new ArrayList<int[][]>();
    private int TG = -1;
    
    public SimulationResult(int N){
        this.N = N;
    }//end constructor
    
    public void addStep(Agent[] agents){
        /*
        The current coordinates of each agent are stored in an array, as well
        as the TG variable is updated for the current time. 
        */
        int[][] coordinates = new int[agents.length][2];
        for(int i=0;i<agents.length;i++){
            coordinates[i][0] = agents[i].getx();
            coordinates[i][1] = agents[i].gety();
        }
        temporalAgents.add(coordinates);
        TG++;
    }//end addStep
    
    public int getTime(){
        return TG;
    }//end method
    
    public int size(){
        /*
        Returns the number of time steps that have passed. 
        */
        return temporalAgents.size();
    }//end method
    
    public int getGridSize(){
        return N;
    }//end method
    
    public int[][] getAgentCoordinates(int t){
        /*
        Returns a 2D array containing the position of each agent at a given
        time, t. 
        */
        return temporalAgents.get(t);
    }//end method
    
}//end class