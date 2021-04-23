
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;


public class GraphicSimulation{
    
    private JTextField txtSpeed;
    private Timer timer;
    private SimulationResult result;
    private int idx = 0;
    private JPanel drawingPanel;
    private JButton btnPause;
    private JButton btnSlow;
    private JButton btnMed;
    private JButton btnFast;
    private int size = 400;
    
    
    public GraphicSimulation(SimulationResult result){
        this.result = result;
        
        // initialize JFrame
        JFrame frame = new JFrame("Predator-Prey Game Simulation");
        //frame.setSize(size+50, size);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        //set JFrame layout properties
        frame.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints frameGrid = new GridBagConstraints();
        
        
        //----------------------------------------- add the graphics
        drawingPanel = new JPanel();
        drawingPanel.setPreferredSize(new Dimension(size, size));
        drawingPanel.setMinimumSize(new Dimension(size, size));
        //drawingPanel.setSize(size, size);
        //drawingPanel.setBorder(BorderFactory.createTitledBorder("Simulation"));
	frameGrid.gridx = 1;
        frameGrid.gridy = 1;
        frame.getContentPane().add(drawingPanel, frameGrid);
        
        
        //----------------------------------------- add timer and begin
        ActionListener updateCaller = new ActionListener(){
            public void actionPerformed(ActionEvent event){update();}};
        
        timer = new Timer(1000, updateCaller);
        
        //----------------------------------------- add the simulation controls
        JPanel controlPanel = new JPanel(new GridLayout(1, 4));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
	frameGrid.gridx = 1;
        frameGrid.gridy = 2;
        frame.getContentPane().add(controlPanel, frameGrid);
        
        btnPause = new JButton("PAUSE");
        controlPanel.add(btnPause);
        btnPause.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                timer.stop();
            }
        });
        
        btnSlow = new JButton("SLOW");
        controlPanel.add(btnSlow);
        btnSlow.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                timer.setDelay(1000);
                timer.start();
            }
        });
        
        btnMed = new JButton("MEDIUM");
        controlPanel.add(btnMed);
        btnMed.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                timer.setDelay(500);
                timer.start();
            }
        });
        
        btnFast = new JButton("FAST");
        controlPanel.add(btnFast);
        btnFast.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                timer.setDelay(50);
                timer.start();
            }
        });
        
        
        frame.pack();
        frame.setVisible(true);
        update();
    }//end constructor
    
    public void update(){
        if(idx >= result.size()){
            timer.stop();
            btnPause.setEnabled(false);
            btnSlow.setEnabled(false);
            btnMed.setEnabled(false);
            btnFast.setEnabled(false);
        }
        else{
            BufferedImage img = getFrameImage();
            ((Graphics2D)(drawingPanel.getGraphics())).drawImage(img, 0, 0, drawingPanel);
        }
        idx++;
    }//end method
    
    private BufferedImage getFrameImage(){//int[][] coordinates, int N, int size){
        int N = result.getGridSize();
        int squareSize = size / N;
        
        BufferedImage img = new BufferedImage(size,size,BufferedImage.TYPE_BYTE_INDEXED);
        Graphics2D g2 = img.createGraphics();
        
        //background color
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, size, size);
        
        //draw lines
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        for(int i = 1;i<N;i++){
            g2.drawLine(i*squareSize, 0, i*squareSize, size); 
            g2.drawLine(0, i*squareSize, size, i*squareSize); 
        }
        
        int[][] agents = result.getAgentCoordinates(idx);
        int gap = 2;
        
        for(int i=0;i<agents.length;i++){
            int gridx = agents[i][0] * squareSize;
            int gridy = agents[i][1] * squareSize;
            
            int x1 = gridx+gap;
            int y1 = gridy+gap;
            int x2 = squareSize - gap*2;
            int y2 = squareSize - gap*2;
            
            if(i==agents.length-1)
                g2.setColor(Color.GREEN);
            else
                g2.setColor(Color.RED);
            g2.fillRect(x1, y1, x2, y2);
        }
        
        return img;
    }//end method
    
}//end class