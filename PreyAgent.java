
import java.util.Random;



public class PreyAgent implements Agent{
    
    private String[] actions = {"UP", "DOWN", "LEFT", "RIGHT", "STAY"};
    private int x, y, N;
    String state;
    Random rand = new Random();
    
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
    
    public double distance(int x1, int y1, int x2, int y2){
        //compute euclidean distance
        //return Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2);
        //return Math.abs(x1-x2) + Math.abs(y1-y2);
        int x = x1>x2 ? x1-x2 : x2-x1;
        int y = y1>y2 ? y1-y2 : y2-y1;
        return x+y;
        //return 0.0;
    }//end method
    
    public boolean equals(Agent a){
        return a.getx()==this.x && a.gety()==this.y;
    }//end method
    
    public boolean equals(int x, int y){
        return this.x==x && this.y==y;
    }//end method
    
    public String getAction(){
        String[] actions = {"UP", "DOWN", "LEFT", "RIGHT", "STAY"};
        String impossibleActions = "";
        if(y==0)
            impossibleActions += " " + actions[0];
        if(y==N-1)
            impossibleActions += " " + actions[1];
        if(x==0)
            impossibleActions += " " + actions[2];
        if(x==N-1)
            impossibleActions += " " + actions[3];
        String a;
        do{
            int idx = rand.nextInt(actions.length);
            a = actions[idx];
        }while(impossibleActions.indexOf(a) >=0);
        return a;
    }//end method
    
    public String getAction(Agent[] agents){
        
        //random choice 20% of the time
        if(rand.nextDouble() < 0.2)
            return getAction();
        //80% of the time, move away from the predators
        int x=this.x, y=this.y;
        String bestAction = "STAY";
        double bestDistance = 0.0;
        
        for(String action:actions){
            if(checkAction(action)){
                if(action.equals("UP")){
                    y = this.y-1;
                }
                else if(action.equals("DOWN")){
                    y = this.y+1;
                }
                else if(action.equals("LEFT")){
                    x = this.x-1;
                }
                else if(action.equals("RIGHT")){
                    x = this.x+1;
                }

                double d = distance(x, y, agents[0].getx(), agents[0].gety());
                d += distance(x, y, agents[1].getx(), agents[1].gety());
                if(d>bestDistance){
                    bestDistance = d;
                    bestAction = action;
                }
            }
        }
        return bestAction;
    }//end method
    
    public int getx(){
        return this.x;
    }//end method
    
    public int gety(){
        return this.y;
    }//end method
    
    public boolean isPrey(){
        return true;
    }//end method
    
    public boolean isAdjacent(Agent a){
        int x = a.getx();
        int y = a.gety();
        
        if(this.x-1==x && this.y==y)
            return true;
        else if(this.x+1==x && this.y==y)
            return true;
        else if(this.x==x && this.y-1==y)
            return true;
        else if(this.x==x && this.y+1==y)
            return true;
        return false;
    }//end method
    
    public void performAction(String action){
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
    
}//end class