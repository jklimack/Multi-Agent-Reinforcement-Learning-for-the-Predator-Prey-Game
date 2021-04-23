
The application of Multi-Agent Reinforcement Learning (MARL) to the Predator-Prey
domain using a Partaker-Sharer Framework. 

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