import java.awt.image.BufferedImage;

public class CropThread extends Thread{
	/*
	Martin Antonio Vivanco Palacios
	A01701167
	*/
	private int begin, rows, cols, numb, action;
	private BufferedImage img, cutimg;

	public CropThread(int rows, int cols, int begin, BufferedImage img, BufferedImage cutimg, int numb, int action){
		this.begin = begin;
		this.rows = rows;
		this.cols = cols;
		this.img = img;
		this.cutimg = cutimg;
		this.numb = numb;
		this.action = action;
	}
	public void run() {
		if (action == 0) {
			for (int i = begin; i < rows; i++) {
					for (int j = 0; j < cols; j++) {
						if ((i%numb == 0) && (j%numb == 0)) {
							cutimg.setRGB(j/numb,i/numb, img.getRGB(j, i));

						}
					}
			}
		}
		if (action == 1) {
			for (int i = begin; i < rows; i++) {
				int r = cols - 1;
				for (int j = 0; j < cols; j++) {
					cutimg.setRGB(r,i, img.getRGB(j, i));
					r--;
				}
			}
		}
		if (action == 2) {
			for (int i = begin; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					cutimg.setRGB(i,j, img.getRGB(j, i));
				}
			}
		}
		System.out.println("end thread");
	}
}
