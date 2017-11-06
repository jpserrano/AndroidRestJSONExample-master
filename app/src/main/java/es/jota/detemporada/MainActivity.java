package es.jota.detemporada;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    Activity activity;
    ArrayList<Alimento> alimentos = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        listView = (ListView) findViewById(android.R.id.list);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                alimentos.clear();

                // This method is called once with the initial value and again whenever data at this location is updated.
                GenericTypeIndicator<List<Alimento>> genericTypeIndicator = new GenericTypeIndicator<List<Alimento>>(){};
                List<Alimento> alimentosTmp = dataSnapshot.getValue(genericTypeIndicator);

                if(alimentosTmp != null) {
                    alimentos.addAll(alimentosTmp);

                    ListaAlimentos listaAlimentos = new ListaAlimentos(activity, alimentos);
                    listView.setAdapter(listaAlimentos);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

}

