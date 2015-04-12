package giroresh.mediacenterclient.SocketAsyncTask;

/** represents the result of the server query
 * it holds the actual data if something gets returned
 * otherwise it gets the exception and in the GUI we may display
 * more meaningful messages
 * Created by giro on 2015.04.06..
 */
public class SocketAsyncTaskResult<T> {
    private T result;
    private Exception error;

    public T getResult() {
        return result;
    }

    public Exception getError() {
        return error;
    }

    public SocketAsyncTaskResult(T result) {
        super();
        this.result = result;
    }

    public SocketAsyncTaskResult(Exception error) {
        super();
        this.error = error;
    }
}