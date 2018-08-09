import java.io.*;
import java.util.*;
public class ViterbiAlignment
{
  public ViterbiAlignment()
  {
  }
  
  public void v(String e, String f, SketchPad pad, SMT3 s) throws IOException
  {
    e=e.toLowerCase();
    f=f.toLowerCase();
    CompositeCurve curve=new CompositeCurve();
    for(int i=1;i<=7;i++)
      curve.add(new Line((double)(i)/8,0,(double)(i)/8,1));
    for(int i=1;i<=7;i++)
      curve.add(new Line(0, (double)(i)/8,1,(double)(i)/8));
    String[]es=wordArray(e);
    String[]fs=wordArray(f);
    CompositeCurve eng=new CompositeCurve();
    CompositeCurve fr=new CompositeCurve();
    for(int i=0;i<es.length;i++)
    {
      System.out.println(es[i]);
      System.out.println(1-((double)(i)/8+1.0/16.0));
      eng.add(new Text(es[i],0,(double)(7-i)/8+1.0/16.0));
    }
    for(int i=0;i<fs.length;i++)
    {
      System.out.println(fs[i]);
      System.out.println(1-((double)(i)/8+1.0/16.0));
      fr.add(new Text(fs[i],(double)(i)/8+0.35,1+(double)(i)/25));
    }
    curve.scale(0.75,0.75);
    eng.scale(0.75,0.75);
    fr.scale(0.75,0.75);
    curve.translate(0.25,0);
    int[]a=s.viterbi(e,f,0);
    System.out.println("\n[");
    for(int x:a)
      System.out.println(x+",");
    System.out.println("]");
    for(int i=0;i<a.length;i++)
    {
      System.out.println(7-a[i]+","+ i);
      fill(7-i,a[i], pad);
    }
    curve.draw(pad);
    eng.draw(pad);
    fr.draw(pad);
  }
  
  public static void fill(int row, int col, SketchPad pad)
  {
    CompositeCurve square=new CompositeCurve();
    for(int i=1;i<=99;i++)
      square.add(new Line((double)(col)/8+.125*((double)(i)/100), (double)(row)/8, (double)(col)/8+.125*((double)(i)/100), (double)(row)/8+.125));
    square.scale(0.75,0.75);
    square.translate(0.25,0);
    square.draw(pad);
  }
  
  public String[] wordArray(String sentence)
  {
    ArrayList<Integer>spaces=new ArrayList<Integer>();
    for(int i=0;i<sentence.length();i++)
    {
      if(sentence.substring(i,i+1).equals(" "))
        spaces.add(i);
    }
    String[]words=new String[spaces.size()+1];
    if(spaces.size()>0)
    {
      words[0]=sentence.substring(0,spaces.get(0));
      for(int i=0;i<spaces.size()-1;i++)
        words[i+1]=sentence.substring(spaces.get(i)+1,spaces.get(i+1));
      words[words.length-1]=sentence.substring(spaces.get(spaces.size()-1)+1);
      return words;
    }
    return new String[]{sentence};
  }
}