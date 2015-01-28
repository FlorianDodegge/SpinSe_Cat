package com.example.floriandodegge.spinsecat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by FlorianDodegge on 28.01.15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 10;
    private static final String DATABASE_NAME = "Meanposts";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Posts (ID int, Posts Text);");

        db.execSQL("INSERT INTO Posts VALUES (1, 'Neues Strandspiel: Kipp jedes mal einen Schnaps, wenn du am Strand jemanden mit Arschgeweih siehst, einen Doppelten, wenn du das Arschgeweih erahnst, bevor du es gesehen hast, und einen Dreifachen auf ein Arschgeweih bei Männern.');");
        db.execSQL("INSERT INTO Posts VALUES (2, 'Ich bin gerade high Auto gefahren und hab angehalten, um einen Tannenzapfen über die Straße zu lassen, weil ich dachte, es ist ein Igel.');");
        db.execSQL("INSERT INTO Posts VALUES (3, 'Man oh man, ich bin neben irgendeinem Kerl aufgewacht. Mein bh fehlt und den Namen des typen weiß ich auch nicht. Er hat aber einen schönen Fernseher.');");
        db.execSQL("INSERT INTO Posts VALUES (4, 'Ich glaube, Gott hat die Sonne erfunden, um mich zu bestrafen, wenn ich zu viel trinke');");
        db.execSQL("INSERT INTO Posts VALUES (5, 'Alter, es brennt! Renn! Nein, warte kurz! Ich muss das noch schnell auf Facebook posten!');");
        db.execSQL("INSERT INTO Posts VALUES (6, 'Das war kein Filmriss, ich wurde geblitzdingst!');");
        db.execSQL("INSERT INTO Posts VALUES (7, 'Die Tür schließt manuell! - Danke, Manuell!');");
        db.execSQL("INSERT INTO Posts VALUES (8, 'Ich kaufe ein A und löse !!BOCKWURST!!');");
        db.execSQL("INSERT INTO Posts VALUES (9, '9 von 10 Stimmen in meinem Kopf sagen ich bin irre. Eine summt.');");
        db.execSQL("INSERT INTO Posts VALUES (10, 'Das Leben ist wie ein Teller Suppe. Nur wenn du heiß bist, wird geblasen.');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS 'Posts'");
        onCreate(db);
    }

    public Cursor getQueryResult (String query)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public void doQuery (String query)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

}
