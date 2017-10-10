package parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Class principale du programme
 * 
 * @author Rémi BUTET
 *
 */
public class Main {
		
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Il n'y a pas le chemin du fichier !");
			return;
		}

		File file = new File(args[0]);
		if (!file.exists() || !file.isFile()) {
			System.err.println("Le fichier '" + args[0] + "' n'existe pas !");
			return;
		}
		
		Image image = new Image(file);
		
		try {
			image.decode();
			System.out.println(image.toString());
			image.affiche();
		} catch (MessageException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Permet de créer un fichier avec la lettre R
	 */
	@SuppressWarnings("unused")
	private static void create() {
		byte[] commentaire = ("Lettre R").getBytes();
		
		boolean X = false;
		boolean O = true;
		byte[] R = toBytes(
			X,X,X,X,X,O,
			X,O,O,O,O,X,
			X,O,O,O,O,X,
			X,O,O,O,O,X,
			X,X,X,X,X,O,
			X,X,O,O,O,O,
			X,O,X,O,O,O,
			X,O,O,X,O,O,
			X,O,O,O,X,O,
			X,O,O,O,O,X
		);
		
		File file = new File("Lettre_R.mp");
		FileOutputStream input = null;
		try {
			
			input = new FileOutputStream(file);
			
			// Entete
			input.write(Image.HEADER.getBytes());
			
			// Bloc Header
			input.write(BlocTypes.HEADER.array());
			input.write(ByteBuffer.allocate(4).putInt(9).array());
			input.write(ByteBuffer.allocate(4).putInt(6).array());
			input.write(ByteBuffer.allocate(4).putInt(10).array());
			input.write(PixelTypes.NOIR_ET_BLANC.array());
			
			// Bloc Commentaire
			input.write(BlocTypes.COMMENTAIRE.array());
			input.write(ByteBuffer.allocate(4).putInt(commentaire.length).array());
			input.write(commentaire);
			
			// Bloc Donnees
			input.write(BlocTypes.DONNEES.array());
			input.write(ByteBuffer.allocate(4).putInt(R.length).array());
			input.write(R);
			
		} catch (IOException e) {
			System.out.println("Problème lors de la l'écriture du fichier");
			e.printStackTrace();
		} finally {
			try { if (input != null) input.close(); } catch (IOException e) {}
		}
	}
	
	private static byte[] toBytes(boolean... bools) {
		int length = bools.length + (bools.length % 8);
		BitSet bits = new BitSet(length);
		for (int cpt=0; cpt<bools.length; cpt++) {
			bits.set(cpt, bools[cpt]);
		}
		
		byte[] bytes = bits.toByteArray();
		if (length / 8 == bytes.length) {
			return bytes;
		} else {
			return Arrays.copyOf(bytes, length/8);
		}
	}
}
