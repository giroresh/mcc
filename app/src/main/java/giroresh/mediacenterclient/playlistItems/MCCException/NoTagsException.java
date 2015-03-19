package giroresh.mediacenterclient.playlistItems.MCCException;

/** For Handling File with no Tags
 * Created by giro on 2015.03.19..
 */
public class NoTagsException extends Exception{
    public NoTagsException(){

    }

    public NoTagsException(String message) {
        super(message);
    }

    public NoTagsException(Throwable cause) {
        super(cause);
    }

    public NoTagsException(String message, Throwable cause) {
        super(message, cause);
    }
}
