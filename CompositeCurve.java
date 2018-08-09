import java.util.List;
import java.util.ArrayList;

public class CompositeCurve implements Curve
{
  private List<Curve> curves;
  
  public CompositeCurve()
  {
    curves=new ArrayList<Curve>();
  }
  
  public void draw(SketchPad pad)
  {
    for(Curve c: curves)
      c.draw(pad);
  }
  
  public void add(Curve n)
  {
    curves.add(n);
  }
  
  public void translate(double tx, double ty)
  {
    for(Curve c: curves)
      c.translate(tx, ty);
  }
  
  public void scale(double sx, double sy)
  {
    for(Curve c: curves)
      c.scale(sx, sy);
  }
  
  public void rotate(double degrees)
  {
    for(Curve c: curves)
      c.rotate(degrees);
  }
  
  public Curve copy()
  {
    CompositeCurve comp=new CompositeCurve();
    for(Curve c: curves)
      comp.add(c.copy());
    return comp;
  }
}