public class Text implements Curve
{
  private String text;
  private double x;
  private double y;
  private double angle;
  
  public Text(String t, double xx, double yy)
  {
    x=xx;
    y=yy;
    text=t;
    angle=0;
  }
  public Text(String t, double xx, double yy,double a)
  {
    x=xx;
    y=yy;
    text=t;
    angle=a;
  }
  
  public void draw(SketchPad pad)
  {
    if(angle==0)
      pad.drawText(text, x, y);
    else
      pad.drawText(text, x, y, angle);
  }
  public void translate(double tx, double ty)
  {
  }
  public void scale(double sx, double sy)
  {
    x*=sx;
    y*=sy;
  }
  public void rotate(double degrees)
  {
  }
  public Curve copy()
  {
    return new Text(text, x, y);
  }
}