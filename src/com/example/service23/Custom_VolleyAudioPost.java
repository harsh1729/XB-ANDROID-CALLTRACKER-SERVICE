package com.example.service23;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;



public class Custom_VolleyAudioPost extends Request<JSONObject> {

    //private MultipartEntity entity = new MultipartEntity();
	
	MultipartEntityBuilder builder;   
	HttpEntity entity;
    //private static final String FILE_PART_NAME = "file";
   
    //private static final String STRING_PART_NAME = "text";

    private final Response.Listener<JSONObject> mListener;
    //private final File mFilePart;
    
    //private final String mStringPart;
    HashMap< String, String> mapStringParams = null;
    HashMap< String, File> mapFileParams = null;

    public Custom_VolleyAudioPost(String url, HashMap< String, File> mapFileParams, HashMap< String, String> strParams, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        super(Method.POST, url, errorListener);

        mListener = listener;
        this.mapFileParams = mapFileParams;
        //mStringPart = stringPart;
        mapStringParams = strParams;
        builder = MultipartEntityBuilder.create(); 
        buildMultipartEntity();
        entity = builder.build();
       
    }

    private void buildMultipartEntity()
    {
    	
        try
        {
        	if(mapFileParams != null)
        		for(String key : mapFileParams.keySet()){
        			 Log.i("SUSHIL", " File KEy "+key);
        			builder.addPart(key, new FileBody(mapFileParams.get(key)));
        		}
        	
        	if(mapStringParams != null)
        		for(String key : mapStringParams.keySet()){
        			 
        			builder.addPart(key, new StringBody(mapStringParams.get(key) , ContentType.TEXT_PLAIN));
        		}
        		
        }
        catch (Exception e)
        {
            Log.e("SUSHIL","UnsupportedEncodingException");
        }
    }

    @Override
    public String getBodyContentType()
    {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            entity.writeTo(bos);
        }
        catch (IOException e)
        {
        	 Log.e("SUSHIL","IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response)
    {
    	 Log.i("SUSHIL","protected Response<JSONObject> parseNetworkResponse " + response.toString());
    	try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response)
    {
        mListener.onResponse(response);
    }
}