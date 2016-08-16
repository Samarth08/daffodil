package edu.illinois.ncsa.daffodil.functionality

import java.util.Locale

import org.junit.Assert
import org.junit.Test

import com.ibm.icu.text.SimpleDateFormat
import com.ibm.icu.util.Calendar
import com.ibm.icu.util.GregorianCalendar

class TestFunctionality {

  @Test def test_calendar_format_timezone() {
    /* Test where bug was found:
     * TestSimpleTypes.scala -> test_timeZoneFormats6
     * Modify the test to be roundTrip="true"
     * 
     * The original input into the test in Daffodil is 08:43.uslax
     * this translates into 08:43:00.000000-08:00 in the infoset.
     * On unparsing we expect to receive 08:43.uslax back, however 
     * we receive 08:43.unk.  This appears to be due to the fact
     * that the underlying ICU formatting does not recognize the
     * SimpleTimeZone GMT-0800 object that is parsed from the infoset
     * value.
     * 
     * Mike suggested creating this unit test to demonstrate the issue
     * and to verify that it is not a Daffodil bug, but that the issue
     * resides within the ICU library itself.  
     * 
     * We fully expect this test
     * to 'break' when a fix has been implemented.
     * */
    val value = "08:43:00.000000-08:00"
    val cal = new GregorianCalendar()
    val pos = new java.text.ParsePosition(0)
    val patternIn = "HH:mm:ss.SSSSSSxxxxx"
    new com.ibm.icu.text.SimpleDateFormat(patternIn).parse(value, cal, pos)
    cal.getTime

    val pattern = "hh:mm.V"
    val locale = Locale.ENGLISH
    val calendar = Calendar.getInstance(locale)
    calendar.clear()
    val formatter = new SimpleDateFormat(pattern, locale)
    // Make sure you've done 'sbt update-classifiers' to pull in all the source.
    // Step through the format here and into SimpleDateFormat -> case: 29 aka V -> TimeZoneFormat.format
    // It would appear as though the SimpleTimeZone GMT-0800 is not recognized in the ICU library
    // and so returns this 'unk' value rather than 'uslax'.  Even though -0800 GMT IS uslax!!! :(
    val str = formatter.format(cal)
    //Console.out.println(str)
    Assert.assertEquals("08:43.unk", str)
  }
}