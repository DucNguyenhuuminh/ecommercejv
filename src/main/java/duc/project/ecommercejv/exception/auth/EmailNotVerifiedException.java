package duc.project.ecommercejv.exception.auth;

public class EmailNotVerifiedException extends RuntimeException{
    public EmailNotVerifiedException(String message) {
        super(message);
    }
}
