package manager;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

public class ConCheck {
    private static final ReentrantLock borrowingLock = new ReentrantLock();

    public static <T> T lockBorrowing(Callable<T> action) throws Exception {
        borrowingLock.lock();
        try {
            return action.call();
        } finally {
            borrowingLock.unlock();
        }
    }
}

