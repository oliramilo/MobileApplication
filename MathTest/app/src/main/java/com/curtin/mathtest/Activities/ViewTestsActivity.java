package com.curtin.mathtest.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.curtin.mathtest.Database.TestHandler;
import com.curtin.mathtest.Database.UserHandler;
import com.curtin.mathtest.Model.TestSubmission;
import com.curtin.mathtest.R;

import java.util.List;

public class ViewTestsActivity extends AppCompatActivity{

    private RecyclerView rv;
    private LinearLayout ly;
    private UserHandler userHandler;
    private TestHandler testHandler;
    private List<TestSubmission> submissionList;
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tests);
        this.ctx = getApplicationContext();
        userHandler = new UserHandler(ctx);
        testHandler = new TestHandler(ctx);
        Intent intent = getIntent();
        String contact = intent.getStringExtra("CONTACT");
        submissionList = testHandler.getTestSubmissionsFrom(contact);
        setRecyclerViewLayout();
    }


    private void setRecyclerViewLayout() {
        rv = (RecyclerView) findViewById(R.id.testRecyclerView);
        TestAdapter adapter = new TestAdapter();
        rv.setAdapter(adapter);
        LinearLayoutManager rvLayout = new LinearLayoutManager(ctx);
        rv.setLayoutManager(rvLayout);
    }


    private class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {

        @NonNull
        @Override
        public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TestViewHolder vHolder = new TestViewHolder(LayoutInflater.from(ctx),parent);
            return vHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
            holder.bind(submissionList.get(position));
        }

        @Override
        public int getItemCount() {
            return submissionList.size();
        }

        private class TestViewHolder extends RecyclerView.ViewHolder  {
            private TextView grade;
            private TextView startDate;
            private TextView timeStarted;
            private TextView completionTime;
            private TestSubmission submission;
            public TestViewHolder(LayoutInflater li,ViewGroup parent) {
                super(li.inflate(R.layout.list_test_entry,parent,false));
                grade = (TextView)  itemView.findViewById(R.id.testGrade);
                startDate = (TextView) itemView.findViewById(R.id.testDateStarted);
                completionTime = (TextView) itemView.findViewById(R.id.testTimeCompletion);
                timeStarted = (TextView) itemView.findViewById(R.id.testTimeStarted);
            }

            public void bind(TestSubmission submission) {
                this.submission = submission;
                grade.setText("Score: "+ submission.getScore());
                startDate.setText("Date started: " + submission.getDateStarted());
                timeStarted.setText("Time started: " + submission.getTimeStarted());
                completionTime.setText("Completion time: " + submission.getTestDuration());
            }
        }
    }
}