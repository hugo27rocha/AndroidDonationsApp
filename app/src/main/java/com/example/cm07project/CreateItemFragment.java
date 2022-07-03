package com.example.cm07project;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class CreateItemFragment extends Fragment {
    private EditText mtitle;
    private EditText mdesc;
    private EditText mquant;
    private String category;
    private Spinner spinner;
    private Button create;
    private Button gallery;
    private Button photo;
    private DatabaseReference reference;
    private FirebaseUser user;
    private String userid;
    // Uri indicates, where the image will be picked from
    private Uri filePath;
    private ImageView imageView;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private String id;
    Bitmap photoTaken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_create_item, null);

        spinner = (Spinner) root.findViewById(R.id.category_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        reference = FirebaseDatabase.getInstance().getReference("Products");


        create = root.findViewById(R.id.createItemButton);
        photo = root.findViewById(R.id.takephoto);
        gallery = root.findViewById(R.id.btnChoose);
        imageView = root.findViewById(R.id.imgView);

        mtitle = root.findViewById(R.id.itemtitleedit);
        mdesc = root.findViewById(R.id.itemdescedit);
        mquant = root.findViewById(R.id.itemquant);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createItem();

            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},2000);
                }
                else {
                    takePicture();
                }

            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2000);
                }
                else {
                    startGallery();
                }
            }
        });


        return  root;


    }

    private void takePicture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    private void startGallery() {
        // Defining Implicit Intent to mobile gallery
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 1000);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super method removed
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000) {
                Uri returnUri = data.getData();
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bitmapImage);
                filePath = returnUri;
                Log.i("pathhAA",returnUri.toString());
            }
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
           /* Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);*/
            Bundle extras = data.getExtras();
            photoTaken = (Bitmap) extras.get("data");
            imageView.setImageBitmap(photoTaken);
            saveImage(photoTaken);

        }
    }

    private void saveImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("erro","Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
            filePath = Uri.fromFile(pictureFile);
            Log.i("bom diaaaaa",filePath.getPath().toString());
        } catch (FileNotFoundException e) {
            Log.d("erro", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("erro", "Error accessing file: " + e.getMessage());
        }
    }
    public String UploadImage() {

        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading Item...");
            progressDialog.show();

            String photoid = UUID.randomUUID().toString();

            StorageReference ref = storageReference.child("images/" + photoid);

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                           // databaseReference.child("imageid").setValue(imageUploadInfo);
                        }
                    });
            return photoid;
        }
        else {

            Toast.makeText(getContext(), "Foto obrigatória", Toast.LENGTH_LONG).show();
            return "";
        }


    }

    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.i("bom dia", mediaStorageDir.getAbsolutePath());
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            Log.i("bom", "n existe");
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
            Log.i("bom", "criado");

        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        Log.i("pathhh",mediaFile.getAbsolutePath());
        return mediaFile;
    }

    private void createItem() {
        String title = mtitle.getText().toString();
        String desc = mdesc.getText().toString();
        category = spinner.getSelectedItem().toString();

        if(title.isEmpty()) {
            Toast.makeText(getActivity(), "Título obrigatório", Toast.LENGTH_SHORT).show();

        } else if(desc.isEmpty()) {
            Toast.makeText(getActivity(), "Descrição obrigatória", Toast.LENGTH_SHORT).show();

        } else if(category.equals("Selecionar...")) {
            Toast.makeText(getActivity(), "Categoria obrigatória", Toast.LENGTH_SHORT).show();

        } else if(mquant.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Descrição obrigatória", Toast.LENGTH_SHORT).show();

        } else if (filePath == null){

            Toast.makeText(getActivity(), "Foto obrigatória", Toast.LENGTH_SHORT).show();
        } else {
            int quant = Integer.parseInt(mquant.getText().toString());
            id = UUID.randomUUID().toString();

            String idphoto = UploadImage();

            if (idphoto.isEmpty()) {
                Toast.makeText(getActivity(), "Upload Failed", Toast.LENGTH_SHORT).show();
            } else {
                Products itemP = new Products(id, userid, title,desc, category,quant,idphoto);

                FirebaseDatabase.getInstance().getReference("Products")
                        .push()
                        .setValue(itemP).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();

                            ProfileFragment llf = new ProfileFragment();

                            ft.replace(R.id.container, llf);
                            ft.addToBackStack("tag");
                            ft.commit();

                        }else {
                            Toast.makeText(getActivity(), "Item Failed:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }



        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == 2000)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent cameraIntent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (cameraIntent2.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(cameraIntent2, 1000);
                }
            }
            else
            {
                Toast.makeText(getContext(), "gallery permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }



}