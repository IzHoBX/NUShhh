package izho.com.nushhh;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

public class PopupManager {

    public void showLinkTimetablePopup(final Context context) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(R.layout.prompt_link_nusmods_timetable)
                .setTitle("Please link NUSMODS timetable")
                .setPositiveButton("Done", null)
                .create();
        alertDialog.setButton(-1, "Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input = ((EditText) alertDialog.findViewById(R.id.input)).getText().toString();
                if(input.isEmpty()) {
                    Toast.makeText(context, "Link must not be empty", Toast.LENGTH_SHORT).show();
                    showLinkTimetablePopup(context);
                    return;
                } else {
                    //show loading
                    //fetch timetable from nusmods
                    Singletons.getNusmodsManager().getClassesFromUrl(context, input);
                }
            }
        });
        alertDialog.show();
    }
}
