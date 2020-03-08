import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.io.*;
import java.util.StringTokenizer;

public class GUI extends JFrame implements ActionListener
{ JPanel panel = new JPanel();
  Controller cont = Controller.inst();
  JButton loadModelButton = new JButton("loadModel");
  JButton saveModelButton = new JButton("saveModel");
  JButton testButton = new JButton("test");
  JButton psButton = new JButton("compute ps");
  JButton rcButton = new JButton("compute rc");

 public GUI()
  { super("Select use case to execute");
    setContentPane(panel);
    addWindowListener(new WindowAdapter() 
    { public void windowClosing(WindowEvent e)
      { System.exit(0); } });
  panel.add(loadModelButton);
  loadModelButton.addActionListener(this);
  panel.add(saveModelButton);
  saveModelButton.addActionListener(this);
  panel.add(testButton);
  testButton.addActionListener(this);
  }

  public void actionPerformed(ActionEvent e)
  { if (e == null) { return; }
    String cmd = e.getActionCommand();
    if ("loadModel".equals(cmd))
    { Controller.loadModel("java/res/in_poisson.txt");
      cont.checkCompleteness();
      System.err.println("Model loaded");
      return; } 
    if ("saveModel".equals(cmd))
    { cont.saveModel("java/res/out_poisson.txt");
      cont.saveXSI("java/res/xsi_poisson.txt");
      return; } 
    if ("test".equals(cmd))
    {  cont.test() ;
       //cont.computeRC();
       return; }
    if ("compute ps".equals(cmd))
    {  cont.test() ;  return; }
    if ("compute rc".equals(cmd))
    {  //cont.computeRC();
      return; }
  }

  public static void main(String[] args)
  {  GUI gui = new GUI();
    gui.setSize(400,400);
    gui.setVisible(true);
  }
 }
