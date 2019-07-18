package com.example.gauth.awsmobileappv1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
*/

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button happy;
    Button sad;
    Button angry;
    Button jealous;
    Button indifferent;
    Button upload;
    Button download;
    String feelings;
    Geocoder geocoder;
    String currentAddress="";
    static String downloadedData="";

    private LocationManager locationManager;
    private LocationListener locationListener;
 //   private OkHttpClient okHttpClient = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        happy = (Button) findViewById(R.id.happy);
        sad = (Button) findViewById(R.id.sad);
        angry = (Button) findViewById(R.id.angry);
        jealous = (Button) findViewById(R.id.jealous);
        indifferent = (Button) findViewById(R.id.indifferent);
        upload = (Button) findViewById(R.id.upload);
        download=(Button)findViewById(R.id.download);
        geocoder= new Geocoder(this,Locale.getDefault());

        happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feelings = happy.getText().toString();
                Toast.makeText(getApplicationContext(), feelings, Toast.LENGTH_SHORT).show();
            }
        });
        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feelings = sad.getText().toString();
                Toast.makeText(getApplicationContext(), feelings, Toast.LENGTH_SHORT).show();
            }
        });
        angry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feelings = angry.getText().toString();
                Toast.makeText(getApplicationContext(), feelings, Toast.LENGTH_SHORT).show();
            }
        });
        jealous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feelings = jealous.getText().toString();
                Toast.makeText(getApplicationContext(), feelings, Toast.LENGTH_SHORT).show();
            }
        });
        indifferent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feelings = indifferent.getText().toString();
                Toast.makeText(getApplicationContext(), feelings, Toast.LENGTH_SHORT).show();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            Toast.makeText(getApplicationContext(),"Download Clicked",Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //DOWNLOAD DATA

                        OkHttpClient client= new OkHttpClient();
                        MediaType JSON=MediaType.parse("application/json;charset=utf-8");
                        JSONObject actualObject=new JSONObject();
                        try
                        {
                            actualObject.put("key2",feelings);
                            actualObject.put("selection","download");
                        }
                        catch (Exception e)
                        {
                            Log.i("Error1 is!!! ",e.toString());
                        }

                        RequestBody body =RequestBody.create(JSON,actualObject.toString());
                        Request newReq =new Request.Builder()
                                .url("https://cf8nudq13a.execute-api.us-east-2.amazonaws.com/postAPIfinal")
                                .post(body)
                                .build();
                        try {
                            Response response= client.newCall(newReq).execute(); // CRITICAL LINE!!!
                            downloadedData=response.body().string();
                            Log.i("Downloaded data !!! ",downloadedData);

                        }
                        catch (Exception e)
                        {
                            Log.i("Download Error is!!! ",e.toString());
                        }
                    }
                }).start();

                Handler handler=new Handler();
                Runnable r=new Runnable() {
                    public void run() {
                        startActivity(new Intent(getBaseContext(), DownloadedData.class));                    }
                };
                handler.postDelayed(r, 5000);

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client= new OkHttpClient();
                    MediaType JSON=MediaType.parse("application/json;charset=utf-8");
                        JSONObject actualObject=new JSONObject();

                     try { //USERNAME
                         actualObject.put("key1", "Shiva1000");
                        //FEELINGS
                         actualObject.put("key2", feelings);

                        //CURRENT ADDRESS
                         actualObject.put("key3", currentAddress);

                        //CURRENT DATE
                         actualObject.put("key4",
                                 LocalDateTime.now().getYear()+"-"
                                         +LocalDateTime.now().getMonth()
                                         +"-"+LocalDateTime.now().getDayOfMonth());
                         //CURRENT TIME
                         actualObject.put("key5", LocalDateTime.now().getHour()+":"
                                 +LocalDateTime.now().getMinute());

                         //SELECT UPLOAD
                         actualObject.put("selection","upload");
                     }
                     catch(Exception e)
                     {
Log.i("Error1 is!!! ",e.toString());
                     }
                     RequestBody body =RequestBody.create(JSON,actualObject.toString());
                     Request newReq =new Request.Builder()
                             .url("https://cf8nudq13a.execute-api.us-east-2.amazonaws.com/postAPIfinal")
                             .post(body)
                             .build();
                    try{

                        String pattern = "\\[addressLines=.*\\],";
                        Pattern r = Pattern.compile(pattern);
                        Matcher m = r.matcher(currentAddress.trim());
                        while(m.find())
                        {
                            currentAddress=m.group().trim().replace("[addressLines=","")
                                          .replace(",", "");
                            Log.i("CurrentAddress is!!! ",currentAddress.trim());
                            Response response= client.newCall(newReq).execute(); // CRITICAL LINE!!!
                            Log.i("Response is!!! ",response.toString());

                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Error2 is" +e,Toast.LENGTH_SHORT).show();
                        Log.i("Error2 is!!! ",e.toString());
                    }
                    }
                }).start();
                }
        });

        //LOCATION RELATED CODE GOES BELOW...
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                try {
                  Toast.makeText(getBaseContext(),
                          geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1).toString(),
                          Toast.LENGTH_SHORT).show();
                  currentAddress=geocoder
                                .getFromLocation(location.getLatitude(), location.getLongitude(),1)
                                .toString();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 3);
            }
         else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       super.onRequestPermissionsResult(requestCode,permissions,grantResults);
         if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED)
                    {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER
                                , 0, 0, locationListener);
                    }

                }
    }
/*
public void uploadData()
{
    StringRequest stringRequest = new StringRequest(Request.Method.POST,
            "https://cf8nudq13a.execute-api.us-east-2.amazonaws.com/postAPIfinal",
            new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        Toast.makeText(getApplicationContext(),"Respones is "+response.toString(),Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Response error: "+response.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getBaseContext(),"Volley error: "+error,Toast.LENGTH_SHORT).show();
                }
            })
    {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String,String> params =new HashMap<>();
            params.put("Content-Type", "application/json; charset=utf-8");
            params.put("key1","aaji92");
            params.put("key2","happy");
            params.put("key3","raleigh");
            params.put("key4","TODAY");
            params.put("key5","Right Now");

            return  params;
        }
    };
    HandleApiRequests.getInstance(this).addToRequestQueue(stringRequest);
}
*/
}


/*
class FeedTask extends AsyncTask<String,Void,String>
{
    private Context mContext;

    public FeedTask(Context context)
    {
        mContext=context;
    }

    @Override
    protected String doInBackground(String... strings) {
       try{
          // String query = URLEncoder.encode("https://cf8nudq13a.execute-api.us-east-2.amazonaws.com/postAPIfinal",
                 //  "utf-8");
OkHttpClient client=new OkHttpClient();
RequestBody postData=new FormBody.Builder()
        .add("key1","Ganesh")
        .add("key2","ganesh93")
        .add("key3","zyx")
        .add("key4","9876")
        .add("key5","NOWW")
        .build();
Request request= new Request.Builder()
        .url("https://cf8nudq13a.execute-api.us-east-2.amazonaws.com/postAPIfinal")
        .post(postData)
        .build();

Response response=client.newCall(request).execute();
String result=response.toString();
return result;

       }
catch (Exception e){
    return e.toString();
}
}

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(mContext,"RESULT IS "+s,Toast.LENGTH_LONG).show();
    }
}
*/