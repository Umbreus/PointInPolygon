import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;

import java.util.ArrayList;

/**
 * Provides a GUI for the program
 * 
 * @author Matthew Dutton
 * @version 10/11/16
 * 
 * Note: Drawing the shape will cast double -> int. Take heed.
 */
public class GuiHandler
{
    //Makes a program window
    private Frame frame;
    //A container for components (including other panels)
    private Panel panel;
    private Drawer jpanel;
    //Text labels
    private Label label;

    private Button button;

    private boolean drawMode;
    private double[][] points;

    private ArrayList<double[]> drawPoints;

    private PointInPolygon polytest;

    public static void main(String[] args){
        GuiHandler gui = new GuiHandler(args);
    }

    /**
     * Constructor for objects of class GuiHandler
     * 
     */
    public GuiHandler (String[] args)
    {
        if(args[0].equals("draw")){
            drawMode = true;
            drawPoints = new ArrayList<double[]>();
        }
        else{
            // 10,10 20,20 --> { {10,10} , {20,20} }
            drawMode = false;
            String[] pointStr = args[0].split(" ");
            points = new double[pointStr.length][2];
            for(int i = 0; i < points.length; i++){
                String pt[] = pointStr[i].split(",");
                points[i][0] = Double.parseDouble(pt[0]);
                points[i][1]  = Double.parseDouble(pt[1]);
            }
        }

        makeWindow();

        if(drawMode == false){
            polytest = new PointInPolygon(points);
            label.setText("Drag mouse to test points");
            jpanel.DrawPoly(points);
        }
    }

    private void makeWindow(){
        //Set up Frame
        frame = new Frame("Point-In-Polygon Tester");
        frame.setSize(650,650);

        //Seperated into N,E,S,W and CENTER
        frame.setLayout(new BorderLayout());

        //Makes the window close when the close button is pushed
        frame.addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent windowEvent){
                    System.exit(0);
                } 
            });

        //Create Components
        label = new Label("Point-In-Polygon Tester", Label.CENTER);

        //Makes the Center Panel
        //Defaults to FlowLayout()

        /* Replaced with below
        panel = new Panel();
        panel.setBackground(new Color(220,220,220));
        panel.addMouseMotionListener(new MouseTracker());
        panel.addMouseListener(new MouseTracker2());
         */
        jpanel = new Drawer();
        jpanel.setBackground(new Color(220,220,220));
        jpanel.addMouseMotionListener(new MouseTracker());
        jpanel.addMouseListener(new MouseTracker2());

        if(drawMode == true){
            button = new Button("Finalize Shape");
            frame.add(button,BorderLayout.SOUTH);
            button.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e) {
                        if(drawMode == true){
                            jpanel.DrawPoly(new double[][] {drawPoints.get( drawPoints.size()-1 ),drawPoints.get(0)} );
                            points = new double[drawPoints.size()][2];
                            for(int i = 0; i < points.length; i++){
                                points[i] = drawPoints.get(i);
                            }
                            polytest = new PointInPolygon(points);
                            label.setText("Shape Finalized.");
                            
                            button.setLabel("New Shape");
                            drawMode = false;
                        }
                        else{
                            drawPoints = new ArrayList<double[]>();
                            polytest = null;
                            button.setLabel("Finalize Shape");
                            drawMode = true;
                            jpanel.Clear();
                        }
                    }
                });
        }

        //Adds Components to Frame
        frame.add(label,BorderLayout.NORTH);
        //frame.add(panel,BorderLayout.CENTER);
        frame.add(jpanel,BorderLayout.CENTER);

        //Shows Frame
        frame.setVisible(true);

        //jpanel.DrawPoly(new double[][] {{100,100},{100,400},{400,400},{400,100}} );
    }

    class Drawer extends JPanel{
        public Drawer(){
        }

        public void Clear(){
            Graphics g = getGraphics();
            g.setColor(new Color(220,220,220));
            g.fillRect(0,0,650,650);
            g.dispose();
        }
        
        public void DrawPoly(double[][] shape){
            Graphics g = getGraphics();
            for(int i = 0; i < shape.length ; i++){
                g.drawLine((int)shape[i][0], (int)shape[i][1], (int)shape[(i+1)%shape.length][0], (int)shape[(i+1)%shape.length][1]);
            }
            g.dispose();
        }
    }

    class MouseTracker implements MouseMotionListener {
        public void mouseDragged(MouseEvent e) {
            if(polytest != null){
                label.setText("Mouse Moved: ("+e.getX()+", "+e.getY() +")"+"  Inside Polygon: " + polytest.checkPoint(new double[] {e.getX(),e.getY()}) );
            }
        }

        public void mouseMoved(MouseEvent e) {
            label.setText("Mouse Moved: ("+e.getX()+", "+e.getY() +")");
        }    
    }  

    class MouseTracker2 implements MouseListener {
        public void mouseClicked(MouseEvent e) {
            if(drawMode == true){
                drawPoints.add(new double[] {e.getX(),e.getY()} );
                if(drawPoints.size() > 1){
                    jpanel.DrawPoly(new double[][] {drawPoints.get( drawPoints.size()-1 ),drawPoints.get( drawPoints.size()-2 )} );
                }
                label.setText("Point Placed: ("+e.getX()+", "+e.getY() +")");
            }
        }  

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e)  {}

        public void mousePressed(MouseEvent e) {}

        public void mouseReleased(MouseEvent e){}
    }  
}
