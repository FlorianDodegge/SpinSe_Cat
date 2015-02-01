package com.example.floriandodegge.spinsecat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by FlorianDodegge on 28.01.15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "catspin.db";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_POSTS = "CREATE TABLE Posts (ID int, name String, description String, link String);";

        db.execSQL(CREATE_TABLE_POSTS);

        db.execSQL("INSERT INTO Posts VALUES (1, 'Meine Empfehlung für euch', 'Nur das Beste für meine Freunde!!', 'http://www.amorelie.at/sexspielzeug/dildos/the-boss-stub-black/');");
        db.execSQL("INSERT INTO Posts VALUES (2, 'Solltet ihr mal spielen :)', 'SpinSeCat - das beste Trinkspiel aller Zeiten', 'http://www.spinsecat.com/');");
        db.execSQL("INSERT INTO Posts VALUES (3, 'Für eure nächste Reise', 'Mein neuer Allrounder', 'http://www.amazon.de/s/ref=nb_sb_noss_1?__mk_de_DE=%C3%85M%C3%85%C5%BD%C3%95%C3%91&url=search-alias%3Dtoys&field-keywords=ORIGINAL%20OstrichPillow%20%C2%AE%20Powernapping%20Kissen%20Strau%C3%9Fen-Kissen%20Ostrich%20Pillow%20Reisekissen');");
        db.execSQL("INSERT INTO Posts VALUES (4, 'Meine Frage an euch', 'Soll ich meinen Mann verleihen?', 'http://forum.gofeminin.de/forum/couple2/__f138670_couple2-Soll-ich-meinen-Mann-ausleihen.html');");
        db.execSQL("INSERT INTO Posts VALUES (5, 'Meine Katzen spielen auch gerne #SpinSeCat', 'Meine Katzen 2.0', 'https://www.youtube.com/watch?v=KZYNap91JWQ');");
        //db.execSQL("INSERT INTO Posts VALUES (6, '', '', '');");
        //db.execSQL("INSERT INTO Posts VALUES (7, '', '', '');");
        //db.execSQL("INSERT INTO Posts VALUES (8, '', '', '');");
        //db.execSQL("INSERT INTO Posts VALUES (9, '', '', '');");
        //db.execSQL("INSERT INTO Posts VALUES (10, '', '', '');");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Posts");
        onCreate(db);
    }

    public  Post getPostById(int Id){
       // SQLiteOpenHelper dbHelper= null;

        SQLiteDatabase db = getReadableDatabase();

        String selectQuery =  "SELECT " +
                Post.KEY_ID + "," +
                Post.KEY_name + "," +
                Post.KEY_description + "," +
                Post.KEY_link +
                " FROM " + Post.TABLE
                + " WHERE " +
                Post.KEY_ID + "="+Id;// It's a good practice to use parameter ?, instead of concatenate string

        Post post = new Post();

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(Id) } );

        if (cursor.moveToFirst()) {
            do {
                post.ID = cursor.getInt(cursor.getColumnIndex(Post.KEY_ID));
                post.name = cursor.getString(cursor.getColumnIndex(Post.KEY_name));
                post.description  = cursor.getString(cursor.getColumnIndex(Post.KEY_description));
                post.link = cursor.getString(cursor.getColumnIndex(Post.KEY_link));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return post;
    }

}
