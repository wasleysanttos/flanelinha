package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by WasleySantos on 13/11/2016.
 */

public class ConverteData {

    public String extraiData(Date dtData) throws Exception{

//        Calendar c = Calendar.getInstance();
//        c.setTime(dtData);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

//        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

        return df.format(dtData);
    }

    public String extraiHoraDeDta (Date dtData) throws Exception{

//        Calendar c = Calendar.getInstance();
//        c.setTime(dtData);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

//        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

        return df.format(dtData);
    }

}
