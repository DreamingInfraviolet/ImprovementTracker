package space.amareth.mood;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RatingBar;

public class MainActivity extends AppCompatActivity {
    public static MainActivity mainActivity;

    public MainActivity() {
        mainActivity = this;
    }

    CalendarView calendarView;
    RatingBar ratingBar;
    Button detailsButton;

    HistoryEntry currentEntry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            HistoryManager.create(this);
            HistoryManager.instance().load();

            calendarView = (CalendarView) findViewById(R.id.calendarView);
            calendarView.setMinDate(getPackageManager()
                    .getPackageInfo("space.amareth.mood", 0)
                    .firstInstallTime);
            calendarView.setMaxDate(System.currentTimeMillis());

            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                    currentEntry = HistoryManager.instance().getEntry(view.getDate());
                    if (currentEntry == null)
                    {
                        mainActivity.ratingBar.setNumStars(0);
                        mainActivity.detailsButton.setEnabled(false);
                    }
                    else
                    {
                        mainActivity.ratingBar.setNumStars(currentEntry.rating);
                        mainActivity.detailsButton.setEnabled(true);
                    }
                }
            });

            ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            detailsButton = (Button)findViewById(R.id.detailsButton);
        }
        catch (Exception e)
        {
            Log.e("Error", e.toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void alertUser(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage(message)
                .setPositiveButton("Okay", null)
                .show();
    }
}