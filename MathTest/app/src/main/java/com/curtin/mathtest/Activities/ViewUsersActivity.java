package com.curtin.mathtest.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.curtin.mathtest.Database.UserHandler;
import com.curtin.mathtest.Database.UserImageHandler;
import com.curtin.mathtest.Model.User;
import com.curtin.mathtest.Model.UserImage;
import com.curtin.mathtest.R;

import java.util.List;
public class ViewUsersActivity extends AppCompatActivity implements ListItemClickListener{

    private RecyclerView rv;
    private LinearLayout ly;
    private List<User> users;
    private List<UserImage> userImages;
    private UserHandler userHandler;
    private UserImageHandler userImageHandler;
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        ctx = getApplicationContext();
        userImageHandler = new UserImageHandler(ctx);
        userHandler = new UserHandler(ctx);
        users = userHandler.getAllUsers();
        setRecyclerViewLayout();
    }


    private void setRecyclerViewLayout() {
        rv = (RecyclerView) findViewById(R.id.userRecyclerView);
        UserAdapter adapter = new UserAdapter(this);
        rv.setAdapter(adapter);
        LinearLayoutManager  rvLayout = new LinearLayoutManager(ctx);
        rv.setLayoutManager(rvLayout);
    }

    @Override
    public void onListItemClick(int position) {
        Intent toSelectedUser = new Intent(ViewUsersActivity.this,SelectedUserActivity.class);
        User user = users.get(position);
        toSelectedUser.putExtra("CONTACT",user.getContact());
        toSelectedUser.putExtra("PASSWORD",user.getPassword());
        startActivity(toSelectedUser);
    }

    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private final ListItemClickListener mOnClickListener;

        public UserAdapter(ListItemClickListener mOnClickListener) {
            this.mOnClickListener = mOnClickListener;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            UserViewHolder vHolder = new UserViewHolder(LayoutInflater.from(ctx), parent);
            return vHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            holder.bind(users.get(position));
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        private class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView userImage;
            private TextView userName;
            private Button viewUser;
            private User user;

            public UserViewHolder(LayoutInflater li, ViewGroup parent)
            {
                super(li.inflate(R.layout.list_user_entry, parent, false));
                userName = (TextView) itemView.findViewById(R.id.userNameText);
                userImage = (ImageView)  itemView.findViewById(R.id.studentPhotoView);
                viewUser = (Button) itemView.findViewById(R.id.deleteUserButton);
            }

            public void bind(User vUser){
                user = vUser;
                userName.setText(user.getFullName());
                //set image profile
                if(userImageHandler.hasCustomProfileImage(user.getContact())) {
                    Bitmap profileImage = userImageHandler.getUserProfile(user.getContact());
                    userImage.setImageBitmap(profileImage);
                }
                viewUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentToSelectedUser = new Intent(ViewUsersActivity.this,SelectedUserActivity.class);
                        intentToSelectedUser.putExtra("CONTACT",user.getContact());
                        startActivity(intentToSelectedUser);
                    }
                });
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                mOnClickListener.onListItemClick(position);
            }
        }
    }


}