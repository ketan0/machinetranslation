import java.util.*;
import java.io.*;

public class SMT3
{
  HashMap<String, Double> t; 
  HashMap<String, Double> q; 
  
  public SMT3()
  {
    ds();
    //deserialize();
  }
  
  public void ds()
  {
    try
    {
      FileInputStream fileIn = new FileInputStream("st.ser");
      ObjectInputStream in = new ObjectInputStream(fileIn);
      t = (HashMap<String, Double>) in.readObject();
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
      FileInputStream fileIn = new FileInputStream("sq.ser");
      ObjectInputStream in = new ObjectInputStream(fileIn);
      q = (HashMap<String, Double>) in.readObject();
      in.close();
      fileIn.close();
    }catch(IOException i)
    {
      System.out.println("failed to deserialize q");
      i.printStackTrace();
    }catch(ClassNotFoundException c)
    {
      System.out.println("class not found");
      c.printStackTrace();
    }
  }
  
  public void deserialize()
  {
    try
    {
      FileInputStream fileIn = new FileInputStream("t.ser");
      ObjectInputStream in = new ObjectInputStream(fileIn);
      t = (HashMap<String, Double>) in.readObject();
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
      FileInputStream fileIn = new FileInputStream("q.ser");
      ObjectInputStream in = new ObjectInputStream(fileIn);
      q = (HashMap<String, Double>) in.readObject();
      in.close();
      fileIn.close();
    }catch(IOException i)
    {
      System.out.println("failed to deserialize q");
      i.printStackTrace();
    }catch(ClassNotFoundException c)
    {
      System.out.println("class not found");
      c.printStackTrace();
    }
  }
  
  public void serialize() throws IOException, FileNotFoundException
  {
    ArrayList<String[]>frs=new ArrayList<String[]>();
    ArrayList<String[]>ens=new ArrayList<String[]>();
    BufferedReader enBr = new BufferedReader(new FileReader("europarl-v7.de-en.en"));
    BufferedReader frBr = new BufferedReader(new FileReader("europarl-v7.de-en.de"));
    while(true)
    {
      String eline=enBr.readLine();
      String fline=frBr.readLine();
      if(eline==null||fline==null)
        break;
      String[]words=wordArray(eline);
      String[]words2=wordArray(fline);
      frs.add(words2);
      ens.add(words);
    }
    try
    {
      FileOutputStream fileOut =
        new FileOutputStream("ens.ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(ens);
      out.close();
      fileOut.close();
      System.out.printf("data is saved in ens.ser");
    }catch(IOException i)
    {
      i.printStackTrace();
    }
    try
    {
      FileOutputStream fileOut =
        new FileOutputStream("frs.ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(frs);
      out.close();
      fileOut.close();
      System.out.printf("data is saved in frs.ser");
    }catch(IOException i)
    {
      i.printStackTrace();
    }
  }
  
  public HashMap<String,Double> getT()
  {
    return t;
  }
  
  public HashMap<String,Double> getQ()
  {
    return q;
  }
  
  public void replace(String file) 
  {
      String oldFileName = file;
      String tmpFileName = "tmp_"+file;

      BufferedReader br = null;
      BufferedWriter bw = null;
      try {
        br = new BufferedReader(new FileReader(oldFileName));
        bw = new BufferedWriter(new FileWriter(tmpFileName));
        String line;
        while ((line = br.readLine()) != null) {
          String newLine="";
          for(int i=0;i<line.length();i++)
            newLine+=Character.isUpperCase(line.charAt(i))?Character.toLowerCase(line.charAt(i)):line.charAt(i);
          bw.write(newLine+"\n");
        }
      } catch (Exception e) {
        System.out.println("fail");
        return;
      } finally {
         try {
            if(br != null)
               br.close();
         } catch (IOException e) {}
         try {
            if(bw != null)
               bw.close();
         } catch (IOException e) {}
      }
      // Once everything is complete, delete old file..
      File oldFile = new File(oldFileName);
      oldFile.delete();

      // And rename tmp file's name to old file name
      File newFile = new File(tmpFileName);
      newFile.renameTo(oldFile);

  }
  public double tm(String ee, String ff, int it) throws IOException, FileNotFoundException//P(f|e)
  {
//    q=new HashMap<String, Double>();
//    t=new HashMap<String, Double>();
    String[]english=new String[]{
      "the book",
        "a house",
        "the big book",
        "a small book",
        "the big house",
        "a small house"};
   
    String[]french=new String[]{
      "das buch",
        "ein haus",
        "das grosse buch",
        "ein kleines buch",
        "das grosse haus",
        "ein kleines haus"};
    ArrayList<String[]>frs=new ArrayList<String[]>();
    ArrayList<String[]>ens=new ArrayList<String[]>();
    HashMap<String, Double>m=new HashMap<String, Double>();
    //t=new HashMap<String, Double>();
    //q=new HashMap<String, Double>();
    BufferedReader enBr = new BufferedReader(new FileReader("europarl-v7.de-en.de"));
    BufferedReader frBr = new BufferedReader(new FileReader("europarl-v7.de-en.en"));
//      try
//      {
//         FileInputStream fileIn = new FileInputStream("t.ser");
//         ObjectInputStream in = new ObjectInputStream(fileIn);
//         t = (HashMap<String, Double>) in.readObject();
//         in.close();
//         fileIn.close();
//      }catch(IOException i)
//      {
//        System.out.println("fail");
//        i.printStackTrace();
//      }catch(ClassNotFoundException c)
//      {
//        System.out.println("class not found");
//        while(true)
//        {
//          String eline=enBr.readLine();
//          String fline=frBr.readLine();
//          if(eline==null||fline==null)
//            break;
//          for(String e: wordArray(eline))
//          {
//            for(String f: wordArray(fline))
//            {
//              m.put(e,m.get(e)==null?1:m.get(e)+1);
//              t.put(e+" "+f,1/m.get(e));
//            }
//          }
//        }
//        c.printStackTrace();
//      }
    if(it>0)
    {
//    int n=0;
//    while(n<300000)//skip over these lines
//       {
//         String eline=enBr.readLine();
//         String fline=frBr.readLine();
//         if(eline==null||fline==null)
//           break;
//         n++;
//       }
//       System.out.print("OK");
       int n=0;
       while(n<10000)
       {
         String eline=enBr.readLine();
         String fline=frBr.readLine();
         if(eline==null||fline==null)
           break;
         n++;
         if(n%100==0)
           System.out.println(n);
         String[]words=wordArray(eline);
         String[]words2=wordArray(fline);
         frs.add(words2);
         ens.add(words);
         for(int i=0;i<words.length;i++)//english
         {
           for(int j=0;j<words2.length;j++)//french
           {
             String e=words[i];
             String f=words2[j];
             if(t.get(e+" "+f)==null)
             {
               //System.out.println("t("+e+" "+f+") is null");
               m.put(e,m.get(e)==null?1:m.get(e)+1);
               t.put(e+" "+f,1/m.get(e));
             }
             if(q.get(j+" "+i)==null)
             {
               //System.out.println("q("+j+" "+i+") is null");
               q.put(j+" "+i,1.0/1000);
             }
           }
         }
       }
      }
       HashMap<String, Double>count=new HashMap<String, Double>();
       HashMap<String, Double>total=new HashMap<String, Double>();
       HashMap<String, Double>ts=new HashMap<String, Double>();
       HashMap<String, Double>qcount=new HashMap<String, Double>();
       HashMap<String, Double>qtot=new HashMap<String, Double>();
       for(int h=0;h<it;h++)
       {
         count=new HashMap<String, Double>();
         total=new HashMap<String, Double>();
         System.out.println("k"+h);
         int x=0;
         for(int g=0;g<ens.size();g++) //for each sentence pair
         {
           x++;
           if(x%100==0&&x>5000)
             System.out.println(x);
           String[]engsent=ens.get(g);
           String[]frenchsent=frs.get(g);
           for(int a=0;a<engsent.length;a++)//every word combination
           {
             String en=engsent[a];
             for(int b=0;b<frenchsent.length;b++)
             {
               String fr=frenchsent[b];
//            System.out.println("t: "+en+" "+fr+": "+ t.get(en+" "+fr));
//           System.out.println("t.get("+en+" "+fr+"): "+t.get(en+" "+fr));
//           System.out.println("ts.get("+en+" "+fr+"): "+ts.get(en));
//            System.out.println("q: "+q.get(b+" "+a));
//            System.out.println("t: "+t.get(en+" "+fr));
//            System.out.println(q.get(b+" "+a)*t.get(en+" "+fr));
               ts.put(en,ts.get(en)==null?q.get(b+" "+a)*t.get(en+" "+fr): ts.get(en)+q.get(b+" "+a)*t.get(en+" "+fr));//ts is sum t(f|e)
             }
           }
           for(int a=0;a<engsent.length;a++)//every word combination
           {
             for(int b=0;b<frenchsent.length;b++)
             {
               String en=engsent[a];
               String fr=frenchsent[b];
               double tef=q.get(b+" "+a)*t.get(en+" "+fr);
               count.put(en+" "+fr,count.get(en+" "+fr)==null?tef/ts.get(en):count.get(en+" "+fr)+tef/ts.get(en));//c(e,f)+=t(f|e)/sum(t(f|e))
               qcount.put(b+" "+a, qcount.get(b+" "+a)==null?tef/ts.get(en):qcount.get(b+" "+a)+tef/ts.get(en));
               qtot.put(""+a, qtot.get(""+a)==null?tef/ts.get(en):qtot.get(""+a)+tef/ts.get(en));
//            System.out.println("qc: "+qcount.get(b+" "+a));
//            System.out.println("qt: "+qtot.get(""+a));
               total.put(fr,total.get(fr)==null?tef/ts.get(en):total.get(fr)+tef/ts.get(en)); //c(f)+=t(f|e)/sum(t(f|e)
             }
           }
         }
         System.out.println("j"+h);
         for(String ef:t.keySet())
         {
           String f=ef.substring(ef.indexOf(" ")+1);
           if(count.get(ef)!=null&&total.get(f)!=null)
             t.put(ef, (double)(count.get(ef))/total.get(f));
         }
      
      for(String w:q.keySet())
      {
        String kk=w.substring(w.indexOf(" ")+1);
        if(qcount.get(w)!=null&&qtot.get(kk)!=null)
          q.put(w, (double)(qcount.get(w))/qtot.get(kk));
      }
    }
    if(it>0)
    {
      try
      {
        FileOutputStream fileOut =
          new FileOutputStream("st.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(t);
        out.close();
        fileOut.close();
        System.out.printf("data is saved in st.ser");
      }catch(IOException i)
      {
        i.printStackTrace();
      }
    try
      {
        FileOutputStream fileOut =
          new FileOutputStream("sq.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(q);
        out.close();
        fileOut.close();
        System.out.printf("data is saved in sq.ser");
      }catch(IOException i)
      {
        i.printStackTrace();
      }
    }
    String[]fs=wordArray(ff);
    String[]es=wordArray(ee);
    double result=1.0;
    for(int i=0;i<es.length;i++)
    {
      double subresult=0;
      for(int j=0;j<fs.length;j++)
      {   
        if(t.get(es[i]+" "+fs[j])==null||q.get(j+" "+i)==null)
          continue;
        System.out.println("t: "+es[i]+" "+fs[j]+": "+ t.get(es[i]+" "+fs[j]));
        System.out.println("q: "+j+" "+i+": "+ q.get(j+" "+i));
        subresult+=t.get(es[i]+" "+fs[j])*q.get(j+" "+i);
        System.out.println("subresult: "+subresult);
      }
      result*=subresult;
    }
    return result;
  }
  
   public int[] viterbi(String ee, String ff, int it) throws IOException, FileNotFoundException//P(f|e)
  {//change viterbi later
   //    q=new HashMap<String, Double>();
//    t=new HashMap<String, Double>();
    String[]english=new String[]{
      "the book",
        "a house",
        "the big book",
        "a small book",
        "the big house",
        "a small house"};
    String[]french=new String[]{
      "das buch",
        "ein haus",
        "das grosse buch",
        "ein kleines buch",
        "das grosse haus",
        "ein kleines haus"};
    ArrayList<String[]>frs=new ArrayList<String[]>();
    ArrayList<String[]>ens=new ArrayList<String[]>();
    HashMap<String, Double>m=new HashMap<String, Double>();
    BufferedReader enBr=null;
    BufferedReader frBr=null;
    if(it>0)
    {
      enBr = new BufferedReader(new FileReader("europarl-v7.de-en.de"));
      frBr = new BufferedReader(new FileReader("eeuroparl-v7.de-en.en"));
    }
//      try
//      {
//         FileInputStream fileIn = new FileInputStream("t.ser");
//         ObjectInputStream in = new ObjectInputStream(fileIn);
//         t = (HashMap<String, Double>) in.readObject();
//         in.close();
//         fileIn.close();
//      }catch(IOException i)
//      {
//        System.out.println("fail");
//        i.printStackTrace();
//      }catch(ClassNotFoundException c)
//      {
//        System.out.println("class not found");
//        while(true)
//        {
//          String eline=enBr.readLine();
//          String fline=frBr.readLine();
//          if(eline==null||fline==null)
//            break;
//          for(String e: wordArray(eline))
//          {
//            for(String f: wordArray(fline))
//            {
//              m.put(e,m.get(e)==null?1:m.get(e)+1);
//              t.put(e+" "+f,1/m.get(e));
//            }
//          }
//        }
//        c.printStackTrace();
//      }
    if(it>0)
    {
    int n=0;
    while(n<0)//skip over these lines
       {
         String eline=enBr.readLine();
         String fline=frBr.readLine();
         if(eline==null||fline==null)
           break;
         n++;
       }
       System.out.print("OK");
       while(n<2000)
       {
         String eline=enBr.readLine();
         String fline=frBr.readLine();
         if(eline==null||fline==null)
           break;
         n++;
         String[]words=wordArray(eline);
         String[]words2=wordArray(fline);
         frs.add(words2);
         ens.add(words);
         for(int i=0;i<words.length;i++)//english
         {
           for(int j=0;j<words2.length;j++)//french
           {
             String e=words[i];
             String f=words2[j];
             if(t.get(e+" "+f)==null)
             {
               System.out.println("t("+e+" "+f+") is null");
               t.put(e+" "+f,1.0/1000);
             }
             if(q.get(j+" "+i)==null)
             {
               System.out.println("q("+j+" "+i+") is null");
               q.put(j+" "+i,1.0/1000);
             }
           }
         }
         n++;
         if(n%100==0)
           System.out.println(n);
       }
    }
       
    HashMap<String, Double>count=new HashMap<String, Double>();
    HashMap<String, Double>total=new HashMap<String, Double>();
    HashMap<String, Double>ts=new HashMap<String, Double>();
    HashMap<String, Double>qcount=new HashMap<String, Double>();
    HashMap<String, Double>qtot=new HashMap<String, Double>();
    for(int h=0;h<it;h++)
    {
      int x=0;
      for(int i=0;i<ens.size();i++)
      {
        for(int k=0;k<frs.get(i).length;k++)
        {
          total.put(frs.get(i)[k], 0.0);
          for(int j=0;j<ens.get(i).length;j++)
            count.put(ens.get(i)[j]+" "+frs.get(i)[k], 0.0);
        }
      }
      System.out.println("k"+h);
      for(int g=0;g<ens.size();g++) //for each sentence pair
      {
        x++;
        if(x%100==0)
          System.out.println(x);
        for(int i=0;i<ens.size();i++)
          for(int j=0;j<ens.get(i).length;j++)
          for(int k=0;k<frs.get(i).length;k++)
          ts.put(ens.get(i)[j],0.0);
        String[]engsent=ens.get(g);
        String[]frenchsent=frs.get(g);
        for(int a=0;a<engsent.length;a++)//every word combination
        {
          String en=engsent[a];
          for(int b=0;b<frenchsent.length;b++)
          {
            String fr=frenchsent[b];
//            System.out.println("t: "+en+" "+fr+": "+ t.get(en+" "+fr));
//           System.out.println("t.get("+en+" "+fr+"): "+t.get(en+" "+fr));
//           System.out.println("ts.get("+en+" "+fr+"): "+ts.get(en));
//            System.out.println("q: "+q.get(b+" "+a));
//            System.out.println("t: "+t.get(en+" "+fr));
//            System.out.println(q.get(b+" "+a)*t.get(en+" "+fr));
            ts.put(en,ts.get(en)+q.get(b+" "+a)*t.get(en+" "+fr));//ts is sum t(f|e)
          }
        }
        for(int a=0;a<engsent.length;a++)//every word combination
        {
          for(int b=0;b<frenchsent.length;b++)
          {
            String en=engsent[a];
            String fr=frenchsent[b];
            double tef=q.get(b+" "+a)*t.get(en+" "+fr);
            count.put(en+" "+fr,count.get(en+" "+fr)+tef/ts.get(en));//c(e,f)+=t(f|e)/sum(t(f|e))
            qcount.put(b+" "+a, qcount.get(b+" "+a)==null?tef/ts.get(en):qcount.get(b+" "+a)+tef/ts.get(en));
            qtot.put(""+a, qtot.get(""+a)==null?tef/ts.get(en):qtot.get(""+a)+tef/ts.get(en));
//            System.out.println("qc: "+qcount.get(b+" "+a));
//            System.out.println("qt: "+qtot.get(""+a));
            total.put(fr,total.get(fr)+tef/ts.get(en)); //c(f)+=t(f|e)/sum(t(f|e)
          }
        }
      }
      System.out.println("j"+h);
      for(int i=0;i<ens.size();i++)
        for(int j=0;j<ens.get(i).length;j++)
        for(int k=0;k<frs.get(i).length;k++)
        t.put(ens.get(i)[j]+" "+frs.get(i)[k], count.get(ens.get(i)[j]+" "+frs.get(i)[k])/total.get(frs.get(i)[k]));//still good
      for(String w:q.keySet())
      {
        String kk=w.substring(w.indexOf(" ")+1);
        q.put(w, (double)(qcount.get(w))/qtot.get(kk));
      }
    }
    if(it>0)
    {
      try
      {
        FileOutputStream fileOut =
          new FileOutputStream("t.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(t);
        out.close();
        fileOut.close();
        System.out.printf("data is saved in t.ser");
      }catch(IOException i)
      {
        i.printStackTrace();
      }
    try
      {
        FileOutputStream fileOut =
          new FileOutputStream("q.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(q);
        out.close();
        fileOut.close();
        System.out.printf("data is saved in q.ser");
      }catch(IOException i)
      {
        i.printStackTrace();
      }
    }
    String[]fs=wordArray(ff);
    String[]es=wordArray(ee);
    int[]a=new int[es.length];
    for(int j=0;j<es.length;j++)
    {
      int maxI=-1;
      for(int i=0;i<fs.length;i++)
      {
        if(t.get(es[j]+" "+fs[i])==null)
          continue;
        if(maxI==-1||t.get(es[j]+" "+fs[i])*q.get(j+" "+i)>t.get(es[j]+" "+fs[maxI])*q.get(maxI+" "+i))
          maxI=i;
      }
      a[j]=maxI;
    }
    return a;
  }
   
//   String[]fs=wordArray(ff);
//    String[]es=wordArray(ee);
//    int[]a=new int[es.length];
  
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
  
  public String[] wordArrayN(String sentence)
  {
    ArrayList<Integer>spaces=new ArrayList<Integer>();
    for(int i=0;i<sentence.length();i++)
    {
      if(sentence.substring(i,i+1).equals(" "))
        spaces.add(i);
    }
    String[]words=new String[spaces.size()+2];
    words[0]="NULL";
    if(spaces.size()>0)
    {
      words[1]=sentence.substring(0,spaces.get(0));
      for(int i=0;i<spaces.size()-1;i++)
        words[i+2]=sentence.substring(spaces.get(i)+1,spaces.get(i+1));
      words[words.length-1]=sentence.substring(spaces.get(spaces.size()-1)+1);
      return words;
    }
    return new String[]{"NULL", sentence};
  }
}

//TreeSet of words
//for all the sentences use estimation-maximization
/* EM ALGORITHM
 * initialize t(e|f) uniformly
 do until convergence
 set count(e|f) to 0 for all e,f
 set total(f) to 0 for all f
 for all sentence pairs (e_s,f_s)
 set total_s(e) = 0 for all e
 for all words e in e_s
 for all words f in f_s
 total_s(e) += t(e|f)
 for all words e in e_s
 for all words f in f_s
 count(e|f) += t(e|f) / total_s(e)
 total(f)   += t(e|f) / total_s(e)
 for all fa
 for all e
 t(e|f) = count(e|f) / total(f)*/

/*toy set:
 the book
 a house
 the big book
 a small book
 the big house
 a small house
 
 das buch
 ein haus
 das grosse buch
 ein kleines buch
 das grosse haus
 ein kleines haus
 
 */

//uniform parameters->EM->good parameters->good alignment probabilities->Good P(f|e).