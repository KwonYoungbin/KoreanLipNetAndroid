package bykkmouse.lipnet;


import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class UploadTask extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... strings) {
        String filePath = strings[0];
        String fileName = strings[1];
        //Environment.getExternalStorageDirectory().getAbsolutePath()+"/myApp";
        //Log.d("ㅋㅋ : 스트링0",strings[0] + "");
        //Log.d("ㅋㅋ : 외부저장소",Environment.getExternalStorageDirectory().getAbsolutePath()+"/myApp");
        //Log.d("ㅋㅋ : 이름 ",strings[1] + "");
        String resp = null;
        String lineEnd = "\r\n";
        String boundary = "LipNetUpload";
        //File targetFile = new File("/storage/emulated/0/myApp/VIDEO-137916884351872359.3gp");
        File targetFile = new File(filePath);

        byte[] buffer;
        int maxBufferSize = 5*1024*1024;
        HttpURLConnection conn = null;
        String urlStr = "http://192.168.1.2:5000/fileupload2"; //192.168.1.5:5000(노트북) , 192.168.1.2:5000(데스크탑)
        try {
            conn = (HttpURLConnection)(new URL(urlStr)).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setReadTimeout(50000);
        conn.setConnectTimeout(50000);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        String delimiter = "--" + boundary + lineEnd; // --LipNetUpload\r\n
        StringBuffer postDataBuilder = new StringBuffer();

        postDataBuilder.append(delimiter);
        postDataBuilder.append("Content-Disposition: form-data; name=\"video\"; filename=\"" + fileName + "\"" + lineEnd);

        try {
            DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
            ds.write(postDataBuilder.toString().getBytes());

            //if(filePath != null)
            ds.writeBytes(lineEnd);
            FileInputStream fStream = new FileInputStream(targetFile);
            buffer = new byte[maxBufferSize];
            int length = -1;
            while((length = fStream.read(buffer)) != -1) {
                ds.write(buffer, 0, length);
            }

            ds.writeBytes(lineEnd);
            ds.writeBytes(lineEnd);
            ds.writeBytes("--" + boundary + "--" + lineEnd); //requestbody end
            fStream.close();

            ds.flush();
            ds.close();

            //int responseCode = conn.getResponseCode();
            //if(responseCode == HttpURLConnection.HTTP_OK) {

            String line = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while((line = br.readLine()) != null) {
                resp += line;
            }

            //}

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
