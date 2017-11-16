package es.jota.detemporada;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class AlimentoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimento);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Alimento alimentoSeleccionado = (Alimento) intent.getSerializableExtra(MainActivity.EXTRA_ALIMENTO_SELECCIONADO);
        int mesSeleccionado = intent.getIntExtra(MainActivity.EXTRA_MES_SELECCIONADO, 1);

        // Capture the layout's TextView and set the string as its text
        TextView textAlimento = (TextView) findViewById(R.id.texto_alimento);
        textAlimento.setText(alimentoSeleccionado.getNombre());

        TextView textMes = (TextView) findViewById(R.id.texto_mes);
        textMes.setText(mesSeleccionado + "");

        ImageView imagenAlimento = (ImageView) findViewById(R.id.imagen_alimento);
        imagenAlimento.setImageResource(Resources.getSystem().getIdentifier("img_" + alimentoSeleccionado.getNombre(), "drawable", this.getPackageName()));
    }
}
