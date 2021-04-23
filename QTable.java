
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Random;



public class QTable{
    
    private HashMap<String, HashMap<String,Double>> table = new HashMap<String, HashMap<String,Double>>();
    private HashMap<String, HashMap<String,Integer>> timesUpdated = new HashMap<String, HashMap<String,Integer>>();
    private ArrayList<String> states = new ArrayList<String>();
    private String[] actions;
    
    
    public QTable(String[] actions){
        this.actions = actions;
    }//end constructor
    
    public void addState(String s){
        HashMap<String, Double> temp = new HashMap<String, Double>();
        HashMap<String, Integer> temp2 = new HashMap<String, Integer>();
        for(String action:actions){
            temp.put(action, 0.0);
            temp2.put(action, 0);
        }
        table.put(s, temp);
        timesUpdated.put(s, temp2);
        states.add(s);
    }//end method
    
    public HashMap<String, Double> get(String state){
        if(!stateExists(state))
            return null;
        return table.get(state);
    }//end method
    
    public double get(String state, String action){
        if(!this.stateExists(state))
            return 0.0;
        return table.get(state).get(action);
    }//end method
    
    public double getConfidence(String state, String action){
        
        double discrimination = getMaxValue(state) - getMinValue(state) +1;
        discrimination = 1 - 1/discrimination;
        int m = getCount(state, action);
        return discrimination * m;
    }//end method
    
    public int getCount(String state, String action){
        if(timesUpdated.get(state)==null)
            return 0;
        return timesUpdated.get(state).get(action);
    }//end method
    
    public String getMaxAction(String state){
        Random rand =new Random();
        
        ArrayList<String> maxActions = new ArrayList<String>();
        maxActions.add(actions[0]);
        double maxQ = this.get(state, actions[0]);
        for(String action:actions){
            if(this.get(state, action) > maxQ){
                maxActions.clear();
                maxActions.add(action);
                maxQ = this.get(state, action);
            }
            else if(this.get(state, action) == maxQ)
                maxActions.add(action);
        }
        //if multiple actions are tied for greatest Q-value, return random one
        return maxActions.get(rand.nextInt(maxActions.size()));
    }//end method
    
    public double getMaxValue(String state){
        String action = getMaxAction(state);
        return this.get(state, action);
    }//end method
    
    public double getMinValue(String state){
        double min = get(state, actions[0]);
        for(String action:actions){
            if(get(state, action)<min)
                min = get(state,action);
        }
        return min;
    }//end method
    
    public void print(){
        Collections.sort(states);
        System.out.print("\t\t\t");
        for(String action:actions)
            System.out.print(action + "\t\t");
        System.out.print("\n");
        for(String state:states){
            System.out.print(state+"\t\t");
            for(String action:actions){
                System.out.print(this.get(state, action) + "\t\t");
            }
            System.out.print("\n");
        }
    }//end method
    
    public void set(String state, String action, double val){
        if(!stateExists(state))
            this.addState(state);
        table.get(state).put(action, val);
        int c = getCount(state, action);
        timesUpdated.get(state).put(action, c+1);
    }//end method
    
    public boolean stateExists(String s){
        return table.containsKey(s);
    }//end method

}//end class