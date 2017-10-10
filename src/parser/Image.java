package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Image contient tous les informations récupéré dans le fichier Mini-PNG
 * 
 * @author Rémi BUTET
 *
 */
public class Image {
	
	public static final String HEADER = "Mini-PNG";

	private final File file;
	
	private Integer largeur;
	private Integer hauteur;
	private Optional<PixelTypes> pixelType;
	
	private List<String> commentaires;
	private byte[] contenu;
	private List<Color> palette;
	
	public Image(File file) {
		this.file = file;
		this.commentaires = new ArrayList<String>();
		this.contenu = new byte[0];
		this.palette = new ArrayList<Color>();
		this.pixelType = Optional.empty();
	}
	
	/**
	 * Permet de lire le fichier et de convertir en object Java
	 * @throws MessageException Erreur lors de la lecture ou la conversion du fichier
	 */
	public void decode() throws MessageException {
		FileInputStream input = null;
		try {
			
			input = new FileInputStream(this.file);
			this.readHeader(input);
			this.readBlocs(input);
			
		} catch (IOException e) {
			throw new MessageException("Problème lors de la lecture du fichier", e);
		} finally {
			try {if (input != null) input.close();} catch (IOException e) {}
		}
	}
	
	/**
	 * Vérifie que le fichier commence bien par l'entête
	 * @param input Le fichier en lecture
	 * @throws IOException Erreur lors de la lecture du fichier
	 * @throws MessageException Erreur lors de la conversion du fichier
	 */
	public void readHeader(FileInputStream input) throws IOException, MessageException {
		byte[] buf = new byte[HEADER.length()];
		if (input.read(buf) < 0) throw new MessageException("Fichier corrompu : Pas assez de données !");		
		
		if (!Arrays.equals(buf, HEADER.getBytes())) throw new MessageException("Le fichier n'est pas du type Mini-PNG !");		
	}
	
	/**
	 * Permet de lire tous les blocs du fichier
	 * @param input Le fichier en lecture
	 * @throws IOException Erreur lors de la lecture du fichier
	 * @throws MessageException Erreur lors de la conversion du fichier
	 */
	public void readBlocs(FileInputStream input) throws IOException, MessageException {
		byte[] bufType = new byte[1];
		byte[] bufLongueur = new byte[4];
		
		while (input.read(bufType) >= 0) {
			
			// BlocType
			Optional<BlocTypes> blocType = BlocTypes.of(bufType[0]);
			if (!blocType.isPresent()) throw new MessageException("Fichier corrompu : Le BlocTypes est inconnu !");
			
			// Longueur
			if (input.read(bufLongueur) < 0) throw new MessageException("Fichier corrompu : La longueur est incorrecte !");
			
			int longueur = ByteBuffer.wrap(bufLongueur).getInt();
			if (longueur < 0) throw new MessageException("Fichier corrompu : La longueur est négative !");
			
			// Contenu
			byte[] bufContenu = new byte[longueur];
			if (input.read(bufContenu) < 0) throw new MessageException("Fichier corrompu : Le contenu est incorrect !");
			
			blocType.get().decode(this, bufContenu);
		}
		
		if (!this.pixelType.isPresent()) throw new MessageException("Fichier corrompu : Il n'y a pas de bloc H !");
	}
	
	public void addCommentaire(String value) {
		this.commentaires.add(value);
	}
	
	/**
	 * Retourne la liste des commentaires mais non modifiable
	 * @return La liste des commentaires
	 */
	public List<String> getCommentaires() {
		return Collections.unmodifiableList(this.commentaires);
	}
	
	public void setPixelType(PixelTypes pixelType) {
		this.pixelType = Optional.ofNullable(pixelType);
	}

	public Optional<PixelTypes> getPixelType() {
		return this.pixelType;
	}

	public void setLargeur(int largeur) {
		this.largeur = largeur;
	}
	
	public Integer getLargeur() {
		return this.largeur;
	}
	
	public void setHauteur(int hauteur) {
		this.hauteur = hauteur;
	}

	public Integer getHauteur() {
		return this.hauteur;
	}
	
	/**
	 * Ajoute le contenu de l'image.
	 * 
	 * Si il y a plusieurs blocs D, les données seront rajouté à la suite;
	 * @param bufContenu Le contenu
	 */
	public void addContenu(byte[] bufContenu) {
		ByteBuffer bb = ByteBuffer.allocate(this.contenu.length + bufContenu.length);
		bb.put(this.contenu);
		bb.put(bufContenu);
		this.contenu = bb.array();
	}
	
	/**
	 * Retourne une copie du contenu
	 * @return Le contenu
	 */
	public byte[] getContenu() {
		return Arrays.copyOf(this.contenu, this.contenu.length);
	}

	public void addPallette(Color color) {
		this.palette.add(color);
	}

	public Optional<Color> getPallette(int n) {
		if (n < 0 || n >= this.palette.size()) return Optional.empty();
		
		return Optional.ofNullable(this.palette.get(n));
	}
	
	public String getFilePath() {
		return this.file.getPath();
	}

	@Override
	public String toString() {
		List<String> strings = new ArrayList<String>();
		strings.add("Largeur : " + this.largeur);
		strings.add("Hauteur : " + this.hauteur);
		if (this.pixelType.isPresent()) {
			strings.add("PixelType : " + this.pixelType.get().name());
		} else {
			strings.add("PixelType : Inconnue");
		}
		strings.add("Contenu : " + this.contenu.length + " octets");
		
		if (this.commentaires.isEmpty()) {
			strings.add("Commentaires : Aucun");
		} else {
			strings.add("Commentaires : ");
			for (String commentaire : commentaires) {
				strings.add("    # " + commentaire);
			}
		}
		return String.join("\n", strings);
	}

	/**
	 * Affiche dans la console ou créer un nouveau fichier de l'image
	 * @throws MessageException Erreur si il y a un problème pendant l'affichage
	 */
	public void affiche() throws MessageException {
		if (!this.pixelType.isPresent()) throw new MessageException("Fichier corrompu : Il n'y a pas de bloc H !");
		
		this.pixelType.get().affiche(this);
	}
}
