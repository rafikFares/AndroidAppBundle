package test.base.bundle.rafik.dynamic_activity_feature;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;



public class FeatureModuleApp extends AppCompatActivity{

    private static final String TAG = "tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,">>>>>>>>>>> FeatureModuleApp create activity");
        makeThings();

    }

    public void makeThings(){

    }

}
