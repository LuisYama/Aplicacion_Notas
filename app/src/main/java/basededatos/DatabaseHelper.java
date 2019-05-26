package basededatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.database.CursorWindowCompat;

import java.util.ArrayList;
import java.util.List;

import bd_modelo.Note;

public class DatabaseHelper extends SQLiteOpenHelper {

    //data base version
    private static final int DATABASE_VERSION=1;
    //DATABASE NAME
    private static final String DATABASE_NAME="notes_db";

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    //creando tablas
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create notes table
        db.execSQL(Note.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Note.TABLE_NAME);

        onCreate(db);
    }

    public long insertNote(String note){
        //obtener la base de datos
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        // id y tmistamp se insertan automaticamente
        values.put(Note.COLUMN_NOTE, note);
        //insert fila
        long id=db.insert(Note.TABLE_NAME,null,values);
        //close db conection
        db.close();
        //devolver el nuevo id
        return id;
    }

    public Note getNote(long id){
        //obtener una bd legible ya no se esta insertando nada
        SQLiteDatabase db=this.getReadableDatabase();

        Cursor cursor=db.query(Note.TABLE_NAME,new String[]{Note.COLUMN_ID, Note.COLUMN_NOTE, Note.COLUMN_TIMESTAMP},Note.COLUMN_ID + "=?",new String[]{String.valueOf(id)},null,null,null,null);

        if(cursor!=null)
            cursor.moveToFirst();
        //prepare note object
        Note note= new Note(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));
        cursor.close();
        return note;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        //selecciona all query
        String selectQuery = "SELECT * FROM " + Note.TABLE_NAME + " ORDER BY " + Note.COLUMN_TIMESTAMP + "DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

                notes.add(note);

            }while (cursor.moveToNext());
        }
        //close conection
        db.close();

        //devolver lista de notas
        return notes;
    }

    public int getNotesCount(){
        String countQuery="SELECT * FROM "+Note.TABLE_NAME;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(countQuery,null);

        int count=cursor.getCount();
        cursor.close();

        return count;
    }

    public int updateNote(Note note){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(Note.COLUMN_NOTE, note.getNote());

        //updating
        return db.update(Note.TABLE_NAME,values,Note.COLUMN_ID +" = ?",new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(Note note){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + "=?",new String[]{String.valueOf(note.getId())});

        db.close();
    }
}