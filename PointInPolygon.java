
/**
 * Tests whether a given point is within a polygon
 * 
 * @author  Matthew Dutton
 * @author  Dan Sunday
 * @version 9/11/16
 */
public class PointInPolygon
{
    Point[] polygon;
    
    /**
     * Constructor for objects of class PointInPolygon
     * 
     *  @param shape The vertices of the polygon (x-y coords), in order {{x1,y1},{x2,y2},...}
     */
    public PointInPolygon(double[][] shape)
    {
        polygon = getVertices(shape);
    }

    /**
     * Returns an array of Points for the vertices of the polygon given a set of ordered points
     * 
     * @param  boundary The ordered set of boundary points for the shape 
     */
    private Point[] getVertices(double[][] boundary)
    {
        Point[] poly = new Point[boundary.length];

        for(int i = 0; i < poly.length; i++){   
            poly[i] = new Point(boundary[i][0],boundary[i][1]);
        }

        return poly;
    }
    
    /**
     * Checks if a given point is in the polygon by calling checkPoint(Point p)
     * 
     * @param   point  The point to check {x,y}
     * @return         true if the point is in the polygon, false otherwise
     */
    public boolean checkPoint(double[] point){
        return checkPoint(new Point(point[0],point[1]));
    }
    /**
     * Checks if a given Point is in the polygon
     * 
     * @param   point  The Point to check
     * @return         true if the point is in the polygon, false otherwise
     */
    public boolean checkPoint(Point p){
        return wn_PnPoly( p, polygon, polygon.length ) != 0;
        //TODO: check if n needs to be changed. Also see if wn algorithm needs wraparound on the P array
    }
    
    /**
     * Methods below are adapted from [http://geomalgorithms.com/a03-_inclusion.html]
     * ========================================================================
     * Copyright 2000 softSurfer, 2012 Dan Sunday
     * This code may be freely used and modified for any purpose
     * providing that this copyright notice is included with it.
     * SoftSurfer makes no warranty for this code, and cannot be held
     * liable for any real or imagined damage resulting from its use.
     * Users of this code must verify correctness for their application.
     * ========================================================================
     * 
     * Minor changes, adapted for Java
     * Added wraparound for vertex array access [ (i+1) --> (i+1)%polygon.length ]
     */
    
    /** 
     *  isLeft(): tests if a point is Left|On|Right of an infinite line.
     *  
     *  Input:  three points P0, P1, and P2
     *  Return: >0 for P2 left of the line through P0 and P1
     *          =0 for P2  on the line
     *          <0 for P2  right of the line
     *          
     *  See: Algorithm 1 "Area of Triangles and Polygons"
     */
    private double isLeft( Point P0, Point P1, Point P2 )
    {
        return ( (P1.x - P0.x) * (P2.y - P0.y)
            - (P2.x -  P0.x) * (P1.y - P0.y) );
    }

    /** 
     *  cn_PnPoly(): crossing number test for a point in a polygon
     *  
     *  Input:   P = a point,
     *           V[] = vertex points of a polygon V[n+1] with V[n]=V[0]
     *  Return:  0 = outside, 1 = inside
     *  
     *  This code is patterned after [Franklin, 2000]
     */
    private int cn_PnPoly( Point P, Point[] V, int n )
    {
        int cn = 0;    // the  crossing number counter

        // loop through all edges of the polygon
        for (int i=0; i<n; i++) {    // edge from V[i]  to V[i+1]
            if (((V[i].y <= P.y) && (V[(i+1)%polygon.length].y > P.y))     // an upward crossing
            || ((V[i].y > P.y) && (V[(i+1)%polygon.length].y <=  P.y))) {  // a downward crossing
                // compute  the actual edge-ray intersect x-coordinate
                double vt = (P.y  - V[i].y) / (V[(i+1)%polygon.length].y - V[i].y);
                if (P.x <  V[i].x + vt * (V[(i+1)%polygon.length].x - V[i].x)){ // P.x < intersect
                    ++cn;   // a valid crossing of y=P.y right of P.x
                }
            }
        }
        return (cn&1);    // 0 if even (out), and 1 if  odd (in)
    }

    /** 
     *  wn_PnPoly(): winding number test for a point in a polygon
     *  
     *  Input:   P = a point,
     *           V[] = vertex points of a polygon V[n+1] with V[n]=V[0]
     *  Return:  wn = the winding number (=0 only when P is outside)
     */
    private int wn_PnPoly( Point P, Point[] V, int n )
    {
        int wn = 0;    // the  winding number counter

        // loop through all edges of the polygon
        for (int i=0; i<n; i++) {   // edge from V[i] to  V[i+1]
            if (V[i].y <= P.y) {          // start y <= P.y
                if (V[(i+1)%polygon.length].y  > P.y)      // an upward crossing
                    if (isLeft( V[i], V[(i+1)%polygon.length], P) > 0)  // P left of  edge
                        ++wn;            // have  a valid up intersect
            }
            else {                        // start y > P.y (no test needed)
                if (V[(i+1)%polygon.length].y  <= P.y)     // a downward crossing
                    if (isLeft( V[i], V[(i+1)%polygon.length], P) < 0)  // P right of  edge
                        --wn;            // have  a valid down intersect
            }
        }
        return wn;
    }
}
