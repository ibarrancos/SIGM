package es.ieci.tecdoc.isicres.api.business.exception;

public class GrupoUsuarioException
extends RuntimeException {
    private static final long serialVersionUID = 1;

    public GrupoUsuarioException() {
    }

    public GrupoUsuarioException(String message) {
        super(message);
    }

    public GrupoUsuarioException(Throwable cause) {
        super(cause);
    }

    public GrupoUsuarioException(String message, Throwable cause) {
        super(message, cause);
    }
}
