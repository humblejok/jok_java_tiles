package jok_java_tiles;

// Etape 1 :
// Importation des packages Java 2
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JButton;
import javax.swing.JPanel;

// Etape 2 :
// Importation des packages Java 3D
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class SaveImage3D extends Applet {

  private JPanel bottom = new JPanel();
  private JButton save = new JButton("Sauvegarder");
  private Canvas3D canvas3D = null;
  private OffScreenCanvas3D offScreenCanvas = null;
  private TransformGroup rotation;

  private int index = 0;
  
  public SaveImage3D() {

    this.setLayout(new BorderLayout());
    bottom.setLayout(new FlowLayout(FlowLayout.CENTER));

    save.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveImageCB(e);
      }
    });

    bottom.add(save);
    this.add(bottom, BorderLayout.SOUTH);

    // Etape 3 :
    // Creation du Canvas 3D
    canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
    this.add(canvas3D, BorderLayout.CENTER);

    // Etape 4 :
    // Cree un Canvas3D off-screen (on ne peut pas recuperer directement une
    // image on-screen)
    offScreenCanvas = new OffScreenCanvas3D(canvas3D);

    // Etape 5 :
    // Creation d'un objet SimpleUniverse
    SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

    // Etape 6 :
    // Positionnement du point d'observation pour avoir une vue correcte de la
    // scene 3D
    simpleU.getViewingPlatform().setNominalViewingTransform();

    // Etape 7 :
    // On ajoute le canvas offscreen a l'objet View du SimpleUniverse
    simpleU.getViewer().getView().addCanvas3D(offScreenCanvas);

    // Etape 8 :
    // Creation de la scene 3D qui contient tous les objets 3D que l'on veut
    // visualiser
    BranchGroup scene = createSceneGraph();

    // Etape 9 :
    // Compilation de la scene 3D
    scene.compile();

    // Etape 10 :
    // Attachement de la scene 3D a l'objet SimpleUniverse
    simpleU.addBranchGraph(scene);
    double step = Math.PI /30.0d;
    saveImageCB(null);
    for (index = 1;index<60;index++) {
    	rotation.setTransform(rotation(step * index));
    	saveImageCB(null);
    }
  }

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

	private Transform3D rotation(double angle) {
	    Transform3D t3d=new Transform3D();
	    t3d.rotX(angle);
	    return t3d;

	}
	
	private TransformGroup mkRotation(double angle) {

	    Transform3D t3d=new Transform3D();
	    t3d.rotX(angle);
	    return new TransformGroup(t3d);

	}
  
  /**
   * Creation de la scene 3D qui contient tous les objets 3D
   * @return scene 3D
   */
  public BranchGroup createSceneGraph() {
		BranchGroup group = new BranchGroup();
		Box myBox = new Box(0.7f,0.7f,0.05f, Box.GENERATE_TEXTURE_COORDS, loadTexture("c:\\DEV\\Sources\\KnightMove\\assets\\BlackBoard.png"));
		rotation = mkRotation(0.0f);
		rotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		rotation.addChild(myBox);
		group.addChild(rotation);
		return group;
  }

  /**
   * Callback appele lorsqu'on veut sauvegarder une image
   */
  protected void saveImageCB(ActionEvent e) {
    // Creation du fichier dans lequel on va sauvegarder notre image 3D
    File imageFile = new File("c:\\dev\\black_" + String.format("%02d", index) +".png");

    // Dimension (en pixels) de l'image a sauvegarder dans le fichier
    Dimension dim = new Dimension(1024, 768);

    // On recupere l'image (pixmap) rendue par le canvas 3D offscreen
    BufferedImage image = offScreenCanvas.getOffScreenImage(dim);

    // On recupere le contexte graphique de l'image finale de sortie
    Graphics2D gc = image.createGraphics();
    gc.drawImage(image, 0, 0, null);

    // Sauvegarde de l'image dans un fichier au format PNG
    try {
      ImageIO.write(image, "png", imageFile);
    }
    catch (IOException ex) {
      System.out.println("Impossible de sauvegarder l'image");
    }
  }

  /**
   * Etape 11 :
   * Methode main() nous permettant d'utiliser cette classe comme une applet
   * ou une application.
   * @param args parametres de la ligne de commande
   */
  public static void main(String[] args) {
    Frame frame = new MainFrame(new SaveImage3D(), 1024, 768);
  }
}
