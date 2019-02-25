package izho.com.nushhh;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;

public class SilentJob extends Job {
    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        //prompt to silent phone
        return null;
    }
}
