package izho.com.nushhh;

import android.content.Context;
import android.content.SharedPreferences;

public class TimetableStorage {
    private final String TIMETABLE_ID_KEY = "TIMETABLE_ID";
    private final String INVALID_TIMETABLE_ID = "INVALID";
    private String TIMETABLE_ID;

    public boolean hasLinkedTimetable(Context context) {
        if(TIMETABLE_ID == null) {
            return !getStoredTimetableId(context).equals(INVALID_TIMETABLE_ID);
        } else {
            return !TIMETABLE_ID.equals(INVALID_TIMETABLE_ID);
        }
    }

    private String getStoredTimetableId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("izho.com.nushhh", Context.MODE_PRIVATE);
        return sp.getString(TIMETABLE_ID_KEY, INVALID_TIMETABLE_ID);
    }

    public TimetableStorage(Context context) {
        TIMETABLE_ID = getStoredTimetableId(context);
        if(!hasLinkedTimetable(context)) {
            Singletons.getPopupManager().showLinkTimetablePopup(context);
        }
    }
}
