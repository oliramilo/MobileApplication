package com.curtin.mathtest.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.curtin.mathtest.Database.TestHandler;
import com.curtin.mathtest.Database.UserHandler;
import com.curtin.mathtest.Database.UserImageHandler;
import com.curtin.mathtest.Model.AppAlerter;
import com.curtin.mathtest.Model.TestSubmission;
import com.curtin.mathtest.Model.User;
import com.curtin.mathtest.Model.UserImage;
import com.curtin.mathtest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmailerActivity extends AppCompatActivity implements ListItemClickListener {

    private UserHandler userHandler;
    private UserImageHandler userImageHandler;
    private TestHandler testHandler;
    private RecyclerView rv;
    private Context ctx;
    private List<User> users;
    private HashMap<String,String> selectedUsers;
    private AppAlerter alert;
    private Button sendEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailer);
        ctx = getApplicationContext();
        alert = new AppAlerter(ctx);
        selectedUsers = new HashMap<>();
        userHandler = new UserHandler(ctx);
        testHandler = new TestHandler(ctx);
        userImageHandler = new UserImageHandler(ctx);


        users = userHandler.getAllUsersWithEmail();
        rv = (RecyclerView) findViewById(R.id.emailRecyclerView);
        setRecyclerViewLayout();

        sendEmailButton = (Button) findViewById(R.id.sendEmailButton);
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> users = new ArrayList<>(selectedUsers.keySet());
                List<String> emails = new ArrayList<>();
                String mailBody = "";
                String subject = "Test History";
                for(int i=0;i<users.size();i++) {
                    User user = userHandler.getUser(users.get(i));
                    emails.add(user.getEmail());
                    List<TestSubmission> submissions = testHandler.getTestSubmissionsFrom(user.getContact());
                    for(int j=0;j<submissions.size();j++) {
                        mailBody+=submissions.get(j).toString() + "\n";
                    }
                }
                String[] mailto = new String[emails.size()];
                emails.toArray(mailto);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL,mailto);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, mailBody);
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });
    }

    private void setRecyclerViewLayout() {
        UserAdapter adapter = new UserAdapter(this);
        rv.setAdapter(adapter);
        LinearLayoutManager rvLayout = new LinearLayoutManager(ctx);
        rv.setLayoutManager(rvLayout);
    }

    public HashMap<String, Bitmap> mapUserImages() {
        HashMap<String, Bitmap> mappedImages = new HashMap<>();
        List<UserImage> userImages = userImageHandler.getUserImages();
        for(int i=0;i<userImages.size();i++) {
            UserImage data = userImages.get(i);
            mappedImages.put(data.getContact(), data.getUserImage());
        }
        return mappedImages;
    }

    @Override
    public void onListItemClick(int position) {
        User selectedUser = users.get(position);
        if (selectedUser.hasEmail()) {
            selectedUsers.put(selectedUser.getContact(),selectedUser.getEmail());
        }
        else {
            alert.error(" Selected user " + selectedUser.getFullName() + " does not have email set.");
        }
    }

    private class UserAdapter extends RecyclerView.Adapter<EmailerActivity.UserAdapter.UserViewHolder> {
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
            private CheckBox checkBox;
            private User user;
            public UserViewHolder(LayoutInflater li, ViewGroup parent)
            {
                super(li.inflate(R.layout.list_user_email_entry, parent, false));
                userName = (TextView) itemView.findViewById(R.id.emailerUserNameText);
                userImage = (ImageView)  itemView.findViewById(R.id.studentEmailPhotoView);
                checkBox = (CheckBox) itemView.findViewById(R.id.emailCheckBox);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b) {
                            selectedUsers.put(user.getContact(),user.getEmail());
                        }
                        else {

                        }
                    }
                });
            }

            public void bind(User vUser){
                user = vUser;
                userName.setText(user.getFullName());
                //set image profile
                if(user.getContact().equals("0411639250")) {
                    checkBox.setVisibility(View.INVISIBLE);
                }
                if(userImageHandler.hasCustomProfileImage(user.getContact())) {
                    Bitmap profileImage = userImageHandler.getUserProfile(user.getContact());
                    userImage.setImageBitmap(profileImage);
                }
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                mOnClickListener.onListItemClick(position);
            }


        }
    }
}