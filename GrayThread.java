import java.awt.image.BufferedImage;

public class GrayThread extends Thread{
	/*
	Martin Antonio Vivanco Palacios
	A01701167

	*/
	private int begin, rows, cols, redf, greenf, bluef;
	private BufferedImage img;

	public GrayThread(int rows, int cols, int begin, BufferedImage img, int red, int green, int blue){
		this.begin = begin;
		this.rows = rows;
		this.cols = cols;
		this.img = img;
		this.redf = red;
		this.greenf = green;
		this.bluef = blue;
	}
	public void run() {
		/*
		go through all the cols and the specified rows to change the colour of the matrix
		in those points
		*/
		for (int i = begin; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
						int rgb = img.getRGB(j, i);  // rgb contian all number coded within a single integer concatenaed as red/green/blue

						//use this separation to explore with different filters and effects  you need to do the invers process to encode red green blue into a single value again
						//separation of each number
						int red = rgb & 0xFF;  // & uses  0000000111 with the rgb number to eliminate all the bits but the las 3 (which represent 8 position which are used for 0 to 255 values)
						int green = (rgb >> 8) & 0xFF;  // >> Bitwise shifts 8 positions  & makes  uses  0000000111 with the number and eliminates the rest
						int blue = (rgb >> 16) & 0xFF; // >> Bitwise shifts 16 positions  & makes  uses  0000000111 with the number and eliminates the rest

						//rgb = ~rgb; // ~ negation of the complete pixel value

						float L = (float) (0.2126 * (float) red + 0.7152 * (float) green + 0.0722 * (float) blue);
						// (220, 220, 220) // gray

						int color;
						color = redf * (int) L / 255;
						color = (color << 8) | greenf * (int) L / 255;
						color = (color << 8) | bluef * (int) L / 255;

						img.setRGB(j, i, color); // sets the pixeles to specified color  (negative image)
				}
		}
		System.out.println("end thread");
	}
}
