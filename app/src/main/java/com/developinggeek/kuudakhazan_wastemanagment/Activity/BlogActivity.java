package com.developinggeek.kuudakhazan_wastemanagment.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.developinggeek.kuudakhazan_wastemanagment.Model.Blog;
import com.developinggeek.kuudakhazan_wastemanagment.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogActivity extends AppCompatActivity
{
    private Button btnWrite;
    private RecyclerView blogList;
    private DatabaseReference mDatabase , mUsersDatabase , mLikeDatabase;
    private FirebaseAuth mAuth;
    private boolean mProcessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        btnWrite = (Button)findViewById(R.id.blog_write);

        blogList = (RecyclerView)findViewById(R.id.blog_list);
        blogList.setLayoutManager(new LinearLayoutManager(this));
        blogList.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mUsersDatabase.keepSynced(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase.keepSynced(true);

        mLikeDatabase = FirebaseDatabase.getInstance().getReference().child("Likes");
        mLikeDatabase.keepSynced(true);

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BlogActivity.this,WriteBlogActivity.class));
            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<Blog , BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>
                (
                        Blog.class ,
                        R.layout.blog_item ,
                        BlogViewHolder.class,
                        mDatabase
                )
        {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, Blog model, int position)
            {
                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext() , model.getImage());
                viewHolder.setUserName(model.getUsername());

                if(!model.getUserImage().equals("none")) {
                    viewHolder.userImg.setVisibility(View.VISIBLE);

                    Picasso.with(getApplicationContext()).load(model.getUserImage()).placeholder(R.drawable.user_default)
                            .into(viewHolder.userImg);
                }

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        /*Intent singleBlogIntent = new Intent(BlogActivity.this , SingleBlogActivity.class);
                        singleBlogIntent.putExtra("blog_id",post_key);
                        startActivity(singleBlogIntent);*/
                    }
                });

                mLikeDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid()))
                        {
                            viewHolder.btnLike.setImageResource(R.drawable.like_blue);
                        }

                        if(dataSnapshot.child(post_key).hasChild("count"))
                        {
                            long likeVal = (long)dataSnapshot.child(post_key).child("count").getValue();
                            viewHolder.likeCount.setText(likeVal + "");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view)
                    {
                        mProcessLike = true;

                        mLikeDatabase.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (mProcessLike) {

                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                        mLikeDatabase.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();

                                        long val = (long) dataSnapshot.child(post_key).child("count").getValue();
                                        val--;
                                        mLikeDatabase.child(post_key).child("count").setValue(val);

                                        mProcessLike = false;

                                        viewHolder.btnLike.setImageResource(R.drawable.like_grey);
                                    } else {
                                        mLikeDatabase.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");

                                        //if the like exists before
                                        if(dataSnapshot.child(post_key).hasChild("count"))
                                        {
                                            long val = (long) dataSnapshot.child(post_key).child("count").getValue();
                                            val++;
                                            mLikeDatabase.child(post_key).child("count").setValue(val);

                                            long showVal = val;
                                            viewHolder.likeCount.setText(showVal+"");
                                        }
                                        else
                                        {
                                            mLikeDatabase.child(post_key).child("count").setValue(1);
                                        }

                                        mProcessLike = false;

                                        viewHolder.btnLike.setImageResource(R.drawable.like_blue);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

            }
        };

        blogList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        ImageButton btnLike;
        TextView likeCount;
        CircleImageView userImg;

        public BlogViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            userImg = (CircleImageView)mView.findViewById(R.id.blog_item_user_img);
            btnLike = (ImageButton)mView.findViewById(R.id.blog_item_btn_like);
            likeCount = (TextView)mView.findViewById(R.id.single_blog_like_count);
        }

        public void setTitle(String title)
        {
            TextView blog_title = (TextView) mView.findViewById(R.id.blog_item_title);
            blog_title.setText(title);
        }

        public void setDesc(String desc)
        {
            TextView blog_desc = (TextView) mView.findViewById(R.id.blog_item_desc);
            blog_desc.setText(desc);
        }

        public void setImage(Context ctx , String imageUrl)
        {
            ImageView blogImg = (ImageView) mView.findViewById(R.id.blog_item_img);
            Picasso.with(ctx).load(imageUrl).placeholder(R.drawable.add_image_icon).into(blogImg);
        }

        public void setUserName(String username)
        {
            TextView blog_username = (TextView) mView.findViewById(R.id.blog_item_user_name);
            blog_username.setText("posted by " + username);
        }

    }

}
