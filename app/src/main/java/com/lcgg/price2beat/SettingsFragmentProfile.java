package com.lcgg.price2beat;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.List;
import java.util.Map;

public class SettingsFragmentProfile extends Fragment {

    public final static int QRcodeWidth = 500 ;
    private FirebaseAuth auth;
    private ImageView iv;
    Bitmap bitmap ;

    FirebaseDatabase database;
    DatabaseReference refUser;

    TextView editName, editEmail, editPoints;
    User user;

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

        editName = (TextView) view.findViewById(R.id.profileName);
        editEmail = (TextView) view.findViewById(R.id.profileEmail);
        editPoints = (TextView) view.findViewById(R.id.points);

        FirebaseUser firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        //Users
        refUser = database.getReference("User").child(auth.getUid());
        refUser.addListenerForSingleValueEvent(valueEventListener);



        iv = view.findViewById(R.id.iv);

        try {
            bitmap = TextToImageEncode(firebaseUser.getUid().toString());
            iv.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return view;
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);

            editName.setText(user.getDisplayName());
            editEmail.setText(user.getEmail());
            editPoints.setText(user.getPoints().toString());
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
