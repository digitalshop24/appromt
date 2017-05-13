package by.digitalshop.quests.utils;

/**
 * Created by CoolerBy on 01.04.2017.
 */

public class TextUtils {
    public static String firstCapitalString(CharSequence c){
        if(android.text.TextUtils.isEmpty(c)){
            return "";
        }else {
            try {
                c = String.valueOf( c.charAt( 0 ) ).toUpperCase() + c.subSequence( 1, c.length() ).toString().toLowerCase();
                for ( int i = 0; i < c.length(); i++ ) {
                    if ( String.valueOf( c.charAt( i ) ).contains( " " ) ) {
                        c = c.subSequence( 0, i + 1 ) + String.valueOf( c.charAt( i + 1 ) ).toUpperCase() + c.subSequence( i + 2, c.length() ).toString().toLowerCase();
                    }
                }
            } catch ( Exception e ) {
                // String did not have more than + 2 characters after space.
            }
            return c.toString();
        }
    }
}
