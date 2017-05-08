package cz.etn.overview.repo;

/**
 * Exception from the storage.
 * @author Radek Beran
 */
public class RepositoryException extends RuntimeException {

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryException(String message) {
        super(message);
    }
}
