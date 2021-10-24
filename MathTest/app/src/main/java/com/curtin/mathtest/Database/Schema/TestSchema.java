package com.curtin.mathtest.Database.Schema;

public class TestSchema {

    public static class TestTable {
        public static final String TABLE_NAME = "tests";
        public static class Cols {
            public static final String USER = "user";
            public static final String DATE_SUBMITTED = "date";
            public static final String TIME_STARTED = "time_start";
            public static final String TIME_SPENT = "time";
            public static final String TEST_SCORE = "score";
        }


        public static final String CREATE_TABLE =   "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
                                                    Cols.USER + " TEXT NOT NULL, " + Cols.DATE_SUBMITTED + " TEXT, " +
                                                    Cols.TIME_STARTED + " TEXT, " + Cols.TIME_SPENT + " TEXT, " +
                                                    Cols.TEST_SCORE + " INTEGER );";
    }
}
