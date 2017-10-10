package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Optional;

import parser.MyFunction.FunctionException;

/**
 * PixelTypes est la liste de type d'image et contient une fonctionne par type pour afficher l'image
 * 
 * @author Rémi BUTET
 *
 */
public enum PixelTypes {
	NOIR_ET_BLANC(0, image -> {
		byte[] bytes = image.getContenu();
		int nbBytes = (int) Math.ceil((image.getHauteur() * image.getLargeur())/(double)8);
		if (bytes.length != nbBytes) {
			throw new MessageException("Nombre de données : (Contenu : " + bytes.length + " octets; Taille : " + nbBytes + " octets)");
		}
		
		System.out.println("Images : ");
		BitSet bools = BitSet.valueOf(bytes);
		int cpt = 0;
		for (int h = 0; h < image.getHauteur(); h++) {
			String value = "";
			for (int l = 0; l < image.getLargeur(); l++) {
				if (bools.get(cpt)) {
					value += " ";
				} else {
					value += "X";
				}
				cpt++;
			}
			System.out.println(value);
		}
		return true;
	}),
	
	NIVEAUX_DE_GRIS(1, image -> {
		byte[] bytes = image.getContenu();
		if (bytes.length != image.getHauteur() * image.getLargeur()) {
			throw new MessageException("Nombre de données : (Contenu : " + bytes.length + " octets; Taille : " + image.getHauteur() * image.getLargeur() + " octets)");
		}
		
		String name = image.getFilePath().replaceAll(".mp$", "") + ".pgm";
		File file = new File(name);
		if (file.exists()) throw new MessageException("Le fichier '" + name + "' existe !");
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.US_ASCII));
			writer.write("P2");
			writer.newLine();
			
			for (String commentaire : image.getCommentaires()) {
				writer.write(commentaire);
				writer.newLine();
			}
			
			writer.write(image.getLargeur() + " " + image.getHauteur());
			writer.newLine();
			
			writer.write("255");
			writer.newLine();
			
			int cpt = 0;
			for (int h = 0; h < image.getHauteur(); h++) {
				String[] values = new String[image.getLargeur()];
				for (int l = 0; l < image.getLargeur(); l++) {
					values[l] = "" + Color.unsigned(bytes[cpt]);
					cpt++;
				}

				writer.write(String.join(" ", values));
				writer.newLine();
			}
			
			System.out.println("Image : " + file.getPath());
			return true;
		} catch (SecurityException | IOException  e) {
			throw new MessageException("Problème lors de la l'écriture du fichier", e);
		} finally {
			if (writer != null) {try {writer.close();} catch (IOException e) {}}
		}
	}),
	
	PALETTE(2, image -> {		
		byte[] bytes = image.getContenu();
		if (bytes.length != image.getHauteur() * image.getLargeur()) {
			throw new MessageException("Nombre de données : (Contenu : " + bytes.length + " octets; Taille : " + image.getHauteur() * image.getLargeur() + " octets)");
		}
		
		String[] lines = new String[image.getHauteur()];
		int cpt = 0;
		for (int h = 0; h < image.getHauteur(); h++) {
			String[] values = new String[image.getLargeur()];
			for (int l = 0; l < image.getLargeur(); l++) {
				int n = Color.unsigned(bytes[cpt]);
				Optional<Color> color = image.getPallette(n);
				if (!color.isPresent()) {
					throw new MessageException("Couleur inconnu : " + n);
				}
				
				values[l] = color.get().toString();
				cpt++;
			}

			lines[h] = String.join(" ", values);
		}
		
		String name = image.getFilePath().replaceAll(".mp$", "") + ".ppm";
		File file = new File(name);
		if (file.exists()) throw new MessageException("Le fichier '" + name + "' existe !");
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.US_ASCII));
			writer.write("P3");
			writer.newLine();
			
			for (String commentaire : image.getCommentaires()) {
				writer.write(commentaire);
				writer.newLine();
			}
			
			writer.write(image.getLargeur() + " " + image.getHauteur());
			writer.newLine();
			
			writer.write("255");
			writer.newLine();
			
			for (String line : lines) {
				writer.write(line);
				writer.newLine();
			}
			
			System.out.println("Image : " + file.getPath());
			return true;
		} catch (SecurityException | IOException  e) {
			throw new MessageException("Problème lors de la l'écriture du fichier", e);
		} finally {
			if (writer != null) {try {writer.close();} catch (IOException e) {}}
		}
	}),
	
	COULEURS(3, image -> {
		byte[] bytes = image.getContenu();
		if (bytes.length / 3 != image.getHauteur() * image.getLargeur()) {
			throw new MessageException("Nombre de données : (Contenu : " + bytes.length + " octets; Taille : " + image.getHauteur() * image.getLargeur() + " octets)");
		}
		
		String name = image.getFilePath().replaceAll(".mp$", "") + ".ppm";
		File file = new File(name);
		if (file.exists()) throw new MessageException("Le fichier '" + name + "' existe !");
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.US_ASCII));
			writer.write("P3");
			writer.newLine();
			
			for (String commentaire : image.getCommentaires()) {
				writer.write(commentaire);
				writer.newLine();
			}
			
			writer.write(image.getLargeur() + " " + image.getHauteur());
			writer.newLine();
			
			writer.write("255");
			writer.newLine();
			
			int cpt = 0;
			for (int h = 0; h < image.getHauteur(); h++) {
				String[] values = new String[image.getLargeur()];
				for (int l = 0; l < image.getLargeur(); l++) {
					Color color = new Color(bytes[cpt], bytes[cpt+1], bytes[cpt+2]);
					values[l] = color.toString();
					cpt+=3;
				}

				writer.write(String.join(" ", values));
				writer.newLine();
			}
			
			System.out.println("Image : " + file.getPath());
			return true;
		} catch (SecurityException | IOException  e) {
			throw new MessageException("Problème lors de la l'écriture du fichier", e);
		} finally {
			if (writer != null) {try {writer.close();} catch (IOException e) {}}
		}
	});
	
	private final byte value;
	private final FunctionException<Image, Boolean> fun;
	
	PixelTypes (int value, FunctionException<Image, Boolean> fun) {
		this.value = (byte) value;
		this.fun = fun;
	}
	
	public byte getValue() {
		return this.value;
	}
	
	public byte[] array() {
		byte[] array = new byte[1];
		array[0] = this.value;
		return array;
	}
	
	public boolean affiche(Image image) throws MessageException {
		return this.fun.apply(image);
	}
	
	/**
	 * Retourne le PixelTypes
	 * @param value La valeur du type
	 * @return Le PixelType
	 */
	public static Optional<PixelTypes> of(byte value) {
		for (PixelTypes type : PixelTypes.values()) {
			if (type.getValue() == value) {
				return Optional.of(type);
			}
		}
		return Optional.empty();
	}
}
