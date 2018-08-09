import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.geom.AffineTransform;
import java.io.*;

public class SketchPad extends JComponent implements ActionListener, DocumentListener
{
  private JFrame frame;
  private BufferedImage image;
  private JTextField field;
  private JTextField field2;
  private JTextArea area;
  private JButton button;
  private JButton sw1;
  private String word1;
  private JButton sw2;
  private String word2;
  private JButton sw3;
  private String word3;
  private JButton b1;
  private JButton b2;
  private JButton b3;
  private JButton r;
  private JTextField fi;
  int mode;
  LM m;
  ViterbiAlignment v;
  SMT3 smt3;
  Decoder d;
  
  public SketchPad()
  {
    int width = 600;
    int height = 600;
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics g = image.getGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, width, height);
    
    frame = new JFrame();
    frame.setTitle("Statistical Machine Translation");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
    this.setPreferredSize(new Dimension(width, height));
    frame.getContentPane().add(this);
    frame.pack();
    frame.setVisible(true);
    int mode=1;
    m=new LM();
    v=new ViterbiAlignment();
    smt3=new SMT3();
    d=new Decoder(m, smt3);
  }
  
  public void changedUpdate(DocumentEvent e)
  {
    updateButtons();
  }
  
  public void insertUpdate(DocumentEvent e)
  {
    //what need to do: I need to see the document and update the buttons.
    updateButtons();
  }
  
    
  public void removeUpdate(DocumentEvent e)
  {
    updateButtons();
  }
  
  public void updateButtons()
  {
    String content = area.getText();
    //three options: either predict most common word to start sentence, current word if no space, or next word if space
    if(content.length()==0)
    {
      word1=m.predictNext("NULL")[0];
      word2=m.predictNext("NULL")[1];
      word3=m.predictNext("NULL")[2];
      sw1.setText(word1);
      sw1.setActionCommand(word1);
      sw2.setText(word2);
      sw2.setActionCommand(word2);
      sw3.setText(word3);
      sw3.setActionCommand(word3);
      return;
    }
    int count=0;
    int i=-1;
    int w;
    for (w = content.length()-2; w >= 0; w--) 
    {
      if (content.charAt(w)==' ')
      {
        if(i!=-1)
          break;
        i=w;
      }
    }
    if(content.charAt(content.length()-1)==' ')
    {
      String[]u;
      if(i!=-1)
        u=m.predictNext(content.substring(w+1, i), content.substring(i+1,content.length()-1));
      else
        u=m.predictNext(content.substring(w+1,content.length()-1));
      word1=u[0];
      word2=u[1];
      word3=u[2];
      sw1.setText(word1);
      sw1.setActionCommand(word1);
      sw2.setText(word2);
      sw2.setActionCommand(word2);
      sw3.setText(word3);
      sw3.setActionCommand(word3);
    }
    else
    {
      String[]u;
      if(i!=-1)
        u=m.predictWord(content.substring(w+1, i), content.substring(i+1,content.length()));
      else
        u=m.predictWord(content.substring(w+1,content.length()));
      word1=u[0];
      word2=u[1];
      word3=u[2];
      sw1.setText(word1);
      int x=i==-1?w:i;
      sw1.setActionCommand(word1.equals("~")?"":word1.substring(content.substring(x+1,content.length()).length()));
      sw2.setText(word2);
      sw2.setActionCommand(word2.equals("~")?"":word2.substring(content.substring(x+1,content.length()).length()));
      sw3.setText(word3);
      sw3.setActionCommand(word3.equals("~")?"":word3.substring(content.substring(x+1,content.length()).length()));
    }
  }
  
  public void actionPerformed(ActionEvent e)
  {
//button was pressed
    String event=e.getActionCommand();
    try
    {
      if(event.equals("Translation"))
      {
        setMode(0);
        return;
      }
      if(event.equals("Predictive Text"))
      {
        setMode(1);
        return;
      }
      if(event.equals("Sentence Alignment"))
      {
        setMode(2);
        return;
      }
      if(mode==0)
      {
        if(event.equals("Translate"))
          area.setText(d.decode(field.getText()));
      }
      else if(mode==1)
      {
        area.setText(area.getText()+event+" ");
      }
      else if(mode==2)
      {
        if (event.equals("Align"))
        {
//we now know which button was pressed
          System.out.println("text field contains:  " + field.getText());
          Graphics g = image.getGraphics();
          g.setColor(Color.WHITE);
          g.fillRect(0, 0, image.getWidth(), image.getHeight());
          v.v(field.getText(),field2.getText(), this, smt3);
        }
      }
    }
    catch(IOException r)
    {
      System.out.println("ERROR: unexpected behavior");
    }
  }
  
  public void setMode(int mode) throws IOException, FileNotFoundException
  {
    this.mode=mode;
    frame.getContentPane().removeAll();
    int width = 600;
    int height = 600;
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics g = image.getGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, width, height);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    this.setPreferredSize(new Dimension(width, height));
    frame.getContentPane().setLayout(new BorderLayout());    
    JPanel modes=new JPanel();
    b1 = new JButton("Translation");
    b1.addActionListener(this);
    b1.setActionCommand("Translation");
    b2 = new JButton("Predictive Text");
    b2.addActionListener(this);
    b2.setActionCommand("Predictive Text");
    b3 = new JButton("Sentence Alignment");
    b3.addActionListener(this);
    b3.setActionCommand("Sentence Alignment");
    modes.setLayout(new BoxLayout(modes, BoxLayout.Y_AXIS));
    modes.add(b1);
    modes.add(b2);
    modes.add(b3);
    frame.getContentPane().add(modes, BorderLayout.WEST);
    if(mode==0)
    {
      frame.setTitle("Statistical Machine Translation");
      JPanel r=new JPanel();
      JLabel l=new JLabel("German:");
      r.add(l);
      field = new JTextField(25);
      r.add(field);
      button = new JButton("Translate");
      button.addActionListener(this);
      button.setActionCommand("Translate");
      r.add(button);
      frame.getContentPane().add(r, BorderLayout.NORTH);
      area = new JTextArea("Translation will appear here",10, 50);
      area.setEditable(false);
      area.setLineWrap(true);
      frame.getContentPane().add(area, BorderLayout.CENTER);
    }
    else if(mode==1)
    {
      frame.setTitle("Predictive Text");
      JPanel panel=new JPanel();
      word1="suggested word 1";
      sw1 = new JButton(word1);
      sw1.addActionListener(this);
      sw1.setActionCommand(word1);
      panel.add(sw1);
      word2="suggested word 2";
      sw2 = new JButton(word2);
      sw2.addActionListener(this);
      sw2.setActionCommand(word2);
      panel.add(sw2);
      word3="suggested word 3";
      sw3 = new JButton(word3);
      sw3.addActionListener(this);
      sw3.setActionCommand(word3);
      panel.add(sw3);
      frame.getContentPane().add(panel, BorderLayout.NORTH);
      area = new JTextArea("by-products of",10, 50);
      area.setEditable(true);
      area.setLineWrap(true);
      area.getDocument().addDocumentListener(this);
      updateButtons();
      frame.getContentPane().add(area, BorderLayout.CENTER);
    }
    else if(mode==2)
    {
      frame.setTitle("Viterbi Alignment");
      JPanel p=new JPanel();
      JLabel l=new JLabel("German:");
      p.add(l);
      field = new JTextField(25);
      p.add(field);
      JLabel l2=new JLabel("English:");
      p.add(l2);
      field2 = new JTextField(25);
      p.add(field2);
      button = new JButton("Align");
      button.addActionListener(this);
      button.setActionCommand("Align");
      p.add(button);
      frame.getContentPane().add(p, BorderLayout.NORTH);
      new ViterbiAlignment().v("das ist gut", "that is good", this, smt3);
      frame.getContentPane().add(this, BorderLayout.CENTER);
    }
    frame.pack();
    frame.setVisible(true);
  }
  
  public SketchPad(Color background)
  {
    int width = 400;
    int height = 400;
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics g = image.getGraphics();
    g.setColor(background);
    g.fillRect(0, 0, width, height);
    
    frame = new JFrame();
    frame.setTitle("SketchPad");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.getContentPane().setLayout(new BorderLayout());
    
    this.setPreferredSize(new Dimension(width, height));
    frame.getContentPane().add(this, BorderLayout.CENTER);
    
    frame.pack();
    frame.setVisible(true);
  }
  
  //range from 0 to 1, where (0, 0) is bottom left
  public void drawLine(double x1, double y1, double x2, double y2)
  {
    Graphics g = image.getGraphics();
    g.setColor(Color.BLACK);
    
    int width = image.getWidth(null);
    int height = image.getHeight(null);
    
    g.drawLine((int)(x1 * width), (int)((1 - y1) * height), (int)(x2 * width), (int)((1 - y2) * height));
    repaint();
  }
  
  public void drawLine(Color c, double x1, double y1, double x2, double y2)
  {
    Graphics2D g = (Graphics2D)image.getGraphics();
    g.setColor(c);
    
    int width = image.getWidth(null);
    int height = image.getHeight(null);
    
    g.setStroke(new BasicStroke(1));
    g.drawLine((int)(x1 * width), (int)((1 - y1) * height), (int)(x2 * width), (int)((1 - y2) * height));
    repaint();
  }
  
  public void drawText(String s, double x, double y)
  {
    Graphics g = image.getGraphics();
    g.setColor(Color.BLACK);
    g.drawString(s, (int)(image.getWidth(null)*(x)),(int)(image.getHeight(null)*(1-y)));
  }
  
  public void drawText(String s, double x, double y, double angle)
  {
    Graphics2D g2d = (Graphics2D)image.getGraphics();
    g2d.translate((int)(image.getWidth(null)*x),(int)(image.getHeight(null)*y));
    g2d.rotate(Math.toRadians(angle));
    g2d.drawString(s,0,0);
    g2d.rotate(-Math.toRadians(angle));
    g2d.translate(-(int)(image.getWidth(null)*x),-(int)(image.getHeight(null)*y));
  }
  
  public void paintComponent(Graphics g)
  {
    g.drawImage(image, 0, 0, null);
    drawText("",0.5,0.5);
  }
}