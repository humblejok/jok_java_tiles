package jok_java_tiles;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class JTiles {

	public Appearance loadTexture(String path) {
		Appearance appearance = new Appearance();
		TextureLoader loader = new TextureLoader(path,"LUMINANCE", new Container());
		
		ImageComponent2D image=loader.getImage();

		Texture2D texture=new Texture2D(Texture.BASE_LEVEL,Texture.RGBA,image.getWidth(),image.getHeight());
		texture.setImage(0, image);
		texture.setEnable(true);
		texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
		texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);
		
		appearance.setTexture(texture);
		appearance.setTextureAttributes(new TextureAttributes());
		
		return appearance;
	}
	
	private TransformGroup mkRotation(double angle) {

	    Transform3D t3d=new Transform3D();
	    t3d.rotX(angle);
	    return new TransformGroup(t3d);

	}
	
	public Canvas3D getOffScreen(Canvas3D onScreen) {
		Canvas3D canvas3d = new Canvas3D(onScreen.getGraphicsConfiguration(), true);
		canvas3d.setSize(onScreen.getSize());
		canvas3d.getScreen3D().setSize(onScreen.getScreen3D().getSize());
		canvas3d.getScreen3D().setPhysicalScreenHeight(onScreen.getPhysicalHeight());
		canvas3d.getScreen3D().setPhysicalScreenWidth(onScreen.getPhysicalWidth());
		
		return canvas3d;
	}
	
	public JTiles() {
		SimpleUniverse universe = new SimpleUniverse();
		Canvas3D offScreen = new OffScreenCanvas3D(universe.getCanvas());
		universe.getViewer().getView().addCanvas3D(offScreen);
		BranchGroup group = new BranchGroup();
		Box myBox = new Box(0.5f,0.5f,0.05f, Box.GENERATE_TEXTURE_COORDS, loadTexture("c:\\DEV\\Sources\\KnightMove\\assets\\BlackBoard.png"));
		TransformGroup rotation = mkRotation(Math.PI/4);
		rotation.addChild(myBox);
		group.addChild(rotation);
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.addBranchGraph(group);
		// Saving
		BufferedImage bImage = new BufferedImage(1000,1000,BufferedImage.TYPE_INT_ARGB);
		ImageComponent2D buffer = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);
		buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
		
		offScreen.setOffScreenBuffer(buffer);
		offScreen.renderOffScreenBuffer();
		offScreen.waitForOffScreenRendering();
		try {
		    ImageIO.write(bImage, "png", new File("c:\\dev\\test.png"));
		}
		catch (IOException ex) {
		    System.out.println("Impossible de sauvegarder l'image");
		}
	}
		
	public static void main(String [] argv) {
		new JTiles();
	}
	
}
