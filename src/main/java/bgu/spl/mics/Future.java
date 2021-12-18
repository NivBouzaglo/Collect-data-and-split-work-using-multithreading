package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * <p>
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
    private boolean isDone;
    private T result;

    /**
     * This should be the the only public constructor in this class.
     */
    public Future() {
        //TODO: implement this
        this.isDone = false;
        this.result = null;
    }

    /**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     *
     * @return return the result of type T if it is available, if not wait until it is available.
     * @pre None
     * @inv if(! isDone) wait.
     * @post this.get()==result.
     */

    public T get() {
        //TODO: implement this.
        while (!isDone)
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        return result;
    }

    /**
     * Resolves the result of this Future object.
     *
     * @pre result!=null & this.isDone=false.
     * @post this.result = result.
     */
    public void resolve(T result) {
        //TODO: implement this.
        System.out.println("im resolving "+ result.toString());
        this.result = result;
        isDone = true;
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * @return true if this object has been resolved, false otherwise
     */
    public boolean isDone() {
        //TODO: implement this.
        return isDone;
    }

    /**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     *
     * @param timeout the maximal amount of time units to wait for the result.
     * @param unit    the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not,
     * wait for {@code timeout} TimeUnits {@code unit}. If time has
     * elapsed, return null.
     * @pre
     * @inv
     * @post
     */
    public T get(long timeout, TimeUnit unit) {
        //TODO: implement this.
        if (isDone)
            return result;
        synchronized (this) {
            try {
                unit.wait(timeout);
                if (isDone)
                    return result;
            } catch (InterruptedException ignored) {
            }
        }
        return null;
    }

}