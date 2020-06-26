package com.example.calendartest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//import com.example.hkumoodle.CourseListActivity;
//import com.example.hkumoodle.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static java.lang.Boolean.TRUE;

public class MainActivity extends Activity implements View.OnClickListener {
    EditText txt_UserName, txt_UserPW;
    Button btn_Login;
    ArrayList<String> eventname=new ArrayList<String>();
    ArrayList<String> eventdate=new ArrayList<String>();
    ArrayList<String> eventtime=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.log_in);

        btn_Login = (Button)findViewById(R.id.btn_Login);
        txt_UserName = (EditText)findViewById(R.id.txt_UserName);
        txt_UserPW = (EditText)findViewById(R.id.txt_UserPW);

        // Register the Login button to click listener
        // Whenever the button is clicked, onClick is called
        btn_Login.setOnClickListener(this);

        doTrustToCertificates();
        CookieHandler.setDefault(new CookieManager());
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.btn_Login) {
            String uname = txt_UserName.getText().toString();
            String upassword = txt_UserPW.getText().toString();

            connect( uname, upassword);
        //Intent intent = new Intent(getBaseContext(), com.example.calendartest.Calendar.class);

        //intent.putStringArrayListExtra("EventName",eventname);
        }
    }

    public String ReadBufferedHTML(BufferedReader reader, char [] htmlBuffer, int bufSz) throws java.io.IOException {
        htmlBuffer[0] = '\0';
        int offset = 0;
        do {
            int cnt = reader.read(htmlBuffer, offset, bufSz - offset);
            if (cnt > 0) {
                offset += cnt;
            } else {
                break;
            }
        } while (true);
        return new String(htmlBuffer);
    }

    // generate keyid of POST data to hku portal
    public String keyid(){
        Calendar c1 = Calendar.getInstance();
        String time = String.valueOf(c1.get(Calendar.YEAR)) + String.valueOf(c1.get(Calendar.MONTH))
                + String.valueOf(c1.get(Calendar.DATE)) + String.valueOf(c1.get(Calendar.HOUR))
                + String.valueOf(c1.get(Calendar.MINUTE)) + String.valueOf(c1.get(Calendar.SECOND));
        return time;
    }

    public String getHTML( String inputurl ){
        String urlString = "";
        try
        {
            URL url = new URL(inputurl);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = null;
            if(urlConnection instanceof HttpURLConnection)
            {
                connection = (HttpURLConnection) urlConnection;
            }
            else
            {
                System.out.println("URL not found");
                return "URL not found";
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String current;
            while((current = in.readLine()) != null)
            {
                urlString += current;
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }
        return urlString;
    }

    public String getMoodleFirstPage( String userName, String userPW ) {
        HttpsURLConnection conn_portal = null;
        URLConnection conn_moodle = null;

        final int HTML_BUFFER_SIZE = 2*1024*1024;
        char htmlBuffer[] = new char[HTML_BUFFER_SIZE];

        final int HTTPCONNECTION_TYPE = 0;
        final int HTTPSCONNECTION_TYPE = 1;
        int moodle_conn_type = HTTPCONNECTION_TYPE;


        //String [][] answer = new String [100][];
        //answer[0][0] = "a";
        try {
            /////////////////////////////////// HKU portal //////////////////////////////////////
//            URL url_portal = new
//                    URL("https://hkuportal.hku.hk/cas/login?service=http://moodle.hku.hk/login/index.php?authCAS=CAS&username="
//                    + userName + "&password=" + userPW);
            URL url_portal = new
                    URL("https://hkuportal.hku.hk/cas/servlet/edu.yale.its.tp.cas.servlet.Login");

            conn_portal = (HttpsURLConnection) url_portal.openConnection();

            String urlParameters  = "keyid=" + keyid() + "&service=https://moodle.hku.hk/login/index.php?authCAS=CAS&username="
                    + userName + "&password=" + userPW + "&x=38&y=26";
            byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
            int postDataLength = postData.length;
            conn_portal.setDoOutput(true);
            conn_portal.setInstanceFollowRedirects(false);
            conn_portal.setRequestMethod("POST");
            conn_portal.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn_portal.setRequestProperty("charset", "utf-8");
            conn_portal.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
            conn_portal.setUseCaches(false);
            try(DataOutputStream wr = new DataOutputStream(conn_portal.getOutputStream())) {
                wr.write( postData );
            }

            BufferedReader reader_portal = new BufferedReader(new InputStreamReader(conn_portal.getInputStream()));
            String HTMLSource = ReadBufferedHTML(reader_portal, htmlBuffer, HTML_BUFFER_SIZE);

            int ticketIDStartPosition = HTMLSource.indexOf("ticket=") + 7;
            String ticketID = HTMLSource.substring(ticketIDStartPosition, HTMLSource.indexOf("\";", ticketIDStartPosition));
            reader_portal.close();
            /////////////////////////////////// HKU portal //////////////////////////////////////

            /////////////////////////////////// Moodle //////////////////////////////////////
//            URL url_moodle = new URL("http://moodle.hku.hk/login/index.php?authCAS=CAS&ticket=" + ticketID);
            URL url_moodle = new URL("https://moodle.hku.hk/login/index.php?authCAS=CAS&ticket=" + ticketID);
            conn_moodle =  url_moodle.openConnection();
            ((HttpURLConnection)conn_moodle).setInstanceFollowRedirects(true);

            BufferedReader reader_moodle = new BufferedReader(new InputStreamReader(conn_moodle.getInputStream()));

            /// handling redirects to HTTPS protocol
            while(true) {
                String redirect_moodle = conn_moodle.getHeaderField("Location");
                if (redirect_moodle != null) {
                    URL new_url_moodle = new URL(url_moodle, redirect_moodle);
                    if(moodle_conn_type == HTTPCONNECTION_TYPE) {
                        ((HttpURLConnection) conn_moodle).disconnect();
                    } else {
                        ((HttpsURLConnection) conn_moodle).disconnect();
                    }
                    conn_moodle =  new_url_moodle.openConnection();
                    if(new_url_moodle.getProtocol().equals("http")) {
                        moodle_conn_type = HTTPCONNECTION_TYPE;
                        ((HttpURLConnection)conn_moodle).setInstanceFollowRedirects(true);
                    } else {
                        moodle_conn_type = HTTPSCONNECTION_TYPE;
                        ((HttpsURLConnection)conn_moodle).setInstanceFollowRedirects(true);
                    }

                    url_moodle = new_url_moodle;

                    //String cookie = conn_moodle.getHeaderField("Set-Cookie");
                    //if (cookie != null) {
                    //    conn_moodle2.setRequestProperty("Cookie", cookie);
                    //}
                    reader_moodle = new BufferedReader(new InputStreamReader(conn_moodle.getInputStream()));
                } else {
                    break;
                }
            }

            HTMLSource = ReadBufferedHTML(reader_moodle, htmlBuffer, HTML_BUFFER_SIZE);

            //data extraction
            Pattern p_calendar = Pattern.compile("<a href=\"(.*?)\" title=\"This month\">(.*?)</a>");
            Matcher m_calendar = p_calendar.matcher(HTMLSource);
            System.out.println("start_test_for_url");
            System.out.println(m_calendar.groupCount());
            if(m_calendar.find()) {
                String ans = m_calendar.group(1);
                String month = m_calendar.group(2);
                System.out.println(m_calendar.group(1));
                String urlString = getHTML(m_calendar.group(1));

                //find view-day-link
                Pattern p_day_link = Pattern.compile("<a data-action=\"view-day-link\" href=\"(.*?)\" class=\"day\" title=\"\">(.*?)</a>");
                Matcher m_day_link = p_day_link.matcher(urlString);
                Integer flag = 1;
                while(m_day_link.find()){
                    if(flag == 1){
                        String day_link = m_day_link.group(1);
                        day_link = day_link.replace("amp;", "");
                        String day = m_day_link.group(2);
                        System.out.println(day + day_link);

                        //go to specific day
                        String dayString = getHTML(day_link);

                        Pattern p_event = Pattern.compile("<h3 class=\"name d-inline-block\">(.*?)</h3>\\s(.*?)<span class.*?href.*?>(.*?)</a>(.*?)</span>");
                        Matcher m_event = p_event.matcher(dayString);
                        //answer[0][0] = m_event.group(1);

                        //把获取到的参数添加到eventlist
                        while(m_event.find()){
                            System.out.println("got an event");
                            System.out.println("Event name is: " + m_event.group(1));
                            System.out.println("Event date is: " + m_event.group(3));
                            System.out.println("Event time is: " + m_event.group(4));


                            eventname.add(m_event.group(1));
                            eventdate.add(m_event.group(3));
                            eventtime.add(m_event.group(4));

                        }

                    }
                    flag = 1 - flag;
                }
            }
            System.out.println("end_test_for_url");

            reader_moodle.close();
            return HTMLSource;
            /////////////////////////////////// Moodle //////////////////////////////////////

        } catch(Exception e) {
            return "Error";
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            if(conn_portal != null){
                conn_portal.disconnect();
            }
            if(conn_moodle != null){
                if(moodle_conn_type == HTTPCONNECTION_TYPE) {
                    ((HttpURLConnection) conn_moodle).disconnect();
                } else {
                    ((HttpsURLConnection) conn_moodle).disconnect();
                }
            }
        }
    }

    // trusting all certificate
    public void doTrustToCertificates() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers()
                    {
                        return null;
                    }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                    {
                    }
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                    {
                    }
                }
        };

        try {
            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void alert(String title, String mymessage){
        new AlertDialog.Builder(this)
                .setMessage(mymessage)
                .setTitle(title)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton){}
                        }
                )
                .show();
    }

    public void parse_HTML_Source_and_Switch_Activity( String HTMLsource ){

        Intent intent = new Intent(getBaseContext(), com.example.calendartest.Calendar.class);

        intent.putStringArrayListExtra("EventDate", eventdate);
        intent.putStringArrayListExtra("EventTime", eventtime);
        intent.putStringArrayListExtra("EventName", eventname);
        startActivity(intent);
    }

    public void connect( final String userName, final String userPW ){
        final ProgressDialog pdialog = new ProgressDialog(this);

        pdialog.setCancelable(false);
        pdialog.setMessage("Logging in ...");
        pdialog.show();

        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            boolean success;
            String moodlePageContent;

            @Override
            protected String doInBackground(String... arg0) {
                // TODO Auto-generated method stub
                success = true;
                moodlePageContent = getMoodleFirstPage(userName, userPW);

                if( moodlePageContent.equals("Fail to login") )
                    success = false;

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (success) {
                    parse_HTML_Source_and_Switch_Activity( moodlePageContent );
                } else {
                    alert( "Error", "Fail to login" );
                }
                pdialog.hide();
            }

        }.execute("");
    }
}