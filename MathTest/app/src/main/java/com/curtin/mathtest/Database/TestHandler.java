package com.curtin.mathtest.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.curtin.mathtest.Database.Schema.TestSchema;
import com.curtin.mathtest.Model.TestSubmission;

import java.util.ArrayList;
import java.util.List;

public class TestHandler  {
    private SQLiteDatabase db;
    private Context ctx;
    public TestHandler(Context ctx) {
        this.ctx = ctx;
        load();
    }

    public void load() {
        db = new DatabaseHandler(ctx.getApplicationContext()).getReadableDatabase();
    }

    public void addTestSubmission(TestSubmission submission) {
        String userContact = submission.getUser();
        String dateSubmitted = submission.getDateStarted();
        String timeStarted = submission.getTimeStarted();
        String timeSpent = submission.getTestDuration();
        int score = submission.getScore();
        ContentValues cv = new ContentValues();
        cv.put(TestSchema.TestTable.Cols.USER,userContact);
        cv.put(TestSchema.TestTable.Cols.DATE_SUBMITTED, dateSubmitted);
        cv.put(TestSchema.TestTable.Cols.TIME_STARTED, timeStarted);
        cv.put(TestSchema.TestTable.Cols.TIME_SPENT, timeSpent);
        cv.put(TestSchema.TestTable.Cols.TEST_SCORE, score);
        db.insert(TestSchema.TestTable.TABLE_NAME, null, cv);
    }

    public void removeTestsFromUser(String contact) {
        String command = "DELETE FROM " + TestSchema.TestTable.TABLE_NAME + " WHERE " + TestSchema.TestTable.Cols.USER + " = " + contact + ";";
        String[] whereValue = { String.valueOf(contact)};
        db.delete(TestSchema.TestTable.TABLE_NAME, TestSchema.TestTable.Cols.USER + " = ?", whereValue);
    }

    public List<TestSubmission> getTestSubmissionsFrom(String contact){
        List<TestSubmission> submissions = new ArrayList<>();
        try (TestCursor cursor = new TestCursor(db.query(TestSchema.TestTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TestSubmission submission = cursor.getTestSubmission();
                if(contact.equals(submission.getUser())) {
                    submissions.add(submission);
                }
                cursor.moveToNext();
            }
        }
        return submissions;
    }

    private class TestCursor extends CursorWrapper {

        public TestCursor(Cursor cursor) {
            super(cursor);
        }

        public TestSubmission getTestSubmission() {
            String userContact = getString(getColumnIndex(TestSchema.TestTable.Cols.USER));
            String dateSubmitted = getString(getColumnIndex(TestSchema.TestTable.Cols.DATE_SUBMITTED));
            String timeStarted = getString(getColumnIndex(TestSchema.TestTable.Cols.TIME_STARTED));
            String timeSpent = getString(getColumnIndex(TestSchema.TestTable.Cols.TIME_SPENT));
            int testScore = getInt(getColumnIndex(TestSchema.TestTable.Cols.TEST_SCORE));
            return new TestSubmission(userContact,dateSubmitted,timeStarted,timeSpent,testScore);
        }
    }
}
