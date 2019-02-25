package izho.com.nushhh;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.evernote.android.job.JobManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;

public class NusmodsManager {
    HashMap<String, LinkedList<Lesson>> lessons;

    public void getClassesFromUrl(final Context context, String url) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.INTERNET}, 1);
        }

        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    parseClassesFromUrl(error.networkResponse.headers.get("Location"));

                    getLessonsTimeFromNusmods(context, queue);

                    scheduleJobAccordingToTimetable(context);

                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(context, "Parse returned url exception", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void scheduleJobAccordingToTimetable(Context context) {
        for(LinkedList<Lesson> lessonsUnderMod: lessons.values()) {
            for(Lesson lesson: lessonsUnderMod) {
                JobManager.create(context).addJobCreator(new NushhhJobCreator());
            }
        }
    }

    private void getLessonsTimeFromNusmods(final Context context, RequestQueue queue) {
        for(final String modName: lessons.keySet()) {
            String url = "https://api.nusmods.com/" + Singletons.getRemoteConfigManager().getAyString() + "/modules/" + modName + ".json";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //extract lessons time
                                //save lessons time
                                JSONArray timetable = new JSONObject(response).getJSONArray("History").getJSONObject(0).getJSONArray("Timetable");

                                for(Lesson lesson: lessons.get(modName)) {
                                    int i;
                                    for(i=0; i<timetable.length(); i++) {
                                        if(timetable.getJSONObject(i).getString("LessonType").equals(lesson.lessonType.toString()) && timetable.getJSONObject(i).getInt("ClassNo") == lesson.classNo) {
                                            lesson.populateFromJsonObject(timetable.getJSONObject(i));
                                            Log.i("lessonsTimeFromNusmods", "populating " + lesson.mod + " " + lesson.lessonType + lesson.classNo);
                                            break;
                                        }
                                    }
                                    if(i==timetable.length()) {
                                        Log.e("lessonsTimeFromNusmods", "couldn't parse time for " + lesson.mod + " " + lesson.lessonType + lesson.classNo);
                                    }
                                }

                            } catch (JSONException e) {
                                Toast.makeText(context, "Exception while parsing module json for lessons time", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
    }

    private void parseClassesFromUrl(String returnedLocationUrl) {
        //https://nusmods.com/timetable/sem-2/share?CS2108=LEC:1,TUT:1&CS3230=LEC:1,TUT:10&CS3235=LEC:1,TUT:3&CS3245=LEC:1,TUT:1&GET1014=LEC:1,TUT:4
        int indexOfFirstQuestionMark = 0;
        //we do not want question mark to be the last character as well
        for(indexOfFirstQuestionMark =0; indexOfFirstQuestionMark<returnedLocationUrl.length()-1; indexOfFirstQuestionMark++) {
            if(returnedLocationUrl.charAt(indexOfFirstQuestionMark) == '?') {
                break;
            }
        }
        if(indexOfFirstQuestionMark==returnedLocationUrl.length()-1) {
            throw new IllegalArgumentException("Cannot locate question mark");
        }
        returnedLocationUrl = returnedLocationUrl.substring(indexOfFirstQuestionMark+1);
        String[] temp = returnedLocationUrl.split("&");
        lessons = new HashMap<>();
        for(String s: temp) {
            String[] temp1 = s.split("=");
            String[] lessonsNameUnderMod = temp1[1].split(",");
            LinkedList<Lesson> lessonUnderMod = new LinkedList<>();
            for(String lessonsName: lessonsNameUnderMod) {
                lessonUnderMod.add(new Lesson(lessonsName, temp1[0]));
            }
            lessons.put(temp1[0], lessonUnderMod);
        }
    }
}
