package me.kevinboone.alsa.jalsaselector.swing;
import me.kevinboone.alsa.utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.URL;
import java.util.regex.*;

public class AlsaCardViewer extends JFrame 
  {
  protected CardSelectedListener listener = null;
  protected JList list = null; 
  protected DefaultListModel<String> listModel = null;
  protected Pattern identPattern = null;

/*=========================================================================
  Constructor
=========================================================================*/
  public AlsaCardViewer()
    {
    super();
    setTitle ("ALSA selector");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    identPattern = Pattern.compile (".*\\[(\\S*)\\s.*\\].*");

    listModel = new DefaultListModel<String>();
    list = new JList (listModel);
    JScrollPane listScrollPane = new JScrollPane (list);
    getContentPane().add (listScrollPane, BorderLayout.CENTER);

    MouseListener mouseListener = new MouseAdapter() 
       {
       public void mouseClicked (MouseEvent e) 
         {
         if (e.getClickCount() == 2) 
           {
           String cardName = (String)list.getSelectedValue();
           if (listener != null) listener.selected (cardName);
           }
         }
      };
    list.addMouseListener (mouseListener);

    JButton okButton = new JButton ("OK");
    okButton.addActionListener ( new ActionListener() 
      {
      public void actionPerformed (ActionEvent e)
        {
        String cardIdent = (String)list.getSelectedValue();
        if (cardIdent != null)
          {
          if (listener != null) 
            { 
            String cardName = parseCardName (cardIdent);
            if (cardName != null)
              listener.selected (cardName);
            }
          }
        }
      });

    JButton cancelButton = new JButton ("Cancel");
    cancelButton.addActionListener (new ActionListener() 
      {
      public void actionPerformed (ActionEvent e)
        {
        System.exit(0);
        }
      });

    cancelButton.registerKeyboardAction 
      (new ActionListener()
        {
        public void actionPerformed (ActionEvent e)
          {
          System.exit(0);
          }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
      JComponent.WHEN_IN_FOCUSED_WINDOW);

    getRootPane().setDefaultButton (okButton);

    JPanel buttonPanel = new JPanel();    

    buttonPanel.add (cancelButton);
    buttonPanel.add (okButton);
    getContentPane().add (buttonPanel, BorderLayout.SOUTH);
    
    URL iconURL = getClass().getResource ("/images/jalsaselector.png");
    ImageIcon icon = new ImageIcon (iconURL);
    setIconImage (icon.getImage());

    setLocationByPlatform (true);
    }

/*=========================================================================
  setCardSelectedListener 
=========================================================================*/
  void setCardSelectedListener (CardSelectedListener listener)
    {
    this.listener = listener;
    }

/*=========================================================================
  setup 
=========================================================================*/
  void setup() throws AlsaException
    {
    Vector<String> cards = Cards.getCardIdents(); 
    if (cards.size() == 0)
      throw new AlsaException ("No ALSA devices found");

    for (int i = 0; i < cards.size(); i++)
      {
      listModel.addElement (cards.get(i)); 
      }
    pack();
    }

/*=========================================================================
  parseCardName 
=========================================================================*/
  String parseCardName (String cardIdent)
    {
    Matcher m = identPattern.matcher (cardIdent);
    if (m.matches())
      {
      return cardIdent.substring (m.start(1), m.end(1));
      }
    return null;
    }

/*=========================================================================
  setCard 
=========================================================================*/
  void setCard (String card)
    {
    if (card == null) return;

    int l = listModel.getSize();
    for (int i = 0; i < l; i++)
      {
      String s = listModel.get (i);
      String ps = parseCardName (s);
      if (ps != null) 
        {
        if (ps.equals (card))
          {
          list.setSelectedIndex (i);
          return;
          }
        }
      }
      
    }
  }


