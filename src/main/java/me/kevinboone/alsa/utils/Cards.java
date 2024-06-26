package me.kevinboone.alsa.utils;
import java.util.*;
import java.io.*;
import java.util.logging.*;
import java.util.regex.*;

public class Cards 
  {
  static Logger logger = Logger.getLogger (Cards.class.toString());
  static Pattern identPattern = Pattern.compile (".*\\[(\\S*)\\s.*\\].*");

/*=========================================================================
  Constructor
=========================================================================*/

  public static Vector<String> getCardIdents() throws AlsaException
    {
    Vector<String> cards = new Vector<String>();

    try (BufferedReader reader = new BufferedReader 
        (new FileReader (Config.PROC_ASOUND_CARDS)))
      {
      String line = reader.readLine();
      if (line != null) do
	{
        if (line.indexOf ('[') >= 0 && line.indexOf (']') >= 0)
          {
          cards.add (line);
          }
	line = reader.readLine();
	} while (line != null);

      }
    catch (IOException e)
      {
      throw new AlsaException ("Problem reading ALSA configuration: " 
        + e.toString());
      }

    return cards;
    }

/*=========================================================================
  cardNameFromIdent
=========================================================================*/
  public static String cardNameFromIdent (String cardIdent)
    {
    Matcher m = identPattern.matcher (cardIdent);
    if (m.matches())
      {
      return cardIdent.substring (m.start(1), m.end(1));
      }
    return null;
    }
  }

 
