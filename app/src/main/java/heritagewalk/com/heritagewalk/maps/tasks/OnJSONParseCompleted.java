package heritagewalk.com.heritagewalk.maps.tasks;

import heritagewalk.com.heritagewalk.maps.models.Site;

/**
 * OnJSONParseCompleted interface for notifying UI of completed AsyncTask.
 *
 * Created by hamis on 2017-10-12.
 */

public interface OnJSONParseCompleted {
    void onJSONParseCompleted(Site[] sites);
}
