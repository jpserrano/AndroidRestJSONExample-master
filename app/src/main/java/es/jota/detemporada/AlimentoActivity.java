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

        Intent intent = getIntent();
        Alimento alimentoSeleccionado = (Alimento) intent.getSerializableExtra(MainActivity.EXTRA_ALIMENTO_SELECCIONADO);
        // TODO El mes seleccionado se usará para marcarlo en el panel de calidades por mes
        int mesSeleccionado = intent.getIntExtra(MainActivity.EXTRA_MES_SELECCIONADO, 1);


        // Si en el strings no está definido el nombre del alimento mostramos el nombre desde el objeto en BD
        TextView textAlimento = (TextView) findViewById(R.id.texto_alimento);
        int recursoNombre = AlimentoActivity.this.getResources().getIdentifier(alimentoSeleccionado.getNombre(), "string", AlimentoActivity.this.getPackageName());
        if(recursoNombre == 0) {
            textAlimento.setText(alimentoSeleccionado.getNombre());
            // TODO mostrar un log de error, aunque se pille el nombre de BD hay que dejar constancia para corregirlo
        } else {
            textAlimento.setText(recursoNombre);
        }

        // Si no existe la imagen del alimento mostramos una imagen genérica para que la interfaz no se descuadre
        ImageView imagenAlimento = (ImageView) findViewById(R.id.imagen_alimento);
        int recursoImagen = AlimentoActivity.this.getResources().getIdentifier("img_" + alimentoSeleccionado.getNombre(), "drawable", AlimentoActivity.this.getPackageName());
        if(recursoImagen == 0) {
            imagenAlimento.setImageResource(R.drawable.img_no_foto);
            // TODO mostrar un log para solucionar el problema
        } else {
            imagenAlimento.setImageResource(recursoImagen);
        }
    }
}
