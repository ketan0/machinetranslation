import java.awt.*;

public class Line implements Curve
{
  double x1;
  double y1;
  double x2;
  double y2;
  Color color;
  boolean isColored;
  
  public Line(double xone, double yone, double xtwo, double ytwo)
  {
    x1=xone;
    y1=yone;
    x2=xtwo;
    y2=ytwo;
  }
  
  public Line(Color c, double xone, double yone, double xtwo, double ytwo)
  {
    x1=xone;
    y1=yone;
    x2=xtwo;
    y2=ytwo;
    color=c;
    isColored=true;
  }
  
  public void draw(SketchPad pad)
  {
    if(!isColored)
      pad.drawLine(x1, y1, x2, y2);
    else
      pad.drawLine(color, x1, y1, x2, y2);
  }
  
  public void translate(double tx, double ty)
  {
    x1+=tx;
    x2+=tx;
    y1+=ty;
    y2+=ty;
  }
  
  public void scale(double sx, double sy)
  {
    x1*=sx;
    x2*=sx;
    y1*=sy;
    y2*=sy;
  }
  
  public void rotate(double degrees)
  {
    double temp=x1;
    x1=x1*Math.cos(Math.toRadians(degrees))-y1*Math.sin(Math.toRadians(degrees));
    y1=temp*Math.sin(Math.toRadians(degrees))+y1*Math.cos(Math.toRadians(degrees));
    
    temp=x2;
    x2=x2*Math.cos(Math.toRadians(degrees))-y2*Math.sin(Math.toRadians(degrees));
    y2=temp*Math.sin(Math.toRadians(degrees))+y2*Math.cos(Math.toRadians(degrees));
    
  }
  
  public Curve copy()
  {
    return isColored?new Line(color, x1,y1,x2,y2):new Line(x1, y1, x2, y2);
  }
  
  public void getValues()
  {
    System.out.println("x1 :"+x1+" x2 :"+x2+" y1 :"+y1+" y2 :"+y2);
  }
}