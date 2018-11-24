package com.skylan.allinweather;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.skylan.allinweather.db.Province;

import org.litepal.LitePal;

import java.util.List;

public class ProvinceProvider extends ContentProvider {
    private final static int PROVINCE_DIR = 1;
    private final static int PROVINCE_ITEM = 2;
    private static UriMatcher uriMatcher;
    private static final String AUTHRITY = "com.skylan.allinweather.provider";
    private SQLiteDatabase sqLiteDatabase;

    static {

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHRITY , "Province" , PROVINCE_DIR);
        uriMatcher.addURI(AUTHRITY , "Province/#" , PROVINCE_ITEM);
    }

    public ProvinceProvider() {
    }

    @Override
    public boolean onCreate() {
        sqLiteDatabase = new databasesHelp(getContext() , "address.db" ,null , 1 ).getReadableDatabase();
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case PROVINCE_DIR :
                Cursor cursor = sqLiteDatabase.query("Province" , null , null , null , null , null , null);
                return cursor;
            case PROVINCE_ITEM:
                String id = uri.getPathSegments().get(1);
                Cursor cursor1 = sqLiteDatabase.query("Province" , null , "provinceId = ?" , new String[] {id} , null , null , null);
                return cursor1;
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PROVINCE_DIR:
                return "vnd.android.cursor.dir/vnd.com.skylan.allinweather.provider.Province";
            case PROVINCE_ITEM:
                return "vnd.android.cursor.item/vnd.com.skylan.allinweather.provider.Province";
        }
        return null;
    }
    class databasesHelp extends SQLiteOpenHelper {

        public databasesHelp(Context context,  String name,  SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
