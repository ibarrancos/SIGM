package es.ieci.tecdoc.isicres.api.business.exception;

public class DepartamentoException
extends RuntimeException {
    private static final long serialVersionUID = 1;

    public DepartamentoException() {
    }

    public DepartamentoException(String message) {
        super(message);
    }

    public DepartamentoException(Throwable cause) {
        super(cause);
    }

    public DepartamentoException(String message, Throwable cause) {
        super(message, cause);
    }
}
