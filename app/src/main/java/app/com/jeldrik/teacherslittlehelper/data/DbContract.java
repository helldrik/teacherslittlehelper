package app.com.jeldrik.teacherslittlehelper.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jeldrik on 05/02/15.
 */
public class DbContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String AUTHORITY = "app.com.jeldrik.teacherslittlehelper.data";

    // Use AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CLASS_DAY_TITLE_HOUR_ID= BASE_CONTENT_URI.buildUpon().appendPath("CLASS_DAY_TITLE_HOUR_ID").build();

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_STUDENT = "student";
    public static final String PATH_CLASS = "class";
    public static final String PATH_CLASSCONTENT = "classContent";


    /* Inner class that defines the table contents of the student table */
    public static final class StudentEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENT).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + PATH_STUDENT;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + AUTHORITY + "/" + PATH_STUDENT;

        //table name
        public static final String TABLE_NAME="student";
        public static final String COLUMN_STUDENT_NAME="studentName";
        public static final String COLUMN_EMAIL="email";
        public static final String COLUMN_PHONE="phone";
        public static final String COLUMN_FOREIGN_KEY_CLASS="classID";
    }

    /* Inner class that defines the table contents of the class table */
    public static final class ClassEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLASS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + PATH_CLASS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + AUTHORITY + "/" + PATH_CLASS;

        //table name
        public static final String TABLE_NAME="class";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_LOCATION="location";
        public static final String COLUMN_DATE="date";
        public static final String COLUMN_TIME="time";
        public static final String COLUMN_DURATION="duration";
        public static final String COLUMN_LEVEL="level";
        public static final String COLUMN_EXTRA_INFO="extraInfo";
    }

    /* Inner class that defines the table contents of the classContent table */
    public static final class ClassContentEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLASSCONTENT).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + PATH_CLASSCONTENT;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + AUTHORITY + "/" + PATH_CLASSCONTENT;

        //table name
        public static final String TABLE_NAME="classContent";
        public static final String COLUMN_BOOK="book";
        public static final String COLUMN_PAGE="page";
        public static final String COLUMN_INFO="info";
        public static final String COLUMN_FOREIGN_KEY_CLASS="classID";
    }

}
