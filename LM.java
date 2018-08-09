import java.util.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

public class LM //things to do: maybe add the smoothing coefficients thing, fix the viterbi alignments visualization,
{//NOTE TO SELF: DO NOT COMPILE WHEN THE COUNTS ARE BEING SERIALIZED. THIS WILL CORRUPT THE FILE
  private static HashMap<String, Integer>tc;
  private static HashMap<String, Integer>bc;
  private static HashMap<String, Integer>c;
  
  public LM()
  {
    System.out.println("start");
    deserialize();
    System.out.println("finish");
  }
  
  public HashMap<String,Integer> getTC()
  {
    return tc;
  }
  
  public HashMap<String,Integer> getBC()
  {
    return bc;
  }
  
  public HashMap<String,Integer> getC()
  {
    return c;
  }
  
  public double lm(String sentence) //is it good english? string e must be uncapitalized and lacking punctuation
  {
    double log=0;
    String[]w=wordArrayNN(sentence);
    for(int i=0;i<w.length-2;i++)
    {
      double lb=Math.log(b(w[i]+" "+w[i+1]+" "+w[i+2]));
      log+=lb;
    }
    return Math.pow(Math.E, log/w.length);
  }
  
  public String[] predictNext(String prev, String word) //changes to make: 3 best words
  {
    prev=prev.toLowerCase();
    word=word.toLowerCase();
    double[] max=new double[]{0,0,0};
    String[] predicted=new String[]{"~","~","~"};
    int x=0;
    for(String poss:c.keySet())
    {
      Integer ct=tc.get(prev+" "+word+" "+poss);
      if(ct!=null)
      {
        for(int i=0;i<3;i++)
        {
          if(ct>max[i])
          {
            double o=max[i];
            max[i]=ct;
            for(int j=2;j>i+1;j--)
              max[j]=max[j-1];
            if(i!=2)
              max[i+1]=o;
            
            String temp=predicted[i];
            predicted[i]=poss;
            for(int j=2;j>i+1;j--)
              predicted[j]=predicted[j-1];
            if(i!=2)
              predicted[i+1]=temp;
            break;
          }
        }
      }
    }
    if(predicted[0].equals("~")) //never seen any trigrams with first two words before
      return predictNext(word);
    if(predicted[1].equals("~"))
    {
      String[]p=predictNext(word);
      predicted[1]=p[0];
      predicted[2]=p[1];
      return predicted;
    }
    if(predicted[2].equals("~"))
    {
      String[]p=predictNext(word);
      predicted[2]=p[0];
      return predicted;
    }
    return predicted;
  }
  
  public String[] predictNext(String word) //changes to make: 3 best words
  {
    word=word.toLowerCase();
    double[] max=new double[]{0,0,0};
    String[] predicted=new String[]{"~","~","~"};
    int x=0;
    for(String poss:c.keySet())
    {
      if(bc.get(word+" "+poss)!=null) 
      {
        for(int i=0;i<3;i++)
        {
          Integer cb=bc.get(word+" "+poss);
          if(cb>max[i])
          {
            double o=max[i];
            max[i]=cb;
            for(int j=2;j>i+1;j--)
              max[j]=max[j-1];
            if(i!=2)
              max[i+1]=o;
            
            String temp=predicted[i];
            predicted[i]=poss;
            for(int j=2;j>i+1;j--)
              predicted[j]=predicted[j-1];
            if(i!=2)
              predicted[i+1]=temp;
            break;
          }
        }
      }
    }
    return predicted;
  }
  
  /*public String[] best(String f)
  {
    double[]max=new double[]{0,0,0,0,0,0,0,0,0,0};
    String[] predicted=new String[]{f,f,f,f,f,f,f,f,f,f};
    for(String poss:c.keySet())
    {
      if(invt.get(poss+" "+f)!=null)
      {
        for(int i=0;i<10;i++)
        {
          if(invt.get(poss+" "+f)>max[i])
          {
            double o=max[i];
            max[i]=invt.get(poss+" "+f);
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
  }*/
  
  //write method to predictNext given two words
  
  public String[] predictWord(String prev, String fragment)//give 3 options;
  {
    prev=prev.toLowerCase();
    fragment=fragment.toLowerCase();
    double[] max=new double[]{0,0,0};
    String[] predicted=new String[]{"~","~","~"};
    for(String poss:c.keySet())
    {
      if(fragment.length()<=poss.length()&&poss.substring(0, fragment.length()).equals(fragment.toLowerCase()))
      {
        Integer cb=bc.get(prev+" "+poss);
        if(cb!=null)
        {
          for(int i=0;i<3;i++)
          {
            if(cb>max[i])
            {
              double o=max[i];
              max[i]=cb;
              for(int j=2;j>i+1;j--)
                max[j]=max[j-1];
              if(i!=2)
                max[i+1]=o;
              
              String temp=predicted[i];
              predicted[i]=poss;
              for(int j=2;j>i+1;j--)
                predicted[j]=predicted[j-1];
              if(i!=2)
                predicted[i+1]=temp;
              break;
            }
          }
        }
      }
    }
    if(predicted[0].equals("~")) //never seen any trigrams with first two words before
      return predictWord(fragment);
    if(predicted[1].equals("~"))
    {
      String[]p=predictWord(fragment);
      predicted[1]=p[0];
      predicted[2]=p[1];
      return predicted;
    }
    if(predicted[2].equals("~"))
    {
      String[]p=predictWord(fragment);
      predicted[2]=p[0];
      return predicted;
    }
    return predicted;
  }
  
  public String[] predictWord(String fragment)//give 3 options;
  {
    fragment=fragment.toLowerCase();
    double[] max=new double[]{0,0,0};
    String[] predicted=new String[]{"~","~","~"};
    for(String poss:c.keySet())
    {
      if(fragment.length()<=poss.length()&&poss.substring(0, fragment.length()).equals(fragment.toLowerCase()))
      {
        for(int i=0;i<2;i++)
        {
          if(c.get(poss)>max[i])
          {
            double o=max[i];
            max[i]=c.get(poss);
            for(int j=2;j>i+1;j--)
              max[j]=max[j-1];
            if(i!=2)
              max[i+1]=o;
            
            String temp=predicted[i];
            predicted[i]=poss;
            for(int j=2;j>i+1;j--)
              predicted[j]=predicted[j-1];
            if(i!=2)
              predicted[i+1]=temp;
            break;
          }
        }
      }
    }
    return predicted;
  }
  
  //write method to predict word given a word
  
  public double b(String trigram)
  {
    String[]wa=wordArray(trigram);
    return 0.95*((double)(tc.get(trigram)==null?0:tc.get(trigram))/(bc.get(wa[0]+" "+wa[1])==null?1:bc.get(wa[0]+" "+wa[1])))+0.04*((double)(bc.get(wa[1]+" "+wa[2])==null?0:bc.get(wa[1]+" "+wa[2]))/(c.get(wa[2])==null?1:c.get(wa[2])))+0.008*((double)(c.get(wa[2])==null?0:c.get(wa[2]))/(tc.get("XXX")==null?1:tc.get("XXX")))+0.002;
  } 
  
  public void tCounts(String file) throws IOException, FileNotFoundException
  {
    int count=0;
    tc=new HashMap<String, Integer>();
    bc=new HashMap<String, Integer>();
    c=new HashMap<String, Integer>();
//    deserialize();
    System.out.println("here");
    BufferedReader br = new BufferedReader(new FileReader(file));
    String line;
    int d=0;
    while ((line = br.readLine()) != null) 
    {
      String[]words=wordArrayNN(line);
      String b=words[0]+" "+words[1];
      bc.put(b, bc.get(b)==null?1:bc.get(b)+1);
      for(int i=0;i<words.length-2;i++)
      {
        String trigram=words[i]+" "+words[i+1]+" "+words[i+2];
        String bigram=words[i+1]+" "+words[i+2];
        String word=words[i+2];
        tc.put(trigram, tc.get(trigram)==null?1:tc.get(trigram)+1);
        bc.put(bigram, bc.get(bigram)==null?1:bc.get(bigram)+1);
        c.put(word, c.get(word)==null?1:c.get(word)+1);
        count++;
      }
      d++;
      if(d%10000==0)
        System.out.println("line "+d);
    }
    System.out.println("out");
    tc.put("XXX",tc.get("XXX")==null?count:tc.get("XXX")+count);
    try {
      if(br != null)
        br.close();
    } catch (IOException e) {
      //
    }
    serialize();
  }
  
  public static void test()throws IOException, FileNotFoundException
  {
    File folder=new File("./OANC-GrAF/data/written_1/journal/slate");
    int d=0;
    for(File file:folder.listFiles())
    {
      System.out.println("this: "+file);
      for(File f:file.listFiles())
        if(f.getName().contains(".txt"))
      {
        System.out.println("./OANC-GrAF/data/written_1/journal/slate/"+file.getName()+"/"+f.getName());
        BufferedReader br = new BufferedReader(new FileReader(f));
      }
    }
  }
  
  public void train() throws IOException, FileNotFoundException
  {
//    File folder=new File("./OANC-GrAF/data/written_1/journal/slate");
//    for(File file:folder.listFiles())
//    {
//      if(file.isDirectory())
//      {
//        for(File f:file.listFiles())
//        {
//          if(f.getName().contains(".txt"))
//          {
//            lowercase("./OANC-GrAF/data/written_1/journal/slate/"+file.getName()+"/"+f.getName(),"./OANC-GrAF/data/written_1/journal/slate/"+file.getName()+"/"+"tmp_"+f.getName());
//            tCounts2("./OANC-GrAF/data/written_1/journal/slate/"+file.getName()+"/"+f.getName());
//          }
//        }
//      }
//    }
//    System.out.println("got here");
//    folder=new File("./OANC-GrAF/data/written_1/journal/verbatim");
//    for(File file:folder.listFiles())
//    {
//      if(file.getName().contains(".txt"))
//      {
//        lowercase("./OANC-GrAF/data/written_1/journal/verbatim/"+file.getName(),"./OANC-GrAF/data/written_1/journal/verbatim/"+"tmp_"+file.getName());
//        tCounts2("./OANC-GrAF/data/written_1/journal/verbatim/"+file.getName());
//      }
//    }
//    System.out.println("got here2");
//    folder=new File("./OANC-GrAF/data/written_1/letters/icic");
//    for(File file:folder.listFiles())
//    {
//      if(file.getName().contains(".txt"))
//      {
//        lowercase("./OANC-GrAF/data/written_1/letters/icic/"+file.getName(),"./OANC-GrAF/data/written_1/letters/icic/"+"tmp_"+file.getName());
//        tCounts2("./OANC-GrAF/data/written_1/letters/icic/"+file.getName());
//      }
//    }
//    lowercase("./OANC-GrAF/data/written_1/fiction/eggan/TheStory.txt","./OANC-GrAF/data/written_1/fiction/eggan/tmp_TheStory.txt");
//    tCounts2("./OANC-GrAF/data/written_1/fiction/eggan/TheStory.txt");
//    System.out.println("got here3");
//    serialize();
    String fileName="./OANC-GrAF/data/written_2/non-fiction/OUP";
    File folder=new File(fileName);
    for(File file:folder.listFiles())
    {
      if(file.isDirectory())
      {
        for(File f:file.listFiles())
        {
          if(f.getName().contains(".txt"))
          {
            tCounts2(fileName+"/"+file.getName()+"/"+f.getName());
          }
        }
      }
    }
    System.out.println("got here4");
    fileName="./OANC-GrAF/data/written_2/technical";
    folder=new File(fileName);
    for(File file:folder.listFiles())
    {
      if(file.isDirectory())
      {
        for(File f:file.listFiles())
        {
          if(f.isDirectory())
          {
            for(File g:f.listFiles())
            {
              if(g.getName().contains(".txt"))
              {
                lowercase(fileName+"/"+file.getName()+"/"+f.getName()+"/"+g.getName(),fileName+"/"+file.getName()+"/"+"/"+f.getName()+"tmp_"+g.getName());
                tCounts2(fileName+"/"+file.getName()+"/"+f.getName()+"/"+g.getName());
              }
            }
            if(f.getName().contains(".txt"))
            {
              //lowercase(fileName+"/"+file.getName()+"/"+f.getName(),fileName+"/"+file.getName()+"/"+"tmp_"+f.getName());
              tCounts2(fileName+"/"+file.getName()+"/"+f.getName());
            }
          }
        }
      }
    }
    System.out.println("got here5");
    fileName="./OANC-GrAF/data/written_2/travel_guides";
      folder=new File(fileName);
      for(File file:folder.listFiles())
      {
        if(file.isDirectory())
        {
          for(File f:file.listFiles())
          {
            if(f.getName().contains(".txt"))
            {
              lowercase(fileName+"/"+file.getName()+"/"+f.getName(),fileName+"/"+file.getName()+"/"+"tmp_"+f.getName());
              tCounts2(fileName+"/"+file.getName()+"/"+f.getName());
            }
          }
        }
      }
      serialize();
    }
    
    public void lowercase(String file, String tmp) throws IOException, FileNotFoundException
    {
      String oldFileName = file;
      String tmpFileName = tmp;
      
      BufferedReader br = null;
      BufferedWriter bw = null;
      br = new BufferedReader(new FileReader(oldFileName));
      bw = new BufferedWriter(new FileWriter(tmpFileName));
      String line;
      while ((line = br.readLine()) != null) {
        String newLine="";
        for(int i=0;i<line.length();i++)
          newLine+=Character.isUpperCase(line.charAt(i))?Character.toLowerCase(line.charAt(i)):line.charAt(i);
        bw.write(newLine+"\n");
      }
      try {
        if(br != null)
          br.close();
      } catch (IOException e) {}
      try {
        if(bw != null)
          bw.close();
      } catch (IOException e) {}
      // Once everything is complete, delete old file..
      File oldFile = new File(oldFileName);
      oldFile.delete();
      
      // And rename tmp file's name to old file name
      File newFile = new File(tmpFileName);
      newFile.renameTo(oldFile);
      
    }
    
    public void tCounts2(String file) throws IOException, FileNotFoundException //use for non-aligned corpora
    {
      int count=0;
//    tc=new HashMap<String, Integer>();
//    bc=new HashMap<String, Integer>();
//    c=new HashMap<String, Integer>();
//    deserialize();
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;
      //int d=0;
      String sent="";
      while ((line = br.readLine()) != null) 
      {
        for(char d:line.toCharArray())
        {
          if(d=='.')
          {
            String[]words=wordArrayNN(sent);
            String b=words[0]+" "+words[1];
            bc.put(b, bc.get(b)==null?1:bc.get(b)+1);
            for(int i=0;i<words.length-2;i++)
            {
              String trigram=words[i]+" "+words[i+1]+" "+words[i+2];
              String bigram=words[i+1]+" "+words[i+2];
              String word=words[i+2];
              tc.put(trigram, tc.get(trigram)==null?1:tc.get(trigram)+1);
              bc.put(bigram, bc.get(bigram)==null?1:bc.get(bigram)+1);
              c.put(word, c.get(word)==null?1:c.get(word)+1);
              count++;
            }
            sent="";
          }
          else if(!(d==','||d=='('||d==')'||d=='\"'||d==':'))
            sent+=d;
        }
//      d++;
//      if(d%10000==0)
//        System.out.println("line "+d);
      }
      System.out.println("out");
      tc.put("XXX",tc.get("XXX")==null?count:tc.get("XXX")+count);
      try {
        if(br != null)
          br.close();
      } catch (IOException e) {
        //
      }
    }
    
    public String[] wordArrayNN(String sentence)
    {
      ArrayList<Integer>spaces=new ArrayList<Integer>();
      for(int i=0;i<sentence.length();i++)
      {
        if(sentence.substring(i,i+1).equals(" "))
          spaces.add(i);
      }
      String[]words=new String[spaces.size()+3];
      words[0]="NULL";
      words[1]="NULL";
      if(spaces.size()>0)
      {
        words[2]=sentence.substring(0,spaces.get(0));
        for(int i=0;i<spaces.size()-1;i++)
          words[i+3]=sentence.substring(spaces.get(i)+1,spaces.get(i+1));
        words[words.length-1]=sentence.substring(spaces.get(spaces.size()-1)+1);
        return words;
      }
      return new String[]{"NULL", "NULL",sentence};
    }
    
    public String[]wordArray(String sentence)
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
    
    public void deserialize()
    {
      try
      {
        FileInputStream fileIn = new FileInputStream("tc.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        tc = (HashMap<String, Integer>) in.readObject();
        System.out.println("tc is deserialized");
        in.close();
        fileIn.close();
      }catch(IOException i)
      {
        System.out.println("fail");
        i.printStackTrace();
      }catch(ClassNotFoundException c)
      {
        System.out.println("class not found");
        c.printStackTrace();
      }
      try
      {
        FileInputStream fileIn = new FileInputStream("bc.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        bc = (HashMap<String, Integer>) in.readObject();
        System.out.println("bc is deserialized");
        in.close();
        fileIn.close();
      }catch(IOException i)
      {
        System.out.println("fail");
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
        System.out.println("c is deserialized");
        in.close();
        fileIn.close();
      }catch(IOException i)
      {
        System.out.println("fail");
        i.printStackTrace();
      }catch(ClassNotFoundException c)
      {
        System.out.println("class not found");
        c.printStackTrace();
      }
    }
    
    public void serialize()
    {
      System.out.println("WARNING: do not compile until c.ser is saved.");
      try
      {
        FileOutputStream fileOut =
          new FileOutputStream("tc.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(tc);
        out.close();
        fileOut.close();
        System.out.printf("data is saved in tc.ser");
      }catch(IOException i)
      {
        i.printStackTrace();
      }
      try
      {
        FileOutputStream fileOut =
          new FileOutputStream("bc.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(bc);
        out.close();
        fileOut.close();
        System.out.printf("data is saved in bc.ser");
      }catch(IOException i)
      {
        i.printStackTrace();
      }
      try
      {
        FileOutputStream fileOut =
          new FileOutputStream("c.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(c);
        out.close();
        fileOut.close();
        System.out.printf("data is saved in c.ser");
      }catch(IOException i)
      {
        i.printStackTrace();
      }
    }
    
    public int count(String file, String word) 
    {
      tc=new HashMap<String, Integer>();
      int nc=0;
      BufferedReader br = null;
      try {
        br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
          nc+=line.split(word, -1).length-1;
        }
      } catch (Exception e) {
        System.out.println("fail");
        return -1;
      } finally {
        try {
          if(br != null)
            br.close();
        } catch (IOException e) {
          //
        }
      }
      return nc;
    }
  }
