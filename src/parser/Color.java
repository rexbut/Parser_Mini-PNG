package parser;

public class Color {
	private final int rouge;
	private final int vert;
	private final int bleu;
	
	public Color(byte rouge, byte vert, byte bleu) {
		this.rouge = unsigned(rouge);
		this.vert = unsigned(vert);
		this.bleu = unsigned(bleu);
	}
	
	public int getRouge() {
		return this.rouge;
	}
	
	public int getVert() {
		return this.vert;
	}
	
	public int getBleu() {
		return this.bleu;
	}
	
	public String toString() {
		return this.getRouge() + " " + this.getVert() + " " + this.getBleu();
	}
	
	/**
	 * Permet d'enlevé le signe d'une valeur
	 * @param value Signed
	 * @return Unsigned
	 */
	public static int unsigned(byte value) {
		return value & 0xFF;
	}
}
