package com.lcgg.price2beat;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.internal.WebDialog;
import com.google.android.gms.common.api.ResultTransform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.squareup.picasso.Picasso;

import java.security.AuthProvider;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;

public class SettingsFragmentProfile extends Fragment {

    private FirebaseAuth auth;
    private static final int PICK_IMAGE_REQUEST = 1;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser firebaseUser;

    FirebaseDatabase database;
    DatabaseReference refUser, refPoints;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    TextView editName, editEmail, editPoints;
    EditText updateFirst, updateMiddle, updateLast;
    ImageView imageFirebase;

    String eText, fileUrl;

    LinearLayout displayLinear, updateLinear;

    Button btnUpdate, btnEdit, btnCancel;
    User user;
    AccessToken accessToken;

    private  Uri mImageUri;

    public SettingsFragmentProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_settings_profile, container, false);

        imageFirebase = (ImageView) view.findViewById(R.id.imageFirebase);
        imageFirebase.setOnClickListener(profileImageListener);

        storageRef.child(auth.getUid() + "/profile/profilepic.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                if(uri != null){

                    Picasso.get()
                            .load(uri)
                            .resize(150,150)
                            .centerInside()
                            .into(imageFirebase);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageFirebase.setImageResource(R.drawable.ic_person_black_24dp);
            }
        });

        updateLinear = (LinearLayout) view.findViewById(R.id.updateLinear);
        displayLinear = (LinearLayout) view.findViewById(R.id.displayLinear);

        updateLinear.setVisibility(View.GONE);
        displayLinear.setVisibility(View.VISIBLE);

        editName = (TextView) view.findViewById(R.id.profileName);
        editEmail = (TextView) view.findViewById(R.id.profileEmail);
        editPoints = (TextView) view.findViewById(R.id.points);

        updateFirst = (EditText) view.findViewById(R.id.editProfileFirst);
        updateMiddle = (EditText) view.findViewById(R.id.editProfileMiddle);
        updateLast = (EditText) view.findViewById(R.id.editProfileLast);

        btnUpdate = (Button) view.findViewById(R.id.updateProfile);
        btnEdit = (Button) view.findViewById(R.id.editProfile);
        btnCancel = (Button) view.findViewById(R.id.cancelUpdate);

        firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        accessToken = AccessToken.getCurrentAccessToken();

        //Users
        refUser = database.getReference("User").child(auth.getUid());
        refPoints = database.getReference("Points").child(auth.getUid());
        refUser.addListenerForSingleValueEvent(valueEventListener);

        btnEdit.setVisibility(View.VISIBLE);
        btnUpdate.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);

        btnUpdate.setOnClickListener(updateListener);
        btnEdit.setOnClickListener(updateListenerEdit);
        btnCancel.setOnClickListener(updateListenerCancel);

        refPoints.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double points = dataSnapshot.child("earned").getValue(Double.class);

                editPoints.setText(points.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private View.OnClickListener profileImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openFileChooser();
        }
    };

    private void alertBox(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_alert, null);

        TextView alerTitle = (TextView) dialogView.findViewById(R.id.titleAlert);
        alerTitle.setText(title);

        TextView alert = (TextView) dialogView.findViewById(R.id.txtAlert);
        alert.setText(message);

        builder.setView(dialogView);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                uploadFile();
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openFileChooser() {
        Intent intent =  new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension (Uri uri) {
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return  mine.getExtensionFromMimeType(cr.getType(uri));
    }
    private void uploadFile(){
        if(mImageUri != null){
            final StorageReference fileReference = storageRef.child(auth.getUid() + "/profile/profilepic.jpg");

            fileReference.putFile(mImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;
                                fileUrl = downloadUrl.toString();
                                refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        refUser.child("imageURL").setValue(fileUrl);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }
                });
        }
        else{
            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener updateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            refUser.addListenerForSingleValueEvent(valueEventListenerUpdate);
        }
    };

    private View.OnClickListener updateListenerEdit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateLinear.setVisibility(View.VISIBLE);
            displayLinear.setVisibility(View.GONE);

            btnEdit.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener updateListenerCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateLinear.setVisibility(View.GONE);
            displayLinear.setVisibility(View.VISIBLE);

            btnEdit.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }
    };

    private ValueEventListener valueEventListenerUpdate = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);

            String firstName = updateFirst.getText().toString();
            String middleName = updateMiddle.getText().toString();
            String lastName = updateLast.getText().toString();

            String displayName = firstName + " " + lastName;

            //Set
            refUser.child("displayName").setValue(displayName);
            refUser.child("firstName").setValue(firstName);
            refUser.child("middleName").setValue(middleName);
            refUser.child("lastName").setValue(lastName);

            //Display
            editName.setText(displayName);

            //Change Layouts
            updateLinear.setVisibility(View.GONE);
            displayLinear.setVisibility(View.VISIBLE);

            //Change Buttons
            btnEdit.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);

            editName.setText(user.getDisplayName());
            editEmail.setText(user.getEmail());
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST
                && data != null && data.getData() != null){
            mImageUri = data.getData();

            Picasso.get()
                    .load(mImageUri)
                    .resize(150,150)
                    .centerInside()
                    .into(imageFirebase);

            alertBox("Profile Picture","Successfully change");
        }
    }
}
