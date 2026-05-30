package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.network.ApiClient;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity {

    private TextView mapName;
    private TextView mapEmpty;
    private ImageView mapImage;
    private int salaId;
    private Intent received;
    private int mapaRetries = 0;
    private static final int MAX_MAPA_RETRIES = 3;

    private final Matrix matrix = new Matrix();
    private ScaleGestureDetector scaleDetector;
    private float minScale = 1.0f;
    private static final float MAX_SCALE = 6.0f;
    private float currentScale = 1.0f;

    private final PointF lastTouch = new PointF();
    private long lastTapTime = 0;
    private static final long DOUBLE_TAP_MS = 280;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.apply(this);
        setContentView(R.layout.activity_map);
        ThemeHelper.setup(this, findViewById(R.id.theme_switch));

        received = getIntent();
        salaId   = received.getIntExtra("salaId", -1);

        mapName  = findViewById(R.id.map_name);
        mapEmpty = findViewById(R.id.map_empty);
        mapImage = findViewById(R.id.map_image);

        Button navStats     = findViewById(R.id.nav_stats);
        Button navInventory = findViewById(R.id.nav_inventory);

        navStats.setOnClickListener(v -> {
            startActivity(new Intent(this, CharacterDetailActivity.class).putExtras(received));
            finish();
        });
        navInventory.setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryActivity.class).putExtras(received));
            finish();
        });

        scaleDetector = new ScaleGestureDetector(this,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        float factor = detector.getScaleFactor();
                        float newScale = currentScale * factor;
                        if (newScale < minScale) factor = minScale / currentScale;
                        if (newScale > MAX_SCALE) factor = MAX_SCALE / currentScale;
                        currentScale *= factor;
                        matrix.postScale(factor, factor,
                                detector.getFocusX(), detector.getFocusY());
                        clampMatrix();
                        mapImage.setImageMatrix(matrix);
                        return true;
                    }
                });

        mapImage.setScaleType(ImageView.ScaleType.MATRIX);
        mapImage.setOnTouchListener(this::onMapTouch);
    }

    private boolean onMapTouch(View v, MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastTouch.set(event.getX(), event.getY());
                long now = System.currentTimeMillis();
                if (now - lastTapTime < DOUBLE_TAP_MS) resetZoom();
                lastTapTime = now;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!scaleDetector.isInProgress() && currentScale > minScale + 0.01f) {
                    float dx = event.getX() - lastTouch.x;
                    float dy = event.getY() - lastTouch.y;
                    matrix.postTranslate(dx, dy);
                    clampMatrix();
                    mapImage.setImageMatrix(matrix);
                }
                lastTouch.set(event.getX(), event.getY());
                break;
        }
        return true;
    }

    private void clampMatrix() {
        Drawable d = mapImage.getDrawable();
        if (d == null) return;
        int vw = mapImage.getWidth();
        int vh = mapImage.getHeight();
        if (vw == 0 || vh == 0) return;

        float[] vals = new float[9];
        matrix.getValues(vals);
        float scaleX = vals[Matrix.MSCALE_X];
        float imgW   = d.getIntrinsicWidth()  * scaleX;
        float imgH   = d.getIntrinsicHeight() * scaleX;

        vals[Matrix.MTRANS_X] = imgW <= vw ? (vw - imgW) / 2f
                : Math.max(vw - imgW, Math.min(0, vals[Matrix.MTRANS_X]));
        vals[Matrix.MTRANS_Y] = imgH <= vh ? (vh - imgH) / 2f
                : Math.max(vh - imgH, Math.min(0, vals[Matrix.MTRANS_Y]));
        matrix.setValues(vals);
    }

    private void resetZoom() {
        Drawable d = mapImage.getDrawable();
        if (d == null) return;
        int vw = mapImage.getWidth();
        int vh = mapImage.getHeight();
        int dw = d.getIntrinsicWidth();
        int dh = d.getIntrinsicHeight();
        if (vw == 0 || vh == 0 || dw == 0 || dh == 0) return;

        float scale = Math.min((float) vw / dw, (float) vh / dh);
        matrix.reset();
        matrix.setScale(scale, scale);
        matrix.postTranslate((vw - dw * scale) / 2f, (vh - dh * scale) / 2f);
        currentScale = scale;
        minScale     = scale;
        mapImage.setImageMatrix(matrix);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMapa();
    }

    private void cargarMapa() {
        ApiClient.getService().getMapas(salaId).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call,
                                   Response<List<Map<String, Object>>> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                mapaRetries = 0;

                String nombre = null;
                String imagen = "";
                for (Map<String, Object> m : response.body()) {
                    if (Boolean.TRUE.equals(m.get("visible"))) {
                        nombre = (String) m.get("name");
                        imagen = m.get("image") != null ? (String) m.get("image") : "";
                        break;
                    }
                }

                if (nombre != null) {
                    mapName.setText(nombre);
                    mapName.setVisibility(View.VISIBLE);
                    mapEmpty.setVisibility(View.GONE);

                    if (!imagen.isEmpty()) {
                        try {
                            byte[] bytes = Base64.decode(imagen, Base64.NO_WRAP);
                            Bitmap bmp   = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            if (bmp != null) {
                                mapImage.setImageBitmap(bmp);
                                mapImage.setVisibility(View.VISIBLE);
                                mapImage.post(() -> resetZoom());
                                return;
                            }
                        } catch (Exception ignored) { }
                    }
                    mapImage.setVisibility(View.GONE);
                } else {
                    mapName.setVisibility(View.GONE);
                    mapImage.setVisibility(View.GONE);
                    mapEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                if (mapaRetries < MAX_MAPA_RETRIES) {
                    mapaRetries++;
                    new android.os.Handler(android.os.Looper.getMainLooper())
                            .postDelayed(MapActivity.this::cargarMapa, 800);
                } else {
                    Toast.makeText(MapActivity.this,
                            "Error al cargar el mapa: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

