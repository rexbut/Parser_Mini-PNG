package parser;

/**
 * Exception lancé par mon programme
 * 
 * @author Rémi BUTET
 *
 */
public class MessageException extends Exception {

	private static final long serialVersionUID = -686979467387043753L;

	
	public MessageException(String message) {
		super(message);
	}


	public MessageException(String message, Exception e) {
		super(message, e);
	}
}
