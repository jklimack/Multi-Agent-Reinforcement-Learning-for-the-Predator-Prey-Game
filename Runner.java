

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/*
Runner class contains the main method, which is used to setup a series of 
experiments for applying Multi-Agent Reinforcement Learning (MARL) to the 
Predator-Prey domain. 

The predator-prey domain consists of an NxN grid-based environment, where a 
group of predators (in this case, 2 predators) try to catch a single prey. To
consider the prey caught, one predator must share the same grid-cell as the 
prey, while the other predator is in an adjacent cell. The predators and prey
(agents) are all initialized in random locations on the grid-map. The predators 
must coordinate and move together towards the prey in order to catch it, while 
the prey tries to move away from the predators in order to stay alive. The 
scoring for this game is based on a Time-to-Goal (TG) measurement, which counts 
the number of discrete time steps the predators take to catch the prey. 

The simulation of the predator-prey game is performed numerous times. Initially, 
the predators choose random actions at each time step. Every time the prey is 
caught, the predators learn what a "good" action is, given the current state of
the environment (location of the agents on an NxN grid). The learning is 
performed using Q-tables. After a number of games have been played, the 
predators will be able to catch the prey faster and faster, as they are able
to learn the direction they need to move to catch the prey. 

This particular implementation consists of the Partaker-Sharer Framework
introduced in "A Q-values Sharing Framework for Multiagent Reinforcement 
Learning under Budget Constraint", available at https://arxiv.org/abs/2011.14281


*/
public class Runner{
    
    public static void main(String[] args) {
        // Environment and simulation variables
        // define grid size(NxN)
        int N = 10;
        // number of times the game is played
        int numIterations = 20000; 
        // number of games played to average the score (reduces outliers)
        int numTG2Avg = 100;
        //number of iterations of size numTG2Avg
        int numRuns = numIterations / numTG2Avg;
        // maximum number of steps per game
        int maxTG = 5000; 
        // budget values for the partaker-sharer framework
        int bAsk = 3000, bGive = 3000; 
        
        // initialize agents
        PredatorAgent[] predators = {new PredatorAgent(), new PredatorAgent()};
        for(int i=0;i<predators.length;i++){
            predators[i].setBudgets(bAsk,bGive);
        }
        PreyAgent prey = new PreyAgent();
        Agent[] agents = {predators[0], predators[1], prey};
        
        
        // Simulation result variables
        int[] TG = new int[numRuns];
        int[] budgetConsumption = new int[numIterations];
        SimulationResult[] results = new SimulationResult[2];
        
        // Perform the simulations
        int idx = 0;
        int iteration = 0;
        for(int j=0;j<numRuns;j++){
            // sum used to track the TG for the current set of simulations
            int sum = 0;
            for(int i = 0;i<numTG2Avg;i++){
                // get the result of the current simulation
                SimulationResult result = simulate(agents, N, maxTG);
                //store first and last simulations
                if((j==0 && i==0) || (j==numRuns-1 && i==numTG2Avg-1)) 
                    results[idx++] = result;
                // get the TG for the current simulation
                sum += result.getTime();
                // update the budget consumption with the results of the simulation
                budgetConsumption[iteration++] = 2*(bAsk + bGive) - (predators[0].getBudget() + predators[1].getBudget());
            }
            // compute average TG for the set of simulations
            TG[j] = sum / numTG2Avg;
        }
        
        // output time and budget results to CSV files
        write2CSV(TG, "TG_out");
        write2CSV(budgetConsumption, "budget");
        
        // display the graphical simulation for the saved results
        for(SimulationResult result:results){
            GraphicSimulation gs = new GraphicSimulation(result);
        }
    }//end main
    
    public static SimulationResult simulate(Agent[] agents, int N, int maxTG){
        /*
        This method performs a single simulation of the predator prey game. 
        Given a list of agents (3 agents: predator1, predator2, and prey), the
        size of the grid-map N, and the maximum number of time steps permitted, 
        the game is initiallized and simulated, where the result is returned in 
        a SimulationResult object. The result contains an array of the agent
        positions at each time step, as well as the number of time steps that 
        were taken during the simulation. 
        */
        Random rand = new Random();
        SimulationResult result = new SimulationResult(N);
        
        // randomly position the agents on the grid
        for(int i=0;i<agents.length;i++){
            int x = rand.nextInt(N);
            int y = rand.nextInt(N);
            // set the coordinates of the agent
            agents[i].setCoordinates(x, y);
            // inform the agent of the environment size (NxN)
            agents[i].setEnv(N);
        }
        
        // add the initial positions of each agent to the result
        result.addStep(agents);
        
        // Main game loop: actions are computed and applied for each agent at
        // each iteration. 
        for(int t=1;t<=maxTG;t++){
            // The action of each agent for the current time step is computed
            String[] actions = new String[agents.length];
            for(int i=0;i<agents.length;i++){
                // compute predator action
                if(!agents[i].isPrey()){
                    // get the current state of the environment relative to the
                    // current agent. 
                    String state = computeState(agents, i);
                    // get the index to the other (alternate) predator agent
                    int alt = (i+1)%2;
                    // get the agents action for the current time step
                    actions[i] = ((PredatorAgent)agents[i]).getAction(state, (PredatorAgent)agents[alt]);
                    //actions[i] = ((PredatorAgent)agents[i]).getAction(state);
                }
                else
                    // get the prey agents action for the current time step
                    actions[i] = ((PreyAgent)agents[i]).getAction(agents);
            }
            
            //update environemtn to next state by applying the agents actions
            for(int i=0;i<agents.length;i++){
                agents[i].performAction(actions[i]);
            }
            
            // add the current location of each agent to the result
            result.addStep(agents);
            
            // compute agent reward
            // reward of 1 if the prey is caught, 0 otherwise
            int reward = preyCaught(agents) ? 1 : 0;
            
            //update Q-tables based on teh reward
            for(int i=0;i<agents.length-1;i++){
                //System.out.println("--- Updating: "+i);
                ((PredatorAgent)agents[i]).update(computeState(agents, i), reward);
            }
            
            // end simulation if prey is caught
            if(preyCaught(agents)){
                break;
            }
        }
        return result;
    }//end method
    
    public static String computeState(Agent[] agents, int i){
        /*
        compute the state of the environment relative to a given agent. 
        agents represents the list of agents, and i represents the agent for 
        which the state is being computed. 
        
        The state is represented as a string in the form (int)(int)(int)(int). 
        The first two integers represent the x and y Manhatten distances from
        the current predator agent to the other predator agent. The second pair
        is the distance from the current predator to the prey. 
        */
        
        // compute local distances
        int xcur = agents[i].getx();
        int ycur = agents[i].gety();
        int[] x = new int[agents.length-1];
        int[] y = new int[agents.length-1];
        int idx = 0;
        for(int j=0;j<agents.length;j++){
            if(j!=i){
                x[idx] = agents[j].getx();
                y[idx] = agents[j].gety();
                idx++;
            }
        }
        
        // build state as string
        String state = "";
        for(int j=0;j<x.length;j++){
            state += "("+(xcur-x[j])+")("+(ycur-y[j])+")";
        }
        return state;
    }//end method
    
    public static boolean preyCaught(Agent[] agents){
        /*
        Function to determine if the prey has been caught. The prey is caught 
        if two conditions are met: 1) the prey and one of the predators must
        share the same cell in the environment, and 2) the second predator must
        be in an adjacent cell to the prey. 
        */
        PreyAgent prey = (PreyAgent)(agents[2]);
        boolean cellShared = false;
        boolean adjCellFilled = false;
        
        // iterate through the predator agents and check conditions
        for(int i=0;i<agents.length-1;i++)
            if(prey.equals(agents[i]))
                cellShared = true;
            else if(prey.isAdjacent(agents[i]))
                adjCellFilled = true;
        return cellShared && adjCellFilled;
    }//end method
    
    public static void write2CSV(int[] TG, String filename){
        /*
        Function to output and array of integer values to a CSV file. 
        */
        try{
            File file = new File(filename + ".csv");
            int i = 1;
            while(file.exists()){
                i++;
                file = new File(filename+"("+i+").csv");
            }
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            for(int t:TG){
                writer.write(t+"\n");
            }
            writer.close();
        }catch(IOException e){
            System.out.println("ERROR: Error occurred while trying to write CSV file.");
        }
        
    }//end method
}//end class