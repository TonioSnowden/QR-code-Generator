package qr;

import java.nio.charset.StandardCharsets;
import reedsolomon.ErrorCorrectionEncoding;

public final class DataEncoding {

	public static boolean[] byteModeEncoding(String input, int version) {
		// appel de la méthode pour créer le tableau de bytes en fonction du message et de la version
		boolean[] TAB = bytesToBinaryArray(
				addErrorCorrection(
						fillSequence(
								addInformations(
										encodeString(input,QRCodeInfos.getMaxInputLength(version))),QRCodeInfos.getCodeWordsLength(version)),QRCodeInfos.getECCLength(version)));
		return TAB;
	}

	public static int[] encodeString(String input, int maxLength) {

		byte[] tab = input.getBytes(StandardCharsets.ISO_8859_1);
		int length = (tab.length > maxLength) ? maxLength : tab.length;
		int[] tabByte = new int[length];


		for(int i=0;i<length;i++) {
			byte b = tab[i];
			tabByte[i] = (int)(b & 0xFF);			
		}
		return tabByte;
	}

	public static int[] addInformations(int[] inputBytes) {
		int[] byteCode = new int[inputBytes.length + 2];
		if(inputBytes.length==0) {
			byteCode[0]=64;
			byteCode[1]=0;
		} else {
			byteCode[0]=64 + ((0b1111_0000 & (inputBytes.length)) >>4);
			byteCode[1]=((0b0000_1111 & (inputBytes.length)) <<4) + ((0b1111_0000 & inputBytes[0]) >>4);

			byteCode[byteCode.length - 1]=0 + ((0b0000_1111 & inputBytes[inputBytes.length -1]) << 4);

			for(int i=2;i<byteCode.length - 1;i++) {
				byteCode[i] = ((0b1111_0000 & inputBytes[i-1]) >> 4) + ((0b0000_1111 & inputBytes[i-2]) << 4); 
			}
		}
		return byteCode;
	}

	public static int[] fillSequence(int[] encodedData, int finalLength) {
		int[] finalTab = new int[finalLength];

		for(int i=0;i<finalLength;i++) {
			if(i<encodedData.length) {
				finalTab[i]=encodedData[i];
			} else {
				if((i-(encodedData.length))%2 == 0) {
					finalTab[i]=236;
				} else {
					finalTab[i]=17;
				}
			}
		}
		return finalTab;
	}

	public static int[] addErrorCorrection(int[] encodedData, int eccLength) {
		int[] newTab = new int[encodedData.length + eccLength];
		int[] eccTab = new int[eccLength];

		eccTab = ErrorCorrectionEncoding.encode(encodedData,eccLength);

		for(int i=0;i<encodedData.length;i++) {
			newTab[i]=encodedData[i];
		}

		for(int i=encodedData.length;i<newTab.length;i++) {
			newTab[i]=eccTab[i-encodedData.length];
		}	
		return newTab;
	}

	public static boolean[] bytesToBinaryArray(int[] data) {
		boolean[] bitArray = new boolean[8*data.length];

		for(int i=0; i<data.length;i++) {
			for(int j=0; j<8; j++) {
				if((((0b1111_1111 & data[i])>>(7-j))%2==0)){
					bitArray[8*i + j]=false;
				} else {
					bitArray[8*i + j]=true;
				}
			}
		}
		return bitArray;
	}
}