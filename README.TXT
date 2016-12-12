
Code based off: http://geomalgorithms.com/a03-_inclusion.html

Point-in-polygon
--------------------------
To compile -> javac GuiHandler.java
To run     -> java GuiHandler args[]
Arguments  -> "draw" - starts the program in draw mode: click to add points to draw a polygon
              "x1,y1 x2,y2 x3,y3"... For xn,yn doubles - starts the program with the shape specified (end point will connect to start point)

Notes:
- For argument "x1,y1 ...", load on a clear desktop as the draw method will not work properly if the window loads behind an existing window?
- Hold and drag mouse in testing mode to check if points are in the polygon.
- In draw mode, finalising the polygon will connect the end point to the start point

