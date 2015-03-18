package giroresh.mediacenterclient;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by giro on 2014.12.10..
 * Does the actual sending and receiving
 */
public class SocketAsyncTask extends AsyncTask<Object, Void, String> {
    private String serverIP = null;
    private int portNr = 0;
    private PrintWriter outputStream = null;
    private BufferedReader inputStream = null;
    private Socket serverSocket;
    private String serverDO = null;
    private String result = "";


    public SocketAsyncTask() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        if (!isCancelled()) {
            Log.d("ASYNC","currently working heavily!!!");
        }
        super.onProgressUpdate(values);
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     *
     * @return A result, defined by the subclass of this task.
     *
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected String doInBackground(Object... params) {
        try {
            serverIP = (String) params[0];
            portNr = (Integer) params[1];
            serverDO = (String) params[2];

            serverSocket = new Socket(serverIP, portNr);
            outputStream = new PrintWriter(serverSocket.getOutputStream(), true);
            inputStream = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

            outputStream.print(serverDO);
            outputStream.flush();
            String temp = "";
            while ((temp = inputStream.readLine()) != null) {
                result += temp;
            }

            inputStream.close();
            outputStream.close();
            serverSocket.close();
        } catch (IOException e) {
            Log.d("ASYNC", "Socket ERROR");
        }
        return result;
    }

    void setCmd(String doCmd) {
        serverDO = doCmd;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        setCmd(result);
    }

}