package qr;

public class Main {

	public static final String INPUT =  "https://fr.pornhub.com/";

	/*
	 * Parameters
	 */
	public static final int VERSION = 1;
	public static final int MASK = 1;
	public static final int SCALING = 20;

	public static void main(String[] args) {

		/*
		 * Encoding
		 */
		boolean[] encodedData = DataEncoding.byteModeEncoding(INPUT, VERSION);

		int[][] qrCode = MatrixConstruction.renderQRCodeMatrix(VERSION, encodedData);
		
		int penality = MatrixConstruction.evaluate(qrCode);
		System.out.println("Le nombre de point de penalite pour le mask " + MASK + " est de " + penality + " points.");
		
		int bestMask = MatrixConstruction.findBestMasking(VERSION, encodedData);
		System.out.println("Le meilleur mask est le " + bestMask);
		
		/*
		 * Visualization
		 */
		Helpers.show(qrCode, SCALING);
	}

}
