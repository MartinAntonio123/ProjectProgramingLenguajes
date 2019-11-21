import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.*;

public class Panel extends JPanel{

	private BufferedImage img, original;
	private JLabel pathLabel, rgbLabel, cropLabel, imageLabel;
	private JTextField pathText, rText, gText, bText, cropText;
	private JButton pathButton, rgbButton, cropButton, rotateButton, mirrorButton, saveButton, resetButton;
	private static final int MAXTHREADS = Runtime.getRuntime().availableProcessors();

	public Panel(){
		this.setLayout(null);

		pathLabel = new JLabel("Path");
		pathLabel.setBounds(10, 10, 80, 25);
		this.add(pathLabel);
		pathText = new JTextField(20);
		pathText.setBounds(100, 10, 420, 25);
		this.add(pathText);
		pathButton = new JButton("Submit");
		pathButton.setBounds(560, 10, 80, 25);
		this.add(pathButton);
		pathButton.addActionListener(new ActionListener(){
		  public void actionPerformed(ActionEvent e)
		  {
				displayImage();
		  }
		});

		rgbLabel = new JLabel("RGB filter");
		rgbLabel.setBounds(10, 40, 80, 25);
		this.add(rgbLabel);
		rText = new JTextField(20);
		rText.setBounds(100, 40, 135, 25);
		this.add(rText);
		gText = new JTextField(20);
		gText.setBounds(240, 40, 135, 25);
		this.add(gText);
		bText = new JTextField(20);
		bText.setBounds(380, 40, 135, 25);
		this.add(bText);
		rgbButton = new JButton("Apply");
		rgbButton.setBounds(560, 40, 80, 25);
		this.add(rgbButton);
		rgbButton.addActionListener(new ActionListener(){
		  public void actionPerformed(ActionEvent e)
		  {
				addFilter();
		  }
		});

		cropLabel = new JLabel("Resize");
		cropLabel.setBounds(10, 70, 80, 25);
		this.add(cropLabel);
		cropText = new JTextField(20);
		cropText.setBounds(100, 70, 135, 25);
		this.add(cropText);
		cropButton = new JButton("Resize");
		cropButton.setBounds(240, 70, 80, 25);
		this.add(cropButton);
		cropButton.addActionListener(new ActionListener(){
		  public void actionPerformed(ActionEvent e)
		  {
				cropImage();
		  }
		});
		rotateButton = new JButton("Rotate");
		rotateButton.setBounds(340, 70, 80, 25);
		this.add(rotateButton);
		rotateButton.addActionListener(new ActionListener(){
		  public void actionPerformed(ActionEvent e)
		  {
				rotateImage();
		  }
		});
		mirrorButton = new JButton("Mirror");
		mirrorButton.setBounds(440, 70, 80, 25);
		this.add(mirrorButton);
		mirrorButton.addActionListener(new ActionListener(){
		  public void actionPerformed(ActionEvent e)
		  {
				mirrorImage();
		  }
		});

		saveButton = new JButton("Save");
		saveButton.setBounds(240, 100, 80, 25);
		this.add(saveButton);
		saveButton.addActionListener(new ActionListener(){
		  public void actionPerformed(ActionEvent e)
		  {
				saveImage();
		  }
		});
		resetButton = new JButton("Reset");
		resetButton.setBounds(340, 100, 80, 25);
		this.add(resetButton);
	  resetButton.addActionListener(new ActionListener(){
		  public void actionPerformed(ActionEvent e)
		  {
				resetImage();
		  }
		});
	}

	public void resetImage(){
		img = copyImage(original);
		repaintImage();
	}

	public void saveImage(){
		try {
			ImageIO.write(img, "jpg", new File("newimage.jpg"));
		} catch (IOException e) {
			System.out.print("IOException");
		}
	}

	public void displayImage(){
		System.out.println(pathText.getText());
		try {
    	original = ImageIO.read(new File(pathText.getText()));
		} catch (IOException e) {
			System.out.print("IOException");
		}
		img = copyImage(original);

		imageLabel = new JLabel(new ImageIcon(img));
		imageLabel.setBounds(10, 130, img.getWidth(), img.getHeight());
		this.add(imageLabel);
		repaint();
  }

	public void addFilter(){
		int numRows = img.getHeight();
		int numCols = img.getWidth();
		int red = Integer.parseInt(rText.getText());
		int green = Integer.parseInt(gText.getText());
		int blue = Integer.parseInt(bText.getText());
		GrayThread threads[] = new GrayThread[MAXTHREADS];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new GrayThread((i+1)*(numRows/threads.length), numCols, i*(numRows/threads.length), img, red, green, blue);
		}

		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}

		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		repaintImage();
		//ImageIO.write(img, "jpg", new File("newimage.jpg"));
		System.out.print("Finished");
	}

	public static BufferedImage copyImage(BufferedImage bi){
		ColorModel cm = bi.getColorModel();
    boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
    WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
    return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public void repaintImage(){
		this.remove(imageLabel);
		imageLabel = new JLabel(new ImageIcon(img));
		imageLabel.setBounds(10, 130, img.getWidth(), img.getHeight());
		this.add(imageLabel);
		repaint();
	}

	public void cropImage(){
		int percentage = Integer.parseInt(cropText.getText());
		int numRows = img.getHeight();
		int numCols = img.getWidth();
		BufferedImage img2 = new BufferedImage(numCols/percentage, numRows/percentage, BufferedImage.TYPE_INT_RGB);
		CropThread threads[] = new CropThread[MAXTHREADS];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new CropThread((i+1)*(numRows/threads.length), numCols, i*(numRows/threads.length), img, img2, percentage, 0);
		}

		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}

		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		img = copyImage(img2);
		repaintImage();
		System.out.print("Finished");
	}

	public void mirrorImage(){
		int numRows = img.getHeight();
		int numCols = img.getWidth();
		BufferedImage img2 = new BufferedImage(numCols, numRows, BufferedImage.TYPE_INT_RGB);
		CropThread threads[] = new CropThread[MAXTHREADS];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new CropThread((i+1)*(numRows/threads.length), numCols, i*(numRows/threads.length), img, img2, 1, 1);
		}

		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}

		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		img = copyImage(img2);
		repaintImage();
		System.out.print("Finished");
	}

	public void rotateImage(){
		int numRows = img.getHeight();
		int numCols = img.getWidth();
		BufferedImage img2 = new BufferedImage(numRows, numCols, BufferedImage.TYPE_INT_RGB);
		CropThread threads[] = new CropThread[MAXTHREADS];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new CropThread((i+1)*(numRows/threads.length), numCols, i*(numRows/threads.length), img, img2, 1, 2);
		}

		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}

		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		img = copyImage(img2);
		repaintImage();
		System.out.print("Finished");

	}
}
