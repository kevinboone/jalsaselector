package me.kevinboone.alsa.jalsaselector.swing;
import me.kevinboone.alsa.utils.*;
import java.util.*; 
import java.io.*; 
import java.awt.*;
import javax.swing.*;
import org.apache.commons.cli.*;

public class Main
  {
  static String oldCard = null;
  static AlsaCardViewer viewer = null;

/*=========================================================================
  getPropsFileName 
=========================================================================*/
  static String getPropsFileName()
    {
    return System.getProperty("user.home") + "/" + Config.PROPS_FILE;
    }

/*=========================================================================
  getUserAsound
=========================================================================*/
  static String getUserAsound()
    {
    return System.getProperty("user.home") + "/" + ".asoundrc";
    }

/*=========================================================================
  getIncFileName
=========================================================================*/
  static String getIncFileName()
    {
    return System.getProperty("user.home") + "/" + Config.INC_FILE;
    }

/*=========================================================================
  checkUserConfig 
=========================================================================*/
  static void checkUserConfig() 
    {
    File f = new File (getUserAsound());
    if (f.isFile()) 
      {
      boolean found = false;
      try (BufferedReader reader = new BufferedReader 
          (new FileReader (f)))
        {
	String line = reader.readLine();
	do
	  {
          if (line != null)
            {
            if (line.indexOf (Config.INC_FILE) >= 0) 
              found = true;
            }
	  line = reader.readLine();
	  } while (line != null);
        }
      catch (Exception e) {} // What can we do??
      if (!found)
        {
        JOptionPane.showMessageDialog (null, 
          "$HOME/.asoundrc does not <include> this program's " + 
          "configuration file " + Config.INC_FILE,
          Config.APP_NAME, JOptionPane.WARNING_MESSAGE);
        }
      }
    else
      JOptionPane.showMessageDialog (null, 
         "$HOME/.asoundrc does not exist. This program will not create it",
         Config.APP_NAME, JOptionPane.WARNING_MESSAGE);

    }

/*=========================================================================
  writePropsFile
=========================================================================*/
  static void writePropsFile (String cardName)
    {
    try (FileWriter fw = new FileWriter (getPropsFileName()))
      {
      fw.write ("card=" + cardName + "\n"); 
      fw.close();
      }
    catch (IOException e)
      {
      System.err.println (e);
      }
    }

/*=========================================================================
  writeIncFile 
=========================================================================*/
  static void writeIncFile (String cardName)
      throws IOException
    {
    try (FileWriter fw = new FileWriter (getIncFileName()))
      {
      fw.write ("defaults.pcm.!card " + cardName + "\n"); 
      fw.write ("defaults.ctl.!card " + cardName + "\n"); 
      fw.close();
      }
    catch (IOException e)
      {
      throw e;
      }
    }

/*=========================================================================
  runGui 
=========================================================================*/
  public static void runGui()
    {
    System.setProperty("awt.useSystemAAFontSettings","on");
    System.setProperty("swing.aatext", "true");

    try
      {
      UIManager.setLookAndFeel ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
      }
    catch (Exception e){}

    checkUserConfig();

    //  event dispatcher thread. Probably not necessary here.
    SwingUtilities.invokeLater (new Runnable()
      {
      public void run()
        {
        viewer = new AlsaCardViewer();
        viewer.setCardSelectedListener (new CardSelectedListener() 
          {
          public void selected (String cardName)
            {
            writePropsFile (cardName);
            try
              {
              writeIncFile (cardName);
              JOptionPane.showMessageDialog (viewer, 
                "Default set to " + cardName, 
                 Config.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
              }
            catch (Exception e)
              {
              JOptionPane.showMessageDialog (viewer, 
                "Can't write " + getIncFileName() + ": " + e.toString(),
                 Config.APP_NAME, JOptionPane.ERROR_MESSAGE);
              }
            }
          });
        try
          {
          viewer.setup();
          viewer.setCard (oldCard);
          viewer.show();
          }
        catch (Exception e)
          {
          JOptionPane.showMessageDialog (viewer, e.toString(), 
             Config.APP_NAME, JOptionPane.ERROR_MESSAGE);
          System.exit (-1);
          }
        }
      });
    }

/*=========================================================================
  isCardInList 
=========================================================================*/
  public static boolean isCardNameInList (String tryCardName)
    {
    try
      {
      Vector<String> cardIdents = Cards.getCardIdents();
      int l = cardIdents.size();
      for (int i = 0; i < l; i++)
        {
        String cardIdent = cardIdents.get(i);
        String cardName = Cards.cardNameFromIdent (cardIdent);
        if (cardName != null)
          {
          if (cardName.equals (tryCardName)) return true;
          }
        }
      return false;
      }
    catch (AlsaException e)
      {
      return false; // We don't actually know ... assume not
      }
    }

/*=========================================================================
  setCard
=========================================================================*/
  public static void setCard (String cardName) throws Exception
    {
    if (!isCardNameInList (cardName))
      System.err.println (Config.PROG_NAME + ": Warning: " + 
         "Setting a default that is not in the card list");
    try
      {
      writePropsFile (cardName);
      }
    catch (Exception e){};

    writeIncFile (cardName);
    }

/*=========================================================================
  showCardList 
=========================================================================*/
  public static void showCardList (String currentCardName)
    {
    try
      {
      Vector<String> cardIdents = Cards.getCardIdents();
      int l = cardIdents.size();
      for (int i = 0; i < l; i++)
        {
        String cardIdent = cardIdents.get(i);
        String cardName = Cards.cardNameFromIdent (cardIdent);
        if (cardName != null)
          {
          if (cardName.equals (currentCardName))
            System.out.print ("*");
          System.out.println (cardName + "\t(" + cardIdent + " )"); 
          }
        else
          System.out.println ("???" + "\t(" + cardIdent + " )"); 
        }
      }
    catch (AlsaException e)
      {
      System.err.println (Config.PROG_NAME + ": " + e.toString());
      }
    }

/*=========================================================================
  main 
=========================================================================*/
  public static void main (String[] args)
    {
    boolean showHelp = false;
    boolean showVersion = false;
    boolean showList = false;
    String setCardName = null;

    Options options = new Options();
    options.addOption("a", false, "add two numbers");
    options.addOption("h", "help", false, "show this text");
    options.addOption("l", "list", false, "show list of cards");
    options.addOption("v", "version", false, "show version");
    options.addOption("s", "set", true, "set default card");

    try
      {
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse (options, args);
      if (cmd.hasOption ("help")) showHelp = true;
      if (cmd.hasOption ("version")) showVersion = true;
      if (cmd.hasOption ("list")) showList = true;
      if (cmd.hasOption ("set")) setCardName = cmd.getOptionValue ("set"); 
      }
    catch (Exception e)
      {
      System.err.println (e.getMessage ());
      System.err.println ("\"" + Config.PROG_NAME  + 
         " --help\" for command line information");
      System.exit (-1);
      }

    if (showHelp)
      {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp (Config.PROG_NAME, options);
      System.exit (-1);
      }

    if (showVersion)
      {
      System.out.println (Config.PROG_NAME  + " version 1.0.0");
      System.out.println ("Copyright (c)2024 Kevin Boone");
      System.out.println ("Distributed under the terms of the GPL, v.30");
      System.exit (0);
      }

    // Get the current 'card' name from the props file, if the
    //   file exists, and it is set. 
    try (InputStream is = new FileInputStream (getPropsFileName()))
      {
      getPropsFileName(); 
      Properties props = new Properties();
      props.load (is);
      oldCard = (String)props.get ("card");
      }
    catch (Exception e)
      {
      // Not an error -- the file might not exist yet.
      oldCard = null;
      }

    if (showList)
      {
      showCardList (oldCard);
      System.exit (0);
      }

    if (setCardName != null)
      {
      try
        {
        setCard (setCardName);
        System.exit (0);
        }
      catch (Exception e)
        {
        System.err.println (Config.PROG_NAME + ": " + e.toString());
        }
      }

    runGui();
    }

  }

