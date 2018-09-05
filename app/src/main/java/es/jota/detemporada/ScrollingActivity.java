package es.jota.detemporada;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import es.jota.detemporada.dominio.Alimento;
import es.jota.detemporada.dominio.AlimentoGlobal;

import static es.jota.detemporada.MainActivity.EXTRA_ALIMENTO_SELECCIONADO;
import static es.jota.detemporada.MainActivity.EXTRA_MES_SELECCIONADO;

public class ScrollingActivity extends AppCompatActivity {

    private static final String TAG = ScrollingActivity.class.getName();

    private Alimento alimentoSeleccionado;
    private AlimentoGlobal alimentoGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // StatusBar transparente y NavigationBar translúcida
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        recuperarDatosMainActivity();
        establecerDatosToolbar();
        obtenerDatosGlobalesAlimento();
        mostrarGraficoCalidades();
    }

    /**
     * Recupera el Intent del MainActivity obteniendo el alimento seleccionado
     * y el mes en el que nos encontramos.
     */
    private void recuperarDatosMainActivity() {
        Intent intent = getIntent();
        alimentoSeleccionado = (Alimento) intent.getSerializableExtra(EXTRA_ALIMENTO_SELECCIONADO);
    }

    /**
     * Establece el título y la imagen de fondo del toolbar.
     */
    private void establecerDatosToolbar() {
        setTitle(alimentoSeleccionado.getNombreTraducido());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://de-temporada.appspot.com/img/alimentos/" + alimentoSeleccionado.getNombre() + ".jpg");

        // TODO Controlar el caso que la imagen no exista, mostrar una imagen predeterminada o no hacer nada, pero controlarlo

        ImageView imagenBackground = (ImageView) findViewById(R.id.img_background);
        imagenBackground.setContentDescription(alimentoSeleccionado.getNombre());

        Glide.with(this).using(new FirebaseImageLoader()).load(storageReference).into(imagenBackground);
    }

    /**
     * Recupera de la BD los datos globales del alimento.
     */
    private void obtenerDatosGlobalesAlimento() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference docRef = database.collection("alimentos").document(alimentoSeleccionado.getNombre());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        alimentoGlobal = document.toObject(AlimentoGlobal.class);
                        establecerDatosEnVista();
                    } else {
                        ocultarPanelNutricional();
                        Log.w(TAG, "obtenerDatosGlobalesAlimento: el documento asociado a '" + alimentoSeleccionado.getNombre() + "' es nulo");
                    }
                } else {
                    Log.w(TAG, "obtenerDatosGlobalesAlimento: error al obtener el documento '" + alimentoSeleccionado.getNombre() + "'");
                }
            }
        });
    }

    /**
     * Muestra en la vista los valores nutricionales obtenidos de BD.
     */
    @SuppressLint("SetTextI18n")
    private void establecerDatosEnVista() {
        if(alimentoGlobal != null) {
            ((TextView) findViewById(R.id.valor_calorias)).setText(String.valueOf(alimentoGlobal.getCalorias()) + " kcal");
            ((TextView) findViewById(R.id.valor_carbohidratos)).setText(String.valueOf(alimentoGlobal.getCarbohidratos()) + " gr");
            ((TextView) findViewById(R.id.valor_proteinas)).setText(String.valueOf(alimentoGlobal.getProteinas()) + " gr");
            ((TextView) findViewById(R.id.valor_grasas)).setText(String.valueOf(alimentoGlobal.getGrasas()) + " gr");
        }
    }

    private void ocultarPanelNutricional() {
        RelativeLayout panelNutricional = (RelativeLayout) findViewById(R.id.panel_nutricional);
        panelNutricional.setVisibility(View.GONE);
    }

    /**
     * Muestra el gráfico con la calidad del alimento por mes.
     */
    private void mostrarGraficoCalidades() {
        List<BarEntry> calidades = new ArrayList<>();
        float ejeX = 0;
        int[] listaColores = new int[12];

        for(Long ejeY : alimentoSeleccionado.getCalidades()) {
            // Ponemos un 1 para que aparezcan valores en la gráfica
            // Si pasa de 10 lo ponemos a 10 para igualar máximos
            if(ejeY == 0) {
                ejeY = 1L;
                listaColores[(int)ejeX] = R.color.calidad_0;
            } else if(ejeY >= 10) {
                ejeY = 10L;
                listaColores[(int)ejeX] = R.color.calidad_10;
            } else {
                listaColores[(int)ejeX] = R.color.calidad_5;
            }

            calidades.add(new BarEntry(ejeX, ejeY));
            ejeX++;
        }

        BarDataSet dataSet = new BarDataSet(calidades, "Calidades");
        dataSet.setColors(listaColores, this);
        BarData barData = new BarData(dataSet);
        BarChart barChart = (BarChart) findViewById(R.id.chart);
        barChart.setData(barData);

        aplicarEstilosChart(barChart);

        // Refrescar el gráfico
        barChart.invalidate();
    }

    private void aplicarEstilosChart(BarChart barChart) {
        // Deshabilita el zoom
        barChart.setScaleEnabled(false);

        // Ocultar los textos de la legenda y descripción
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // Elimina los labels con valores de la izquierda/derecha
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        // Elimina el grid posterior al gráfico
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.getAxisLeft().setDrawAxisLine(false);

        barChart.animateY(2000);

        barChart.getData().setDrawValues(false);

        // Establecer los label del eje X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(12);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int recursoNombre = getResources().getIdentifier("mes_corto_" + ((int)value + 1), "string", MainActivity.class.getPackage().getName());
                return getResources().getString(recursoNombre);
            }
        });
    }
}
