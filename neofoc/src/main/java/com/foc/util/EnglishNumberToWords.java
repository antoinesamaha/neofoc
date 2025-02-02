/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.foc.util;
import java.text.DecimalFormat;

public class EnglishNumberToWords {

  private static final String[] tensNames = {
    "",
    " ten",
    " twenty",
    " thirty",
    " forty",
    " fifty",
    " sixty",
    " seventy",
    " eighty",
    " ninety"
  };

  private static final String[] numNames = {
    "",
    " one",
    " two",
    " three",
    " four",
    " five",
    " six",
    " seven",
    " eight",
    " nine",
    " ten",
    " eleven",
    " twelve",
    " thirteen",
    " fourteen",
    " fifteen",
    " sixteen",
    " seventeen",
    " eighteen",
    " nineteen"
  };

  private static String convertLessThanOneThousand(int number) {
    String soFar;

    if (number % 100 < 20){
      soFar = numNames[number % 100];
      number /= 100;
    }
    else {
      soFar = numNames[number % 10];
      number /= 10;

      soFar = tensNames[number % 10] + soFar;
      number /= 10;
    }
    if (number == 0) return soFar;
    return numNames[number] + " hundred" + soFar;
  }


  public static String convert(long number) {
    // 0 to 999 999 999 999
    if (number == 0) { return "zero"; }

    String snumber = Long.toString(number);

    // pad with "0"
    String mask = "000000000000";
    DecimalFormat df = new DecimalFormat(mask);
    snumber = df.format(number);

    // XXXnnnnnnnnn 
    int billions = Integer.parseInt(snumber.substring(0,3));
    // nnnXXXnnnnnn
    int millions  = Integer.parseInt(snumber.substring(3,6)); 
    // nnnnnnXXXnnn
    int hundredThousands = Integer.parseInt(snumber.substring(6,9)); 
    // nnnnnnnnnXXX
    int thousands = Integer.parseInt(snumber.substring(9,12));    

    String tradBillions;
    switch (billions) {
    case 0:
      tradBillions = "";
      break;
    case 1 :
      tradBillions = convertLessThanOneThousand(billions) 
      + " billion ";
      break;
    default :
      tradBillions = convertLessThanOneThousand(billions) 
      + " billion ";
    }
    String result =  tradBillions;

    String tradMillions;
    switch (millions) {
    case 0:
      tradMillions = "";
      break;
    case 1 :
      tradMillions = convertLessThanOneThousand(millions) 
      + " million ";
      break;
    default :
      tradMillions = convertLessThanOneThousand(millions) 
      + " million ";
    }
    result =  result + tradMillions;

    String tradHundredThousands;
    switch (hundredThousands) {
    case 0:
      tradHundredThousands = "";
      break;
    case 1 :
      tradHundredThousands = "one thousand ";
      break;
    default :
      tradHundredThousands = convertLessThanOneThousand(hundredThousands) 
      + " thousand ";
    }
    result =  result + tradHundredThousands;

    String tradThousand;
    tradThousand = convertLessThanOneThousand(thousands);
    result =  result + tradThousand;

    // remove extra spaces!
    result = result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    
    //First letter should be capital
    String firstLetter = result.substring(0, 1);
    firstLetter = firstLetter.toUpperCase();
    result = firstLetter + result.substring(1);
    
    return result;
  }

  /**
   * testing
   * @param args
   */
  public static void main(String[] args) {
    System.out.println("*** " + EnglishNumberToWords.convert(0));
    System.out.println("*** " + EnglishNumberToWords.convert(1));
    System.out.println("*** " + EnglishNumberToWords.convert(16));
    System.out.println("*** " + EnglishNumberToWords.convert(100));
    System.out.println("*** " + EnglishNumberToWords.convert(118));
    System.out.println("*** " + EnglishNumberToWords.convert(200));
    System.out.println("*** " + EnglishNumberToWords.convert(219));
    System.out.println("*** " + EnglishNumberToWords.convert(800));
    System.out.println("*** " + EnglishNumberToWords.convert(801));
    System.out.println("*** " + EnglishNumberToWords.convert(1316));
    System.out.println("*** " + EnglishNumberToWords.convert(1000000));
    System.out.println("*** " + EnglishNumberToWords.convert(2000000));
    System.out.println("*** " + EnglishNumberToWords.convert(3000200));
    System.out.println("*** " + EnglishNumberToWords.convert(700000));
    System.out.println("*** " + EnglishNumberToWords.convert(9000000));
    System.out.println("*** " + EnglishNumberToWords.convert(9001000));
    System.out.println("*** " + EnglishNumberToWords.convert(123456789));
    System.out.println("*** " + EnglishNumberToWords.convert(2147483647));
    System.out.println("*** " + EnglishNumberToWords.convert(3000000010L));

    /*
     *** zero
     *** one
     *** sixteen
     *** one hundred
     *** one hundred eighteen
     *** two hundred
     *** two hundred nineteen
     *** eight hundred
     *** eight hundred one
     *** one thousand three hundred sixteen
     *** one million 
     *** two millions 
     *** three millions two hundred
     *** seven hundred thousand 
     *** nine millions 
     *** nine millions one thousand 
     *** one hundred twenty three millions four hundred 
     **      fifty six thousand seven hundred eighty nine
     *** two billion one hundred forty seven millions 
     **      four hundred eighty three thousand six hundred forty seven
     *** three billion ten
     **/
  }
}

/*
Fran�ais 
Quite different than the english version but french is a lot more difficult!
package com.rgagnon.howto;

import java.text.*;

class FrenchNumberToWords {
  private static final String[] dizaineNames = {
    "",
    "",
    "vingt",
    "trente",
    "quarante",
    "cinquante",
    "soixante",
    "soixante",
    "quatre-vingt",
    "quatre-vingt"
  };

  private static final String[] uniteNames1 = {
    "",
    "un",
    "deux",
    "trois",
    "quatre",
    "cinq",
    "six",
    "sept",
    "huit",
    "neuf",
    "dix",
    "onze",
    "douze",
    "treize",
    "quatorze",
    "quinze",
    "seize",
    "dix-sept",
    "dix-huit",
    "dix-neuf"
  };

  private static final String[] uniteNames2 = {
    "",
    "",
    "deux",
    "trois",
    "quatre",
    "cinq",
    "six",
    "sept",
    "huit",
    "neuf",
    "dix"
  };

  private static String convertZeroToHundred(int number) {

    int laDizaine = number / 10;
    int lUnite = number % 10;
    String resultat = "";

    switch (laDizaine) {
    case 1 :
    case 7 :
    case 9 :
      lUnite = lUnite + 10;
      break;
    default:
    }

    // s�parateur "-" "et"  ""
    String laLiaison = "";
    if (laDizaine > 1) {
      laLiaison = "-";
    }
    // cas particuliers
    switch (lUnite) {
    case 0:
      laLiaison = "";
      break;
    case 1 :
      if (laDizaine == 8) {
        laLiaison = "-";
      }
      else {
        laLiaison = " et ";
      }
      break;
    case 11 :
      if (laDizaine==7) {
        laLiaison = " et ";
      }
      break;
    default:
    }

    // dizaines en lettres
    switch (laDizaine) {
    case 0:
      resultat = uniteNames1[lUnite];
      break;
    case 8 :
      if (lUnite == 0) {
        resultat = dizaineNames[laDizaine];
      }
      else {
        resultat = dizaineNames[laDizaine] 
                                + laLiaison + uniteNames1[lUnite];
      }
      break;
    default :
      resultat = dizaineNames[laDizaine] 
                              + laLiaison + uniteNames1[lUnite];
    }
    return resultat;
  }

  private static String convertLessThanOneThousand(int number) {

    int lesCentaines = number / 100;
    int leReste = number % 100;
    String sReste = convertZeroToHundred(leReste);

    String resultat;
    switch (lesCentaines) {
    case 0:
      resultat = sReste;
      break;
    case 1 :
      if (leReste > 0) {
        resultat = "cent " + sReste;
      }
      else {
        resultat = "cent";
      }
      break;
    default :
      if (leReste > 0) {
        resultat = uniteNames2[lesCentaines] + " cent " + sReste;
      }
      else {
        resultat = uniteNames2[lesCentaines] + " cents";
      }
    }
    return resultat;
  }

  public static String convert(long number) {
    // 0 � 999 999 999 999
    if (number == 0) { return "z�ro"; }

    String snumber = Long.toString(number);

    // pad des "0"
    String mask = "000000000000";
    DecimalFormat df = new DecimalFormat(mask);
    snumber = df.format(number);

    // XXXnnnnnnnnn 
    int lesMilliards = Integer.parseInt(snumber.substring(0,3));
    // nnnXXXnnnnnn
    int lesMillions  = Integer.parseInt(snumber.substring(3,6)); 
    // nnnnnnXXXnnn
    int lesCentMille = Integer.parseInt(snumber.substring(6,9)); 
    // nnnnnnnnnXXX
    int lesMille = Integer.parseInt(snumber.substring(9,12));    

    String tradMilliards;
    switch (lesMilliards) {
    case 0:
      tradMilliards = "";
      break;
    case 1 :
      tradMilliards = convertLessThanOneThousand(lesMilliards) 
      + " milliard ";
      break;
    default :
      tradMilliards = convertLessThanOneThousand(lesMilliards) 
      + " milliards ";
    }
    String resultat =  tradMilliards;

    String tradMillions;
    switch (lesMillions) {
    case 0:
      tradMillions = "";
      break;
    case 1 :
      tradMillions = convertLessThanOneThousand(lesMillions) 
      + " million ";
      break;
    default :
      tradMillions = convertLessThanOneThousand(lesMillions) 
      + " millions ";
    }
    resultat =  resultat + tradMillions;

    String tradCentMille;
    switch (lesCentMille) {
    case 0:
      tradCentMille = "";
      break;
    case 1 :
      tradCentMille = "mille ";
      break;
    default :
      tradCentMille = convertLessThanOneThousand(lesCentMille) 
      + " mille ";
    }
    resultat =  resultat + tradCentMille;

    String tradMille;
    tradMille = convertLessThanOneThousand(lesMille);
    resultat =  resultat + tradMille;

    return resultat;
  }

  public static void main(String[] args) {
    System.out.println("*** " + FrenchNumberToWords.convert(0));
    System.out.println("*** " + FrenchNumberToWords.convert(9));
    System.out.println("*** " + FrenchNumberToWords.convert(19));
    System.out.println("*** " + FrenchNumberToWords.convert(21));
    System.out.println("*** " + FrenchNumberToWords.convert(28));
    System.out.println("*** " + FrenchNumberToWords.convert(71));
    System.out.println("*** " + FrenchNumberToWords.convert(72));
    System.out.println("*** " + FrenchNumberToWords.convert(80));
    System.out.println("*** " + FrenchNumberToWords.convert(81));
    System.out.println("*** " + FrenchNumberToWords.convert(89));
    System.out.println("*** " + FrenchNumberToWords.convert(90));
    System.out.println("*** " + FrenchNumberToWords.convert(91));
    System.out.println("*** " + FrenchNumberToWords.convert(97));
    System.out.println("*** " + FrenchNumberToWords.convert(100));
    System.out.println("*** " + FrenchNumberToWords.convert(101));
    System.out.println("*** " + FrenchNumberToWords.convert(110));
    System.out.println("*** " + FrenchNumberToWords.convert(120));
    System.out.println("*** " + FrenchNumberToWords.convert(200));
    System.out.println("*** " + FrenchNumberToWords.convert(201));
    System.out.println("*** " + FrenchNumberToWords.convert(232));
    System.out.println("*** " + FrenchNumberToWords.convert(999));
    System.out.println("*** " + FrenchNumberToWords.convert(1000));
    System.out.println("*** " + FrenchNumberToWords.convert(1001));
    System.out.println("*** " + FrenchNumberToWords.convert(10000));
    System.out.println("*** " + FrenchNumberToWords.convert(10001));
    System.out.println("*** " + FrenchNumberToWords.convert(100000));
    System.out.println("*** " + FrenchNumberToWords.convert(2000000));
    System.out.println("*** " + FrenchNumberToWords.convert(3000000000L));
    System.out.println("*** " + FrenchNumberToWords.convert(2147483647));
    
     *** OUTPUT
     *** zero
     *** neuf
     *** dix-neuf
     *** vingt et un
     *** vingt-huit
     *** soixante et onze
     *** soixante-douze
     *** quatre-vingt
     *** quatre-vingt-un
     *** quatre-vingt-neuf
     *** quatre-vingt-dix
     *** quatre-vingt-onze
     *** quatre-vingt-dix-sept
     *** cent
     *** cent un
     *** cent dix
     *** cent vingt
     *** deux cents
     *** deux cent un
     *** deux cent trente-deux
     *** neuf cent quatre-vingt-dix-neuf
     *** mille
     *** mille un
     *** dix mille
     *** dix mille un
     *** cent mille
     *** deux millions
     *** trois milliards
     *** deux milliards cent quarante-sept millions 
     **          quatre cent quatre-vingt-trois mille six cent quarante-sept
     
  }
}
*/
