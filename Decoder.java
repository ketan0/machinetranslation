import java.util.*;
import java.io.*;

public class Decoder
{
  //HashMap<String, Double>invt;
  HashMap<String, Double>t;
  HashMap<String, Integer>c;
  SMT3 s;
  LM m;
  
  public Decoder()
  {
    m=new LM();
    s=new SMT3();
    t=s.getT();
    c=m.getC();
    System.out.println("c.size: "+c.size());
    //System.out.println("bc.size: "+bc.size());
    //System.out.println("tc.size: "+tc.size());
//    invt=new HashMap<String, Double>();
//    System.out.println("init invt");
//    for(String x: t.keySet())//0 if never seen e before
//    {
//      invt.put(x.substring(x.indexOf(" ")+1)+" "+x.substring(0,x.indexOf(" ")),c.get(x.substring(x.indexOf(" ")+1))==null?0:t.get(x)*c.get(x.substring(x.indexOf(" ")+1)));
//    }
    //System.out.println("invt.size: "+invt.size());
    //System.out.println("ayy lma0");
    //deserialize();
    //ds();
  }
  
  public Decoder(LM l, SMT3 smt3)
  {
    m=l;
    s=smt3;
    t=s.getT();
    c=m.getC();
    //System.out.println("c.size: "+c.size());
    //System.out.println("bc.size: "+bc.size());
    //System.out.println("tc.size: "+tc.size());
//    invt=new HashMap<String, Double>();
//    System.out.println("init invt");
//    for(String x: t.keySet())//0 if never seen e before
//    {
//      invt.put(x.substring(x.indexOf(" ")+1)+" "+x.substring(0,x.indexOf(" ")),c.get(x.substring(x.indexOf(" ")+1))==null?0:t.get(x)*c.get(x.substring(x.indexOf(" ")+1)));
//    }
    //System.out.println("invt.size: "+invt.size());
    //System.out.println("ayy lma0");
    //deserialize();
    //ds();
  }
  
  public void ds()
  {
    try
    {
      FileInputStream fileIn = new FileInputStream("t.ser");
      ObjectInputStream in = new ObjectInputStream(fileIn);
      t = (HashMap<String, Double>) in.readObject();
      System.out.println("t deserialized");
      in.close();
      fileIn.close();
    }catch(IOException i)
    {
      System.out.println("failed to deserialize t");
      i.printStackTrace();
    }catch(ClassNotFoundException c)
    {
      System.out.println("class not found");
      c.printStackTrace();
    }
    try
    {
      FileInputStream fileIn = new FileInputStream("c.ser");
      ObjectInputStream in = new ObjectInputStream(fileIn);
      c = (HashMap<String, Integer>) in.readObject();
      System.out.println("c deserialized");
      in.close();
      fileIn.close();
    }catch(IOException i)
    {
      System.out.println("failed to deserialize c");
      i.printStackTrace();
    }catch(ClassNotFoundException c)
    {
      System.out.println("class not found");
      c.printStackTrace();
    }
  }
  
  public void serialize()
  {
//    try
//    {
//      FileOutputStream fileOut =
//        new FileOutputStream("invt.ser");
//      ObjectOutputStream out = new ObjectOutputStream(fileOut);
//      out.writeObject(invt);
//      out.close();
//      fileOut.close();
//      System.out.printf("data is saved in invt.ser");
//    }catch(IOException i)
//    {
//      i.printStackTrace();
//    }
  }
  
  public void deserialize()
  {    
//    try
//    {
//      FileInputStream fileIn = new FileInputStream("invt.ser");
//      ObjectInputStream in = new ObjectInputStream(fileIn);
//      invt = (HashMap<String, Double>) in.readObject();
//      in.close();
//      fileIn.close();
//    }catch(IOException i)
//    {
//      System.out.println("failed to deserialize invt");
//      i.printStackTrace();
//    }catch(ClassNotFoundException c)
//    {
//      System.out.println("class not found");
//      c.printStackTrace();
//    }
  }
  
  public String decode(String f) throws IOException
  {
    //init invt
    f=f.toLowerCase();
//    System.out.println("serializing...");
//    serialize();
//    System.out.println("done");
      //choose depth-first translation
    ArrayList<String[]>e=new ArrayList<String[]>();
    String translation="";
    ArrayList<String>words=new ArrayList<String>();
    for(String w:wordArray(f))
      e.add(best(w));
    for(String[] best:e)
    {
      System.out.println("|");
      for(String w:best)
        System.out.println(w);
    }
    //loop over the whole sentence, make change that gives the biggest improvement in score
    for(String[] best:e)
    {
      translation+=best[0]+" ";
      words.add(best[0]);
    }
    translation=translation.substring(0,translation.length()-1);
    System.out.println("greedy: "+translation);
//    int[]a=s.viterbi(f, translation,0);//alignments
    double p=score(f, translation);
    int[]maxloc=new int[2];
    double maxp=p;
//    int[]ft=new int[words.size()];   //fertilities, init 0
//    for(int x:a)
//      ft[x]++;
    do
    {
      maxloc[0]=0;
      maxloc[1]=0;
      System.out.println(words);
      for(int i=0;i<words.size();i++)
      {
        String temp=words.get(i);
        for(int j=1;j<10;j++)
        {
          translation="";
          words.set(i,e.get(i)[j]);
          for(String w:words)
            translation+=w+" ";
          double p2=score(f, translation.substring(0,translation.length()-1));
          if(p2>maxp)
          {
            maxp=p2;
            maxloc[0]=i;
            maxloc[1]=j;
          }
        }
        words.set(i,temp);
        System.out.println(words);
      }
      System.out.println("swapped "+words.get(maxloc[0])+" for "+e.get(maxloc[0])[maxloc[1]]);
      words.set(maxloc[0],e.get(maxloc[0])[maxloc[1]]);
    }while(maxloc[1]!=0);
    translation="";
    for(String w:words)
      translation+=w+" ";
    return translation.substring(0,translation.length()-1);
  }
  
  public double score(String e, String f) throws IOException
  {
    return m.lm(e)*s.tm(f,e,0);
  }
  
  public String[] best(String f)
  {
    double[]max=new double[]{0,0,0,0,0,0,0,0,0,0};
    String[] predicted=new String[]{f,f,f,f,f,f,f,f,f,f};
    for(String poss:c.keySet())
    {
      Double po=t.get(f+" "+poss)==null?0:t.get(f+" "+poss)*c.get(poss);
      if(po>0)
      {
        for(int i=0;i<10;i++)
        {
          if(po>max[i])
          {
            double o=max[i];
            max[i]=po;
            for(int j=9;j>i+1;j--)
              max[j]=max[j-1];
            if(i!=9)
              max[i+1]=o;
            
            String temp=predicted[i];
            predicted[i]=poss;
            for(int j=9;j>i+1;j--)
              predicted[j]=predicted[j-1];
            if(i!=9)
              predicted[i+1]=temp;
            break;
          }
        }
      }
    }
    return predicted;
  }
  
// public String[] predictNext(String word) //changes to make: 3 best words
//  {
//    word=word.toLowerCase();
//    double[] max=new double[]{0,0,0};
//    String[] predicted=new String[]{"~","~","~"};
//    for(String poss:c.keySet())
//    {
//      if((bc.get(word+" "+poss)==null?0:bc.get(word+" "+poss))>max[0])
//      {
//        max[0]=bc.get(word+" "+poss);
//        predicted[0]=poss;
//      }
//      else if((bc.get(word+" "+poss)==null?0:bc.get(word+" "+poss))>max[1])
//      {
//        max[1]=bc.get(word+" "+poss);
//        predicted[1]=poss;
//      }
//      else if((bc.get(word+" "+poss)==null?0:bc.get(word+" "+poss))>max[2])
//      {
//        max[2]=bc.get(word+" "+poss);
//        predicted[2]=poss;
//      }
//    }
//    return predicted;
//  }
 
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