package br.com.moderar.leitorcartaorespostamobilemoderar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements InterfaceAsyncTask {

    private static final int CAMERA_REQUEST = 200;
    public static final String TEXTO_OCR = "textoOCR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_moderar_round);
        toolbar.setTitle(R.string.moderar);
        toolbar.setSubtitle(R.string.tecnologia_informacao);
        setSupportActionBar(toolbar);

        ImageButton imageButtonCapturar = findViewById(R.id.imageButtonCapturar);
        imageButtonCapturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        int permissaoWES = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissaoRES = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if((permissaoWES != PackageManager.PERMISSION_GRANTED )||(permissaoRES != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }, 0);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            new OCRTask(MainActivity.this, MainActivity.this).execute(bmp);
        }
    }

    @Override
    public void postExecute(String texto) {

        Intent intent = new Intent(MainActivity.this, OCRResultFormActivity.class);
        intent.putExtra(TEXTO_OCR, texto);
        startActivity(intent);

    }
}
