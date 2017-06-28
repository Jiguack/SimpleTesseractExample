package com.spacepalm.meidreader;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    public static final int UPDATE_MEID = 0;
    public static final int TAKE_PICTURE = 1;
    private DataSender sender;
    Vibrator v;

    //public static Bitmap image;
    public static TessBaseAPI mTess;
    String datapath = "";
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case TAKE_PICTURE:
                    v.vibrate(50);
                    break;
                case UPDATE_MEID:
                    if (msg.obj != null) {
                        String meid = (String) msg.obj;
                        if (meid.equalsIgnoreCase("NONE"))
                            return;
                        Log.d("OCR", "MEID:" + meid);
                        TextView textView = (TextView) findViewById(R.id.textView);
                        textView.setText(meid);

                        //send to server
                        sender.send("MEID", meid);
                        v.vibrate(500);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        sender = new DataSender(null);
        //init image
        //image = BitmapFactory.decodeResource(getResources(), R.drawable.test_image);

        //initialize Tesseract API
        String language = "eng";
        datapath = getFilesDir()+ "/tesseract/";
        mTess = new TessBaseAPI();

        checkFile(new File(datapath + "tessdata/"));

        mTess.init(datapath, language);

        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.CameraContainer, Camera2BasicFragment.newInstance())
                    .commit();
            Camera2BasicFragment.newInstance().mMainActivity = this;
        }
    }

    @Override
    protected void onDestroy() {
        sender.exit();
    }

    public static String processImage(Bitmap image){
        Log.e("OCR", "[Processing Image ...]");

        String OCRresult;
        String imei = "NONE";
        String meid;

        try {
            mTess.setImage(image);
        } catch(RuntimeException e) {
            e.printStackTrace();
        }

        OCRresult = mTess.getUTF8Text();
        if (OCRresult != null) {

            if (OCRresult.length() > 200)
                return imei;

            //Log.e("OCR", "[RESULT_Raw] LEN:" + OCRresult.length());
            Log.d("OCR", "[RESULT_Raw]" + OCRresult);
            meid = getMEID(OCRresult);
            Log.e("OCR", "[RESULT_MEID]" + meid);
            if (!meid.equalsIgnoreCase("NONE")) {
                imei = getIMEI(OCRresult, meid);
            }
        }
        return imei;
    }

    public static String getMEID(String rawData) {
        int count = 0;
        int countStart = 0;
        String found;
        for(int i=0; i<rawData.length(); i++) {
            if (rawData.charAt(i) >= '0' && rawData.charAt(i) <= '9') {
                count++;
                if (count == 1)
                    countStart = i;
            } else {
                if (count == 14) {
                    found = rawData.substring(countStart, countStart+14);
                    return found;
                } else {
                    count = 0;
                    countStart = 0;
                }
            }
        }
        return "NONE";
    }

    public static String getIMEI(String rawData, String meid) {
        String ret;
        String lastpart = meid.substring(8,14);
        String lastCheck = Integer.toString(Checksum.getNumber(meid));
        String toFind1 = lastpart + lastCheck;
        String toFind2 = lastpart + ' ' + lastCheck;
        Log.d("OCR", "Trying find IMEI for " + toFind1 + "or" + toFind2);
        if (rawData.contains(toFind1) || rawData.contains(toFind2)) {
            ret = meid + lastCheck;
            Log.d("OCR", "FOUND IMEI:" + ret);
        } else {
            Log.d("OCR", "NOT FOUND");
            ret = null;
        }
        return ret;
    }

    private void checkFile(File dir) {
        if (!dir.exists()&& dir.mkdirs()){
                copyFiles();
        }
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();

            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }

            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
