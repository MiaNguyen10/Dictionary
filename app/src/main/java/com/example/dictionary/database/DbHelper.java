package com.example.dictionary.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dictionary.Question;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {

    private Context mycontext;
    public static final String TABLE_NAME="Question";
    public static final String TABLE_ID="id";
    public static final String TABLE_QUESTION="Question";
    public static final String TABLE_CASE_A="CaseA";
    public static final String TABLE_CASE_B="CaseB";
    public static final String TABLE_CASE_C="CaseC";
    public static final String TABLE_TRUE_CASE="TrueCase";

    //private String DB_PATH = mycontext.getApplicationContext().getPackageName()+"/databases/";
    private static String DB_NAME = "dict.db";//the extension may be .sqlite or .db
    public SQLiteDatabase myDataBase;
    // public static final String DB_PATH= Environment.getDataDirectory()+
    //   "/data/com.example.dictionary.database/databases/";
    private String DB_PATH = "";

    public DbHelper(Context context) {
        super(context,DB_NAME,null,1);
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mycontext = context;
    }

    public void createdatabase() throws IOException {
        boolean dbexist = checkdatabase();
        myDataBase = null;
        if(dbexist) {
            System.out.println(" Database exists.");
        } else {
            myDataBase = this.getReadableDatabase();
            myDataBase.close();
            try {
                copydatabase();
            } catch(IOException e) {
                throw new Error("Error copying database: " + e);
            }
        }
    }

    private boolean checkdatabase() {
        //SQLiteDatabase checkdb = null;
        boolean checkdb = false;
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbfile = new File(myPath);
            //checkdb = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
            checkdb = dbfile.exists();
        } catch(SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void copydatabase() throws IOException {
        //Open your local db as the input stream
        InputStream myinput = mycontext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outfilename = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myoutput = new FileOutputStream(outfilename);

        // transfer byte to inputfile to outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myinput.read(buffer))>0) {
            myoutput.write(buffer,0,length);
        }

        //Close the streams
        myoutput.flush();
        myoutput.close();
        myinput.close();
    }

    public void opendatabase() throws SQLException {
        //Open the database
        String mypath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close() {
        if(myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion>oldVersion) {
            try {
                copydatabase();
            } catch (IOException e) {
            }
        }
    }

    public ArrayList<Question> getAllQuestion(int number){
        ArrayList<Question> questionList = new ArrayList<Question>(number);
        myDataBase= this.getReadableDatabase();
        Cursor c= myDataBase.rawQuery("SELECT * FROM practice",null);

        if (c.moveToFirst()){
            do {
                Question question= new Question();
                question.setCauhoi(c.getString(c.getColumnIndex("question")));
                question.setCaseA(c.getString(c.getColumnIndex("caseA")));
                question.setCaseB(c.getString(c.getColumnIndex("caseB")));
                question.setCaseC(c.getString(c.getColumnIndex("caseC")));
                question.setTrueCase(c.getInt(c.getColumnIndex("true_case")));
                questionList.add(question);
            }while (c.moveToNext());
        }
        c.close();
        return questionList;
    }


}
