package by.digitalshop.quests;

import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by CoolerBy on 18.12.2016.
 */

public class BaseActivity extends AppCompatActivity {

    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
	
	  public void showErrorSnackBar(int text){
// Create the Snackbar
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);
// Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(Color.WHITE);//change Snackbar's background color;

        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)layout.getLayoutParams();
        params.gravity = Gravity.TOP;
        layout.setLayoutParams(params);
// Hide the text
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this,R.color.purple));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setCompoundDrawablePadding(20);

// Show the Snackbar
        snackbar.show();
    }

    public void showSuccessSnackBar(int text){
// Create the Snackbar
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);
// Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(Color.WHITE);//change Snackbar's background color;

        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)layout.getLayoutParams();
        params.gravity = Gravity.TOP;
        layout.setLayoutParams(params);
// Hide the text
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this,R.color.purple));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setCompoundDrawablePadding(20);

// Show the Snackbar
        snackbar.show();
    }
}
