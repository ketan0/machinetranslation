public interface Curve
{
  void draw(SketchPad pad);
  void translate(double tx, double ty);
  void scale(double sx, double sy);
  void rotate(double degrees);
  Curve copy();
}