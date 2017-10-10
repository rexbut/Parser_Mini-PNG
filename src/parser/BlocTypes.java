package parser;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import parser.MyFunction.BiFunctionException;

/**
 * BlocTypes est la liste de tous les types de bloc avec une fonction pour le decoder
 * 
 * @author Rémi BUTET
 *
 */
public enum BlocTypes {
	HEADER((image, contenu) -> {
		if (contenu.length != 9) throw new MessageException("Le format du bloc H est invalide (length) !");

		if (image.getLargeur() != null || image.getHauteur() != null || image.getPixelType().isPresent()) {
			throw new MessageException("Il y a plusieurs bloc H !");
		}
		
		int largeur = ByteBuffer.wrap(contenu, 0, 4).getInt();
		int hauteur = ByteBuffer.wrap(contenu, 4, 4).getInt();
		Optional<PixelTypes> pixelType = PixelTypes.of(contenu[8]);
		
		if (largeur < 0) throw new MessageException("La largueur est négative !");
		if (hauteur < 0) throw new MessageException("La hauteur est négative !");
		if (!pixelType.isPresent()) throw new MessageException("Le format du bloc H est invalide : PixelType inconnu !");
		
		image.setLargeur(largeur);
		image.setHauteur(hauteur);
		image.setPixelType(pixelType.get());
		
		return true;
	}),
	COMMENTAIRE((image, contenu) -> {
		image.addCommentaire(new String(contenu, StandardCharsets.US_ASCII));
		return true;
	}),
	DONNEES((image, contenu) -> {
		image.addContenu(contenu);
		return true;
	}),
	PALLETTE((image, contenu) -> {
		if (contenu.length % 3 != 0) throw new MessageException("Le format du bloc P est invalide !");
		
		for (int cpt=0; cpt < contenu.length; cpt+=3) {
			image.addPallette(new Color(contenu[cpt], contenu[cpt+1], contenu[cpt+2]));
		}
		return true;
	});
	
	private final BiFunctionException<Image, byte[], Boolean> fun;
	
	BlocTypes (BiFunctionException<Image, byte[], Boolean> fun) {
		this.fun = fun;
	}
	
	public byte[] array() {
		byte[] array = new byte[1];
		array[0] = (byte) this.name().charAt(0);
		return array;
	}
	
	/**
	 * Permet de décoder le bloc
	 * @param image L'object image
	 * @param bufContenu Le contenu du bloc
	 * @return True si tout a bien fonctionné
	 * @throws MessageException Erreur lors du décodage du contenu
	 */
	public boolean decode(Image image, byte[] bufContenu) throws MessageException {
		return this.fun.apply(image, bufContenu);
	}
	
	/**
	 * Retroune le BlocType
	 * @param value La première lettre du type
	 * @return Le BlocType si il existe
	 */
	public static Optional<BlocTypes> of(byte value) {
		for (BlocTypes type : BlocTypes.values()) {
			if (type.name().charAt(0) == value) {
				return Optional.of(type);
			}
		}
		return Optional.empty();
	}
}
