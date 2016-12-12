import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;
import java.util.ArrayList;

/**
 * Provides a GUI for the PointInPolygon testing methods
 * 
 * @author Matthew Dutton
 * @version 12/12/16
 * 
 * Current Problems: The polygon will not draw if the window is covered?
 *                      -Fix: Re-implement painting methods differently
 *                      
 * Note: Drawing the shape on the canvas will cast double -> int. Take heed.
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
    //Button (For drawing mode)
    private Button button;
    private boolean drawMode;
    private double[][] points;
    private boolean colorMode;

    //Used to contain points added in draw mode
    private ArrayList<double[]> drawPoints;

    private PointInPolygon polytest;

    /**
     * Main method for the PointInPolygon program
     * @ param args The arguments for running the program
     *                -"draw" Sets the program in drawing mode. Click to place points to draw a polygon
     *                - Coordinates in the form "x1,y1 x2,y2 x3,y3"... Initiliazes the program with the shape specified. Drawing disabled
     */
    public static void main(String[] args){
        GuiHandler gui = new GuiHandler(args);
    }

    /**
     * Constructor for objects of class GuiHandler
     * @ param args  Same as main method:
     *                -"draw" Sets the program in drawing mode. Click to place points to draw a polygon
     *                - Coordinates in the form "x1,y1 x2,y2 x3,y3"... Initiliazes the program with the shape specified. Drawing disabled
     */
    public GuiHandler (String[] args)
    {
        if(args[0].equals("draw")){
            drawMode = true;
            drawPoints = new ArrayList<double[]>();
        }
        else{
            // Splits and interprets the String input 10,10 20,20 --> { {10,10} , {20,20} }
            drawMode = false;
            String[] pointStr = args[0].split(" ");
            points = new double[pointStr.length][2];
            for(int i = 0; i < points.length; i++){
                String pt[] = pointStr[i].split(",");
                points[i][0] = Double.parseDouble(pt[0]);
                points[i][1]  = Double.parseDouble(pt[1]);
            }
        }

        if(args.length > 1 && args[1].equals("color")){
            colorMode = true;
        }
        else{
            colorMode = false;
        }
        makeWindow();

        if(drawMode == false){
            //Sets up the initial polygon if drawMode == false
            polytest = new PointInPolygon(points);
            label.setText("Drag mouse to test points");
            jpanel.DrawPoly(points);
        }
    }

    /**
     * Sets up the GUI for the program.
     */
    private void makeWindow(){
        //Sets up Frame
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

        //Makes the Center Panel - Defaults to FlowLayout()
        jpanel = new Drawer();
        jpanel.setBackground(new Color(220,220,220));
        jpanel.addMouseMotionListener(new MouseTracker());
        jpanel.addMouseListener(new MouseTracker2());

        //Adds Button
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
        frame.add(jpanel,BorderLayout.CENTER);

        //Shows Frame
        frame.setVisible(true);
    }

    class Drawer extends JPanel{
        public Drawer(){
        }

        /**
         * Clears canvas by filling it with the initial color
         */
        public void Clear(){
            Graphics g = getGraphics();
            g.setColor(new Color(220,220,220));
            g.fillRect(0,0,650,650);
            g.dispose();
        }

        /**
         * Clears canvas by filling it with a specified color
         * 
         * @param c the color to fill with
         */
        public void Clear(Color c){
            Graphics g = getGraphics();
            g.setColor(c);
            g.fillRect(0,0,650,650);
            g.dispose();
        }

        /**
         * Draws a polygon specified by an array of vertices. Connects the first and last points
         * 
         * @param shape the shape to draw
         */
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

                if(colorMode == true){
                    if(polytest.checkPoint(new double[] {e.getX(),e.getY()}) == true){
                        jpanel.Clear(new Color(225, 248, 225));
                    }
                    else{
                        jpanel.Clear();
                    }
                    jpanel.DrawPoly(points);
                }
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

        public void mouseReleased(MouseEvent e){
            if(polytest != null && points != null){
                if(colorMode == true){
                    jpanel.Clear();
                    jpanel.DrawPoly(points);
                }
            }
        }
    }  
}
