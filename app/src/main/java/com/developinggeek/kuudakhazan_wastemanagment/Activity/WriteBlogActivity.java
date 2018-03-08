package com.developinggeek.kuudakhazan_wastemanagment.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.developinggeek.kuudakhazan_wastemanagment.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class WriteBlogActivity extends AppCompatActivity
{
    private ImageButton btnAddImage;
    private Button btnSubmit;
    private EditText edt_title , edt_desc , edt_content;
    private Uri mImageUri = null;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase , mUsersDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_blog);

        btnAddImage = (ImageButton)findViewById(R.id.btn_add_image);
        btnSubmit = (Button)findViewById(R.id.btn_submit);
        edt_desc = (EditText)findViewById(R.id.post_description);
        edt_title = (EditText)findViewById(R.id.post_title);
        edt_content = (EditText)findViewById(R.id.post_content);

        mStorage = FirebaseStorage.getInstance().getReference();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = mAuth.getCurrentUser();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , 1);

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startPosting();
            }
        });

    }

    private void startPosting()
    {
        final String title = edt_title.getText().toString().trim();
        final String content = edt_content.getText().toString().trim();

        if(!TextUtils.isEmpty(title) &&!TextUtils.isEmpty(content))
        {
            mProgress.setTitle("Posting to Blog");
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.show();

            if(mImageUri!=null) {
                Log.i("going","image");

                StorageReference filepath = mStorage.child("blog_images").child(mImageUri.getLastPathSegment());
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        @SuppressWarnings("VisibleForTests") final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        final DatabaseReference newPost = mDatabase.push();

                        mUsersDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                newPost.child("title").setValue(title);
                                newPost.child("image").setValue(downloadUrl.toString());
                                newPost.child("uid").setValue(mCurrentUser.getUid());
                                newPost.child("content").setValue(content);
                                newPost.child("username").setValue(dataSnapshot.child("username").getValue());

                                Toast.makeText(WriteBlogActivity.this, "Blog posted", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(WriteBlogActivity.this, BlogActivity.class));

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        mProgress.dismiss();

                    }
                });
            }
            else{
                Log.i("going","no image");

                final DatabaseReference newPost = mDatabase.push();

                mUsersDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        newPost.child("title").setValue(title);
                        newPost.child("image").setValue("none");
                        newPost.child("uid").setValue(mCurrentUser.getUid());
                        newPost.child("content").setValue(content);
                        newPost.child("username").setValue(dataSnapshot.child("username").getValue());

                        Toast.makeText(WriteBlogActivity.this, "Blog posted", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(WriteBlogActivity.this, BlogActivity.class));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mProgress.dismiss();
            }
        }

        else
        {
            Toast.makeText(this, "Enter all the details", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK)
        {
            mImageUri = data.getData();
            btnAddImage.setImageURI(mImageUri);
        }

    }

}