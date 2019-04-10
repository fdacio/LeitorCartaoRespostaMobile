package br.com.moderar.leitorcartaorespostamobilemoderar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;


public class OCRTask extends AsyncTask<Bitmap, String, String> {

    private Context context;
    private boolean running = true;
    private String msg = "Processando Documento.\nAguarde...";
    private ProgressDialog progressDialog;
    private InterfaceAsyncTask interfaceAsyncTask;


    public OCRTask(Context context, InterfaceAsyncTask interfaceAsyncTask) {
        this.context = context;
        this.interfaceAsyncTask = interfaceAsyncTask;
    }

    @Override
    protected String doInBackground(Bitmap... params) {

        Bitmap bmp = params[0];

        TessBaseAPI baseApi = new TessBaseAPI();
        //String dirBase = "/storage/sdcard/";
        String dirBase = Environment.getExternalStorageDirectory()+"/";
        String datapath = dirBase + "tesseract/";
        String language = "eng";

        try {
            File dir = new File(datapath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            Log.e("LG", e.getMessage());
        }

        try {
            baseApi.init(datapath, language);
        } catch (Exception e) {
            Log.e("LG", e.getMessage());
        }

        baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,"1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST,"!@#$%^&*  ()_+=-[]}{" +";:'\"\\|~`,./<>?");
        baseApi.setDebug(true);
        baseApi.setImage(bmp);

        String recognizedText = baseApi.getUTF8Text();

        baseApi.end();

        return recognizedText;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.setOnCancelListener(new cancelTaskOCR(this));
        progressDialog.show();

    }

    @Override
    protected void onCancelled() {

        super.onCancelled();
        Toast.makeText(context, "Processamento cancelado!", Toast.LENGTH_SHORT).show();
        running = false;

    }

    @Override
    protected void onPostExecute(String retorno) {
        progressDialog.dismiss();
        this.interfaceAsyncTask.postExecute(retorno);
    }

    private class cancelTaskOCR implements DialogInterface.OnCancelListener {

        private AsyncTask task;

        public cancelTaskOCR(AsyncTask task) {
            this.task = task;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            new DialogBox(context,
                    DialogBox.DialogBoxType.QUESTION,
                    "Processamento",
                    "Deseja cancelar o processo?",
                    new DialogInterface.OnClickListener() {//Resposta SIM do DialogBox Question
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            task.cancel(true);
                        }
                    },
                    new DialogInterface.OnClickListener() {//Resposta NÃO do DialogBox Question
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            progressDialog.show();
                        }
                    }

            ).show();
        }
    }

}
