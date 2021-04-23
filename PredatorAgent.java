
import java.util.HashMap;
import java.util.Random;

public class PredatorAgent implements Agent{
    
    private int x, y, N;
    private String state;
    private HashMap<String, Integer> eDegree = new HashMap<String, Integer>();
    private String[] actions = {"UP", "DOWN", "LEFT", "RIGHT", "STAY"};
    private QTable qtable = new QTable(actions);
    private String prevAction;
    private Random rand = new Random();
    
    // learning values
    private int bAsk = 3000, bGive = 3000;
    private double eGreedy = 0.1;
    private double alpha = 0.1;
    private double gamma = 0.9;
    private double vp = 0.7;
    
    
    public String ask(String state, PredatorAgent pred){
        
        //ALGORITHM 2: GIVE
        if(bGive > 0){
            String action = qtable.getMaxAction(state);
            double ci = pred.getConfidence(state, action);
            double cj = this.getConfidence(state, action);
            if(cj > ci){ //if this agents confidence is greater than the partaker
                bGive--;
                return action;
            }
        }
        return null;
    }//end function
    
    public boolean checkAction(String action){
        // make sure the action does not remove the agent from the grid
        String impossibleActions = "";
        if(y==0)
            impossibleActions += " " + actions[0];
        if(y==N-1)
            impossibleActions += " " + actions[1];
        if(x==0)
            impossibleActions += " " + actions[2];
        if(x==N-1)
            impossibleActions += " " + actions[3];
        if(impossibleActions.indexOf(action) >= 0)
            return false;
        return true;
    }//end method
    
    public boolean equals(Agent a){
        return a.getx()==this.x && a.gety()==this.y;
    }//end method
    
    public boolean equals(int x, int y){
        return this.x==x && this.y==y;
    }//end method
    
    public String getAction(){
        String a;
        do{
            int idx = (new Random()).nextInt(actions.length);
            a = actions[idx];
        }while(!checkAction(a));
        return a;
    }//end method
    
    public String getAction(String state){
        
        this.state = state;
        if(!qtable.stateExists(state)){ //first time visiting a new state
            qtable.addState(state);
        }
        do{
            if(rand.nextDouble()<eGreedy){ //exploration
                prevAction = getAction();
            }
            else{ //exploitation
                prevAction = qtable.getMaxAction(state);
            }
        }while(!checkAction(prevAction));
        
        return prevAction;
    }//end method
    
    public String getAction(String state, PredatorAgent pred){
        this.state = state;
        
        //ALGORITHM 1: ASK
        if(bAsk > 0){
            double e = 0.0;
            if(eDegree.get(state)!=null)
                e = Math.sqrt(eDegree.get(state));
            double pAsk = Math.pow((1+vp), -e);
            if(pAsk > rand.nextDouble()){
                String action = pred.ask(state, this);
                if(action!=null){
                    bAsk -=1;
                    qtable.set(state, action, pred.getQ(state, action));
                    return qtable.getMaxAction(state);
                }
            }
        }
        
        return getAction(state);
    }//end method
    
    public int getBudget(){
        return bAsk + bGive;
    }//end method
    
    public double getConfidence(String state, String action){
        return qtable.getConfidence(state, action);
    }//end method
    
    public double getQ(String state, String action){
        return qtable.get(state, action);
    }//end method
    
    public int getx(){
        return this.x;
    }//end method
    
    public int gety(){
        return this.y;
    }//end method
    
    public boolean isPrey(){
        return false;
    }//end method
    
    public void performAction(String action){
        //update the number of times the agent has visited the state
        if(!eDegree.containsKey(state)){
            eDegree.put(state, 1);
        }
        else{
            eDegree.put(state, eDegree.get(state)+1);
        }
        
        // perform the action
        if(action.equals("UP")){
            this.y = this.y-1;
        }
        else if(action.equals("DOWN")){
            this.y = this.y+1;
        }
        else if(action.equals("LEFT")){
            this.x = this.x-1;
        }
        else if(action.equals("RIGHT")){
            this.x = this.x+1;
        }
        else if(action.equals("STAY")){
            
        }
        else{
            System.out.println("ERROR: Invalid action. ");
            System.exit(1);
        }
    }//end method
    
    public void printQTable(){
        qtable.print();
    }//end method
    
    public void setBudgets(int ba, int bg){
        this.bAsk = ba;
        this.bGive = bg;
    }//end method
    
    public void setCoordinates(int x, int y){
        this.x = x;
        this.y = y;
    }//end method
    
    public void setEnv(int N){
        this.N = N;
    }//end setEnv
    
    public void setState(String state){
        this.state = state;
    }//end method
    
    public void update(String nextState, int reward){
        double delta = reward + gamma * qtable.getMaxValue(nextState) - qtable.get(state, prevAction);
        double Q = qtable.get(state, prevAction) + alpha * delta;
        qtable.set(state, prevAction, Q);
    }//end method
    
}//end class