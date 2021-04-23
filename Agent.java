

public interface Agent{
    
    public boolean equals(Agent a);
    
    public boolean equals(int x, int y);
    
    public String getAction();
    
    public int getx();
    
    public int gety();
    
    public boolean isPrey();
    
    public void performAction(String action);
    
    public void setCoordinates(int x, int y);
    
    public void setEnv(int N);
            
    public void setState(String state);
    
    
}//end interface