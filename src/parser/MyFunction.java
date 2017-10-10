package parser;

/**
 * 
 * Les fonctions utilisé pour gérer les exceptions;
 * 
 * @author Rémi BUTET
 *
 */
public interface MyFunction {

	@FunctionalInterface
	public interface FunctionException<A,R> {

	    R apply(A a) throws MessageException;
	}

	@FunctionalInterface
	public interface BiFunctionException<A,B,R> {

	    R apply(A a, B b) throws MessageException;
	}

}
