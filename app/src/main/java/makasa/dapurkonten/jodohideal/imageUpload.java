package makasa.dapurkonten.jodohideal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Base64;
import android.util.Log;
import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.Toast;

        import com.android.volley.AuthFailureError;
        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;

        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
        import java.util.Map;

import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.AppController;

public class imageUpload extends AppCompatActivity{
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    sessionmanager session;
    private Button buttonChoose;
    private ImageLoader mImageLoader = AppController.getInstance().getImageLoader();
    private Button buttonUpload;
    private static String INI=imageUpload.class.getSimpleName();
    private ImageView imageView;
    private NetworkImageView nImageView;

    private EditText editTextName;

    private Bitmap bitmap;

    private int PICK_IMAGE_REQUEST = 1;

    private String UPLOAD_URL = AppConfig.urlAPI;

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String fromActivity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageupload);
        session = new sessionmanager(getApplicationContext());

        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        imageView  = (ImageView) findViewById(R.id.imageView);

        Bundle bundle=getIntent().getExtras();
        fromActivity = bundle.getString("fromActivity");
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(final String userID){
        //Showing the progress dialog

        final ProgressDialog loading = ProgressDialog.show(imageUpload.this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(imageUpload.this, "Foto profil kamu berhasil di perbaharui" , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(imageUpload.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("image", image);
                params.put("userid",userID);
                params.put("jodiUploadImg","");

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
        if (fromActivity.equals("Main")){
            Intent m = new Intent(this, MainActivity.class);
            startActivity(m);
        }
        else{
            session.changeValueRegister("upload",1);
            Intent q = new Intent(this, questionsActivity.class);
            startActivity(q);
        }
    }

    private void showFileChooser() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(imageUpload.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("errorimage",e.toString());
        } catch (IOException e) {
            Log.d("errorimages",e.toString());
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri filePath = data.getData();
        try {
            //Getting the Bitmap from Gallery
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            //Setting the Bitmap to ImageView
            //nImageView.setImageBitmap(BitmapFactory.decodeFile(bitmap));
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
}

    public void choose(View v) {

            showFileChooser();
        }
    public void upload(View v) {
        HashMap<String, String> user = session.getUserDetails();
        String userID = user.get(sessionmanager.SES_USER_ID);
        uploadImage(userID);
    }
    public class customImageLoader implements ImageLoader.ImageCache {

        @Override
        public Bitmap getBitmap(String key) {
            if (key.contains("file://")) {
                return BitmapFactory.decodeFile(key.substring(key.indexOf("file://") + 7));
            } else {
                // Here you can add an actual cache
                return null;
            }
        }
        @Override
        public void putBitmap(String key, Bitmap bitmap) {
            // Here you can add an actual cache
        }
    }
}