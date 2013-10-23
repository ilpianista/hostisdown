/*
  MIT license:

        Copyright (c) 2013 Andrea Scarpino

        Permission is hereby granted, free of charge, to any person obtaining
        a copy of this software and associated documentation files (the
        "Software"), to deal in the Software without restriction, including
        without limitation the rights to use, copy, modify, merge, publish,
        distribute, sublicense, and/or sell copies of the Software, and to
        permit persons to whom the Software is furnished to do so, subject to
        the following conditions:

        The above copyright notice and this permission notice shall be
        included in all copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
        EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
        MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
        NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
        LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
        OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
        WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package it.andreascarpino.hostisdown.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class HostsDataSource {

    private SQLiteDatabase database;
    private HostsOpenHelper dbHelper;
    private String[] allColumns = { HostsOpenHelper.COLUMN_NAME,
            HostsOpenHelper.COLUMN_DATE, HostsOpenHelper.COLUMN_STATUS };

    public HostsDataSource(Context context) {
        dbHelper = new HostsOpenHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Host createHost(String name, Long date, State status) {
        Host host = getHost(name);

        if (host != null) {
            host = updateHost(name, date);
        } else {
            ContentValues values = new ContentValues();
            values.put(HostsOpenHelper.COLUMN_NAME, name);
            values.put(HostsOpenHelper.COLUMN_DATE, date);
            values.put(HostsOpenHelper.COLUMN_STATUS, status.toString());

            long rowId = database.insert(HostsOpenHelper.HOSTS_TABLE_NAME, null,
                    values);
            Cursor cursor = database.query(HostsOpenHelper.HOSTS_TABLE_NAME,
                    allColumns, "rowid = ?", new String[]{ Long.toString(rowId) },
                    null, null, null, null);
            cursor.moveToFirst();
            host = cursorToHost(cursor);
            cursor.close();
        }

        return host;
    }

    public List<Host> getAllHosts() {
        List<Host> hosts = new ArrayList<Host>();

        Cursor cursor = database.query(HostsOpenHelper.HOSTS_TABLE_NAME,
                allColumns, null, null, null, null,
                HostsOpenHelper.COLUMN_DATE + " DESC");

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            hosts.add(cursorToHost(cursor));
        }
        cursor.close();

        return hosts;
    }

    public Host getHost(String name) {
        Host host = null;

        Cursor cursor = database.query(HostsOpenHelper.HOSTS_TABLE_NAME,
                allColumns, HostsOpenHelper.COLUMN_NAME + " = ?",
                new String[]{ name }, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            host = cursorToHost(cursor);
        }
        cursor.close();

        return host;
    }

    public Host updateHost(String name, Long date) {
        ContentValues values = new ContentValues();
        values.put(HostsOpenHelper.COLUMN_DATE, date);

        database.update(HostsOpenHelper.HOSTS_TABLE_NAME, values,
                HostsOpenHelper.COLUMN_NAME + " = ?", new String[]{ name });

        return getHost(name);
    }

    private Host cursorToHost(Cursor cursor) {
        Host host = new Host();

        host.setName(cursor.getString(0));
        host.setDate(cursor.getLong(1));
        if (cursor.getString(2).equals("Down")) {
            host.setStatus(State.Down);
        } else {
            host.setStatus(State.Up);
        }

        return host;
    }

    public void clearHosts() {
        database.delete(HostsOpenHelper.HOSTS_TABLE_NAME, null, null);
    }
}
