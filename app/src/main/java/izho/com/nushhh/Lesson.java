package izho.com.nushhh;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class Lesson implements Comparable<Lesson>{

    public enum LessonType {
        Tutorial, Lecture, SectionalTeaching, Laboratory, Unknown;

        static LessonType parse(String abbrev) {
            switch (abbrev) {
                case "SEC":
                    return SectionalTeaching;
                case "TUT":
                    return Tutorial;
                case "LEC":
                    return Lecture;
                case "LAB":
                    return Laboratory;
                default:
                    return Unknown;
            }
        }
    }
    long id;
    String mod;
    Long startTime;
    Long endTime;
    int classNo;
    LessonType lessonType;
    String week;
    String day;


    public Lesson(String name, String mod) {
        String[] lessonTypeAndNoStrings = name.split(":");
        lessonType = LessonType.parse(lessonTypeAndNoStrings[0]);
        classNo = Integer.parseInt(lessonTypeAndNoStrings[1]);
        this.mod = mod;
    }

    public void populateFromJsonObject(JSONObject jsonObject) {
        try {
            week = jsonObject.getString("WeekText");
            day = jsonObject.getString("DayText");
            startTime = Long.parseLong(jsonObject.getString("StartTime"));
            endTime = Long.parseLong(jsonObject.getString("EndTime"));
        } catch (JSONException e) {
            Log.e("Lesson.class", "Error while parsing JSONObject");
        }
    }

    @Override
    public int compareTo(@NonNull Lesson o) {
        return (int) (this.startTime - o.startTime);
    }
}
