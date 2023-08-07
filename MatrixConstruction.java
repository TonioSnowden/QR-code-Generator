package qr;



public class MatrixConstruction {

	public static int B = 0xFF_00_00_00;
	public static int W = 0xFF_FF_FF_FF;

	public static int[][] renderQRCodeMatrix(int version, boolean[] data, int mask) {

		int[][] matrix = constructMatrix(version, mask);

		addDataInformation(matrix, data, mask);

		return matrix;
	}
	public static int[][] constructMatrix(int version, int mask) {
		int[][] matrix = initializeMatrix(version);
		addFinderPatterns(matrix);
		addAlignmentPatterns(matrix,version);
		addTimingPatterns(matrix);
		addDarkModule(matrix);
		addFormatInformation(matrix,mask);
		return matrix;
	}
	public static int[][] initializeMatrix(int version) {
		int l = QRCodeInfos.getMatrixSize(version);
		int [][] matrice = new int[l][l];
		return matrice;
	}
	public static void addFinderPatterns(int[][] matrix) {
		finderPatterns(0,0,matrix);
		finderPatterns(0,matrix.length - 7,matrix);
		finderPatterns(matrix.length - 7,0,matrix);
		for(int i=0;i<=7;i++) {
			matrix[7][i]=W;
			matrix[i][7]=W;
			matrix[7][matrix.length-(i+1)]=W;
			matrix[matrix.length-8][i]=W;
			matrix[matrix.length-(i+1)][7]=W;
			matrix[i][matrix.length-8]=W;
		}
	} 
	public static void addAlignmentPatterns(int[][] matrix, int version) {
		if (version>1) {
			matrix[matrix.length -7][matrix.length-7]=B;

			for(int i=1; i<=2; i++) {
				if (i%2==0) {
					for(int j=-i;j<=i;j++) {
						matrix[matrix.length-7-i][matrix.length+j-7]=B;
						matrix[matrix.length+i-7][matrix.length+j-7]=B;
						matrix[matrix.length+j-7][matrix.length+i-7]=B;
						matrix[matrix.length+j-7][matrix.length-7-i]=B;
					}
				} else {
					for(int j=-i;j<=i;j++) {
						matrix[matrix.length-7-i][matrix.length+j-7]=W;
						matrix[matrix.length-7+i][matrix.length+j-7]=W;
						matrix[matrix.length-7+j][matrix.length+i-7]=W;
						matrix[matrix.length-7+j][matrix.length-7-i]=W;
					}
				}
			}
		}
	}
	public static void addTimingPatterns(int[][] matrix) {
		for(int i=0;i<(matrix.length-2*8);i++) {
			if(i%2==0) {
				matrix[6][8+i]=B;
				matrix[8+i][6]=B;
			} else {
				matrix[6][8+i]=W;
				matrix[8+i][6]=W;
			}
		}
	}
	public static void addDarkModule(int[][] matrix) {
		matrix[8][matrix.length-8]=B;
	}
	public static void addFormatInformation(int[][] matrix, int mask) {
		boolean[] rS = QRCodeInfos.getFormatSequence(mask);

		for(int i=0;i<=14;i++) {
			if(rS[i]) {
				if(i<6) {
					matrix[i][8]=B;
					matrix[8][matrix.length-1-i]=B;
				} if(i==6) {
					matrix[i+1][8]=B;
					matrix[8][matrix.length-1-i]=B;
				} if(i==7) {
					matrix[i+1][8]=B;
					matrix[matrix.length-(14-i)-1][8]=B;
				} if(i==8) {
					matrix[8][14-i+1]=B;
					matrix[matrix.length-(14-i)-1][8]=B;
				} if(i>=9) {
					matrix[8][14-i]=B;
					matrix[matrix.length-(14-i)-1][8]=B;
				}
			} else {
				if(i<6) {
					matrix[i][8]=W;
					matrix[8][matrix.length-1-i]=W;
				} if(i==6) {
					matrix[i+1][8]=W;
					matrix[8][matrix.length-1-i]=W;
				} if(i==7) {
					matrix[i+1][8]=W;
					matrix[matrix.length-(14-i)-1][8]=W;
				} if(i==8) {
					matrix[8][14-i+1]=W;
					matrix[matrix.length-(14-i)-1][8]=W;
				} if(i>=9) {
					matrix[8][14-i]=W;
					matrix[matrix.length-(14-i)-1][8]=W;
				}
			}
		}
	}
	public static int maskColor(int col, int row, boolean dataBit, int masking) {
		boolean mask=true;

		switch(masking) {
		case 0 : mask=((col+row)%2==0);
		break;

		case 1 : mask=(row%2==0);
		break;

		case 2 : mask=(col%3==0); 
		break;	
		case 3 : mask=(col+row)%3==0;
		break;

		case 4 : mask=((Math.floor(row/2)+Math.floor(col/3))%2==0);
		break;

		case 5 : mask=(((col*row)%2+(col*row)%3)==0);
		break;

		case 6 : mask=((((col*row)%2+((col*row)%3))%2)==0);
		break;

		case 7 : mask=((((col+row)%2+((col*row)%3))%2)==0);
		break;
		}
		if (mask){
			return dataBit ? W:B;
		} else {
			return dataBit ? B:W;
		}
	}
	public static void addDataInformation(int[][] matrix, boolean[] data, int mask) {
		int z=0;
		
        boolean g=true;
		for(int i=matrix.length-1;i>=0;i-=2) {
			
			for(int j=matrix.length-1;j>=0;j--) {
				if (i==6) {
					i=5;
				}
					if (g) {
						if (!(matrix[i][j]==B) && !(matrix[i][j]==W)) {
							if(z<data.length) {
								matrix[i][j]=maskColor(i,j,data[z],mask);
								z+=1;
							} else {
								matrix[i][j]=maskColor(i,j,false,mask);
							}
						}
						if(!(matrix[i-1][j]==W) && !(matrix[i-1][j]==B)) {
							if(z<data.length) {
								matrix[i-1][j]=maskColor(i-1,j,data[z],mask);
								z+=1;
							} else {
								matrix[i-1][j]=maskColor(i-1,j,false,mask);
							}
						}
					}
					else {
						if(!(matrix[i][matrix.length-1-j]==B) && !(matrix[i][matrix.length-1-j]==W)) {
							if(z<data.length) {
								matrix[i][matrix.length-1-j]=maskColor(i,matrix.length-1-j,data[z],mask);
								z+=1;
							} else {
								matrix[i][matrix.length-1-j]=maskColor(i,matrix.length-1-j,false,mask);
							}
						}

						if(!(matrix[i-1][matrix.length-1-j]==B) && !(matrix[i-1][matrix.length-1-j]==W)) {
							if(z<data.length) {
								matrix[i-1][matrix.length-1-j]=maskColor(i-1,matrix.length-1-j,data[z],mask);
								z+=1;
							} else {
								matrix[i-1][matrix.length-1-j]=maskColor(i-1,matrix.length-1-j,false,mask);
							}
						}
					}
				}
			g=!g;}
		}
	public static int[][] renderQRCodeMatrix(int version, boolean[] data) {

		int mask = findBestMasking(version, data);

		return renderQRCodeMatrix(version, data, mask);
	}
	public static int findBestMasking(int version, boolean[] data) {
		int index=10;
		int[] tab= new int[8];
		for(int i=0;i<=7;i++) {
			tab[i]=evaluate(renderQRCodeMatrix(version,data,i));
		}
		for(int i=0;i<=7;i++) {
			int q = Integer.MAX_VALUE;
			if(tab[i]<q) {
				q=tab[i];
				index = i;
			}
		}
		return index;
	}

	public static int evaluate(int[][] matrix) {
        int pointDePenalite = 0;
        int rep =0;
        int rep1 =0;
        for(int i=0;i<matrix.length;i++) {
        	rep=0;
        	rep1=0;
        	for( int j=0;j<matrix.length-1;j++) {
        		if (matrix[i][j]==matrix[i][j+1]) {
        			rep += 1;
        		} else {
        			rep=0;
        		}
        		if (rep == 4) {
        			pointDePenalite += 3;
        		}
        		if (rep>4) {
        			pointDePenalite += 1;
        		}
        		if (matrix[j][i]==matrix[j+1][i]) {
        			rep1 += 1;
        		} else {
        			rep1=0;
        		}
        		if (rep1 == 4) {
        			pointDePenalite += 3;
        		}
        		if (rep1>4) {
        			pointDePenalite += 1;
        		}
        	}
        }
        for(int i=0;i<matrix.length-1;i++) {
        	for(int j=0;j<matrix.length-1;j++) {
        		if(matrix[i][j]==matrix[i+1][j]) {
        			if(matrix[i+1][j]==matrix[i][j+1]) {
        				if(matrix[i][j+1]==matrix[i+1][j+1]) {
        					pointDePenalite += 3;
        				}
        			}
        		}
        	}
        }
        for(int i=0;i<matrix.length;i++) {
        	for(int j=0;j<matrix.length-10;j++) {
        		if (matrix[i][j]==W && matrix[i][j+1]==W && matrix[i][j+2]==W && matrix[i][j+3]==W && matrix[i][j+4]==B && matrix[i][j+5]==W && matrix[i][j+6]==B && matrix[i][j+7]==B && matrix[i][j+8]==B && matrix[i][j+9]==W && matrix[i][j+10]==B) {
        			pointDePenalite += 40;
        		}
        	    if (matrix[j][i]==B && matrix[j+1][i]==W && matrix[j+2][i]==B && matrix[j+3][i]==B && matrix[j+4][i]==B && matrix[j+5][i]==W && matrix[j+6][i]==B && matrix[j+7][i]==W && matrix[j+8][i]==W && matrix[j+9][i]==W && matrix[j+10][i]==W) {
        	    	pointDePenalite+=40;
        		}
        	}
        }
        int mod = 0;
        int modn = 0;
        for(int i=0;i<matrix.length;i++) {
        	for(int j=0;j<matrix.length;j++) {
        		if (matrix[i][j]==B) {
        			modn++;
        			mod++;
        		}else {
        			mod++;
        		}
        	}
        }
        double pn = (modn/mod)*100;
        double pp = Math.abs((pn - (pn%5))-50);
        double ps = Math.abs((pn + (5-pn%5))-50);
        pointDePenalite+= Math.min(pp, ps);
        
		return pointDePenalite;
	}
	// méthode auxilliaire qui crée un finderPatterns
	public static void finderPatterns(int col,int row, int[][] matrix){

		for(int i=col; i<col + 7; i++) {
			for(int j=row;j<row+7;j++) {
					matrix[i][j]=B;
			}
		}
		for(int i=col+1; i<col + 6; i++) {
			for(int j=row+1;j<row+6;j++) {	
					matrix[i][j]=W;
				}
			}
		for(int i=col+2; i<col + 5; i++) {
			for(int j=row+2;j<row+5;j++) {
				matrix[i][j]=B;
			}
		}
	}
}