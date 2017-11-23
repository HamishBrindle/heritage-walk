package heritagewalk.com.heritagewalk.exception;

/**
 * ResourceNotFoundException indicates there wasn't a proper Override of our BaseActivity's
 * 'getLayout' method in the child Activity. This is required in order for our BaseActivity
 * to know what layout it's loading.
 * Created by hamis on 2017-11-23.
 */

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException()
    {
        super(
                "Cannot find an overridden method of 'getLayout' " +
                        "that returns the current Activity's layout."
        );
    }

}
