package com.mattrobertson.greek.reader.objects;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import java.io.*;

public class DataBaseHelper extends SQLiteOpenHelper
{
	public static final int ADDED_BY_USER = 1;
	public static final int ADDED_BY_WIZARD = 2;
	
    private Context mycontext;

    private String DB_PATH = "/data/data/com.mattrobertson.greek.reader/databases/";
    private static String DB_NAME = "gntlex.sqlite";//the extension may be .sqlite or .db
    public SQLiteDatabase myDataBase;
	
	SharedPreferences prefs;
	
	int curVersion=7, installedVersion=0;
	
    /*private String DB_PATH = "/data/data/"
	 + mycontext.getApplicationContext().getPackageName()
	 + "/databases/";*/

    public DataBaseHelper(Context context) throws IOException {
        super(context,DB_NAME,null,1);
        this.mycontext=context;
		
        boolean dbexist = checkdatabase();
		
		prefs = mycontext.getSharedPreferences("com.mattrobertson.greek.reader.prefs",Context.MODE_PRIVATE);
		
		installedVersion = prefs.getInt("dbVersion",0);
		
        if (dbexist) {
			if (installedVersion < curVersion) {
				replacedatabase();
			}
			else
           		opendatabase(); 
        } else {
            System.out.println("Database doesn't exist");
            createdatabase();
        }
    }

    public void createdatabase() throws IOException {
        boolean dbexist = checkdatabase();
        if(dbexist) {
            //System.out.println(" Database exists.");
        } else {
            this.getReadableDatabase();
            try {
                copydatabase();
            } catch(IOException e) {
                throw new Error("Error copying database: " + e.getMessage());
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


	private void replacedatabase() {
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbfile = new File(myPath);

            if (dbfile.exists())
				dbfile.delete();

			createdatabase();

			prefs.edit().putInt("dbVersion",curVersion).commit();

			//Toast.makeText(mycontext,"Replaced!",Toast.LENGTH_SHORT).show();

        } catch(SQLiteException e) {
			//Toast.makeText(mycontext,"SqlE",Toast.LENGTH_SHORT).show();
            System.out.println("There was a problem deleting the database");
        }
		catch (IOException e) {
			//Toast.makeText(mycontext,"IoE",Toast.LENGTH_SHORT).show();
			System.out.println("There was a problem replacing the database");
		}
    }

    private void copydatabase() throws IOException {
        //Open your local db as the input stream
        InputStream myinput = mycontext.getAssets().open("db/"+DB_NAME);

        // Path to the just created empty db
        String outfilename = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myoutput = new FileOutputStream("/data/data/com.mattrobertson.greek.reader/databases/gntlex.sqlite");

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
	public void onCreate(SQLiteDatabase p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3)
	{
		// TODO: Implement this method
	}
}
