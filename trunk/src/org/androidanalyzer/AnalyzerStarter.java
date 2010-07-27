package org.androidanalyzer;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author kleintot
 *
 */
public class AnalyzerStarter extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analisys);
    }
}