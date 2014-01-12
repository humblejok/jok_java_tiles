package jok_java_tiles;

/*File Java3D009.java
Copyright 2007, R.G.Baldwin

This program is an update to the program named Java3D008.

The purpose of this update is to illustrate one approach
to using translation and rotation in combination to cause
different behaviors.  In one case, a white sphere will
spin around an axis that is tilted counter-clockwise. In
two cases, a white sphere orbits around a yellow sphere on
a plane that is tilted relative to the X-Y, Y-Z, and Z-X 
planes.

The universe contains a yellow sphere, a green sphere, and
a white sphere.

The yellow sphere slowly rotates around the vertical axis
in 3D space for a specified period of time. The center of
the yellow sphere is at the origin in 3D space. Therefore,
the yellow sphere appears to rotate around its own 
vertical axis.

The green sphere is translated to a location slightly 
above, to the right of, and behind the yellow sphere.  The
green sphere maintains its translated location throughout 
the time that the program is running.

The behavior of the white sphere depends on input via a
user input GUI.  The input GUI contains three buttons
labeled "a", "b", and "c"

Depending on the user input the white sphere is animated 
in one of three ways:

1. If the user clicks the "a" button, the white sphere is 
translated to a location just outside the yellow sphere on
the positive z-axis (0.0f,0.0f,0.7f) where it maintains 
its location in space and rotates about its own vertical 
axis.  However, its vertical axis is tilted relative to
the direction of the vertical axis of 3D space. This is 
the result of rotation followed by axis transformation
followed by translation.

2. If the user clicks the "b" button, the white sphere is 
translated to the same location in space as for the "a"
button and then orbits the yellow sphere with the same 
face of the white sphere always toward the yellow sphere.
However, the orbit is on a plane that is tilted relative 
to  the X-Y, Y-Z, and Z-X planes. This is the result 
of translation followed by rotation followed by axis
transformation.

3. If the user clicks the "c" button, the white sphere 
orbits the yellow sphere as in 2 above.  However, for this
case, the white sphere also rotates on its own vertical 
axis while orbiting the yellow sphere.  As before, the 
orbit is on a plane that is tilted relative to  the 
X-Y, Y-Z, and Z-X planes. This is the result of rotation 
followed by translation followed by another rotation 
followed by axis transformation.

Note:  On my relatively slow laptop, the first animation 
cycle is perhaps 25-percent complete before the first 
image appears on the screen.  I was unable to discover any
way to prevent this from happening. In addition, there are
sporadic undesirable pauses in the animation.

Tested using Java SE 6, and Java 3D 1.5.0 running under
Windows XP.
*********************************************************/
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Primitive;
import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Alpha;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Node;
import javax.vecmath.Vector3f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3d;
import javax.vecmath.Color3f;
import java.awt.Frame;
import java.awt.Button;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//This is the top-level driver class for this program.
public class Java3D008 extends Frame{
  TheScene theScene;

  public static void main(String[] args){
	  Java3D008 thisObj = new Java3D008();
  }//end main
  //----------------------------------------------------//

  public Java3D008(){//top-level constructor
    setLayout(new GridLayout(1,3));
    Button aButton = new Button("a");
    Button bButton = new Button("b");
    Button cButton = new Button("c");

    add(aButton);
    add(bButton);
    add(cButton);

    aButton.addActionListener(
      new ActionListener(){
        public void actionPerformed(ActionEvent e){
          theScene = new TheScene("a");
        }//end actionPerformed
      }//end new ActionListener
    );//end addActionListener

    bButton.addActionListener(
      new ActionListener(){
        public void actionPerformed(ActionEvent e){
          theScene = new TheScene("b");
        }//end actionPerformed
      }//end new ActionListener
    );//end addActionListener

    cButton.addActionListener(
      new ActionListener(){
        public void actionPerformed(ActionEvent e){
          theScene = new TheScene("c");
        }//end actionPerformed
      }//end new ActionListener
    );//end addActionListener

    setTitle("Copyright 2007, R.G.Baldwin");
    setBounds(236,0,235,75);
    setVisible(true);

    //This window listener is used to terminate the
    // program when the user clicks the X button.
    addWindowListener(
      new WindowAdapter(){
        public void windowClosing(WindowEvent e){
          System.exit(0);
        }//end windowClosing
      }//end new WindowAdapter
    );//end addWindowListener

  }//end constructor
  //----------------------------------------------------//

  //This is an inner class, from which the universe will
  // be  instantiated and animated.
  class TheScene extends Frame{

    //Declare instance variables that are used later by
    // the program.
    Canvas3D canvas3D;
    Sphere yellowSph;
    Sphere whiteSph;
    Sphere greenSph;
    PointLight pointLight;
    SimpleUniverse simpleUniverse;

    BranchGroup mainBranchGroup = new BranchGroup();

    TransformGroup greenTransformGroup = 
                                     new TransformGroup();
    TransformGroup yellowRotXformGroup = 
                                     new TransformGroup();

    String buttonLabel;
    //--------------------------------------------------//

    TheScene(String input){//constructor
      //Save the incoming parameter to be used later to
      // decide which behavior to execute.
      buttonLabel = input;

      //Construct the universe.
      createACanvas();
      createTheUniverse();

      //Construct the objects that will occupy the
      // universe.
      createYellowSphere();
      createWhiteSphere();
      createGreenSphere();
      createPointLight();

      //Animate the objects.

      //Animation of the yellow sphere is independent of
      // user input.
      animateYellowSphere();

      //Animation of the white sphere depends on the
      // button selected by the user in the input GUI.
      if(buttonLabel.equals("a")){
        //This code will rotate the white sphere around
        // the vertical axis and translate the rotating
        // sphere away from the origin in 3D space causing
        // it to remain stationary while spinning in 3D
        // space. The vertical axis belonging to the white
        // sphere is tilted relative to the vertical axis
        // in 3D space and the sphere rotates about that
        // tilted axis.

        //Begin rotation of white sphere.
        TransformGroup whiteRotXformGroup = 
                       rotate(whiteSph,new Alpha(4,2500));

        //Now perform a one-time rotational transform on
        // the whiteRotXformGroup, rotating it around
        // its z axis only.  This will cause the vertical
        // axis of the white sphere to tilt counter-
        // clockwise by 22.5 degrees.  It will then spin
        // around that tilted axis after it is translated.
        TransformGroup tiltedGroup = 
                      tiltTheAxes(whiteRotXformGroup,
                      0.0d,//x-axis
                      0.0d,//y-axis
                      Math.PI/8.0d);//z-axis

        //Translate the rotating white sphere
        TransformGroup whiteTransXformGroup = translate(
                            tiltedGroup,
                            new Vector3f(0.0f,0.0f,0.7f));

        mainBranchGroup.addChild(whiteTransXformGroup);

      }else if(buttonLabel.equals("b")){
        //This code will translate the white sphere and
        // rotate the translated white sphere around the
        // vertical axis in 3D space, causing the white
        // sphere to orbit the yellow sphere with the same
        // face of the white sphere toward the yellow
        // sphere at all times.  The white sphere will 
        // orbit in a tilted plane.

        //Translate the white sphere.
        TransformGroup whiteTransXformGroup = translate(
                            whiteSph,
                            new Vector3f(0.0f,0.0f,0.7f));

        //Begin rotation of translated white sphere around
        // the vertical axis at the origin in 3D space.
        TransformGroup whiteRotXformGroup = 
           rotate(whiteTransXformGroup,new Alpha(2,5000));

        //Now perform a one-time rotational transform on
        // the whiteRotXformGroup, rotating it around
        // its z axis only.  This will cause the
        // white sphere to orbit the yellow sphere on a 
        // plane that is tilted relative to the x-z,
        // plane.
        TransformGroup tiltedGroup = 
                      tiltTheAxes(whiteRotXformGroup,
                      0.0d,//x-axis
                      0.0d,//y-axis
                      Math.PI/4.0d);//z-axis

        mainBranchGroup.addChild(tiltedGroup);

      }else if(buttonLabel.equals("c")){
        //The following code will rotate the white sphere,
        // translate the rotating white sphere away from
        // the origin in 3d space, and rotate the
        // translated rotating white sphere around the
        // origin in 3D space causing the white sphere to
        // orbit the yellow sphere and to spin about its
        // own vertical axis at the same time. The white
        // sphere will orbit in a tilted plane.

        //Begin rotation of the white sphere
        TransformGroup whiteRotXformGroup = 
                       rotate(whiteSph,new Alpha(8,1250));

        //Translate the rotating white sphere
        TransformGroup whiteTransXformGroup = translate(
                            whiteRotXformGroup,
                            new Vector3f(0.0f,0.0f,0.7f));

        //Begin rotation of the translated rotating white
        // sphere.
        TransformGroup whiteRotGroupXformGroup = 
           rotate(whiteTransXformGroup,new Alpha(2,5000));

        //Now perform a one-time rotational transform on
        // the whiteRotGroupXformGroup, rotating it around
        // its x, y, and z axes.  This will cause the
        // white sphere to orbit the yellow sphere on a 
        // plane that is tilted relative to the x-y, y-z,
        // and z-x planes.
        TransformGroup tiltedGroup = 
                      tiltTheAxes(whiteRotGroupXformGroup,
                      Math.PI/8.0d,
                      Math.PI/2.0d,
                      Math.PI/4.0d);

        mainBranchGroup.addChild(tiltedGroup);

      }//end else-if

      //Finish populating the mainBranchGroup.
      mainBranchGroup.addChild(greenTransformGroup);
      mainBranchGroup.addChild(pointLight);
      mainBranchGroup.addChild(yellowRotXformGroup);

      //Populate the universe by adding the branch group
      // that contains the objects.
      simpleUniverse.addBranchGraph(mainBranchGroup);

      //Do the normal GUI stuff.
      setTitle("Copyright 2007, R.G.Baldwin");
      setBounds(0,0,235,235);
      setVisible(true);

      //This listener is used to terminate the program 
      // when the user clicks the X-button on the Frame.
      addWindowListener(
        new WindowAdapter(){
          public void windowClosing(WindowEvent e){
            System.exit(0);
          }//end windowClosing
        }//end new WindowAdapter
      );//end addWindowListener

    }//end constructor
    //--------------------------------------------------//

    //Create a Canvas3D object to be used for rendering
    // the Java 3D universe.  Place it in the CENTER of
    // the Frame.
    void createACanvas(){
      canvas3D = new Canvas3D(
              SimpleUniverse.getPreferredConfiguration());
      add(BorderLayout.CENTER,canvas3D);
    }//end createACanvas
    //--------------------------------------------------//

    //Create and set properties for the large yellow
    // sphere.
    void createYellowSphere(){
      //Begin by describing the appearance of the surface
      // of the large sphere.  Make the color of the large
      // sphere yellow.
      Material yellowSphMaterial = new Material();
      yellowSphMaterial.setDiffuseColor(1.0f,1.0f,0.0f);
      Appearance yellowSphAppearance = new Appearance();
      yellowSphAppearance.setMaterial(yellowSphMaterial);

      //Now instantiate the large yellow sphere with 9
      // divisions.  Set the radius to 0.5. The reason for
      // setting GENERATE_NORMALS is unclear at this time.
      yellowSph = new Sphere(0.5f,
                             Primitive.GENERATE_NORMALS,
                             9,
                             yellowSphAppearance);
    }//end createYellowSphere
    //--------------------------------------------------//

    //Create a white sphere with 8 divisions.  Make the
    // number of divisions small so that the fact that it
    // is spinning will be visually obvious.    
    void createWhiteSphere(){
      Material whiteSphMaterial = new Material();
      whiteSphMaterial.setDiffuseColor(1.0f,1.0f,1.0f);
      Appearance whiteSphAppearance = new Appearance();
      whiteSphAppearance.setMaterial(whiteSphMaterial);
      whiteSph = new Sphere(0.2f,
                            Primitive.GENERATE_NORMALS,
                            8,
                            whiteSphAppearance);
    }//end createWhiteSphere
    //--------------------------------------------------//

    //Create a small green sphere located up to the
    // right and behind the yellow sphere.    
    void createGreenSphere(){
      Material greenSphMaterial = new Material();
      greenSphMaterial.setDiffuseColor(0.0f,1.0f,0.0f);
      Appearance greenSphAppearance = new Appearance();
      greenSphAppearance.setMaterial(greenSphMaterial);
      greenSph = new Sphere(0.10f,
                            Primitive.GENERATE_NORMALS,
                            50,
                            greenSphAppearance);
                            
      //Translate the green sphere.
      greenTransformGroup = translate(
                           greenSph,
                           new Vector3f(0.5f,0.5f,-0.5f));
    }//end createGreenSphere
    //--------------------------------------------------//
    //Create a white point light, located over the
    // viewer's right shoulder.    
    void createPointLight(){
      Color3f pointLightColor = 
                              new Color3f(1.0f,1.0f,1.0f);
      Point3f pointLightPosition = 
                              new Point3f(1.0f,1.0f,2.0f);
      Point3f pointLightAttenuation = 
                              new Point3f(1.0f,0.0f,0.0f);

      pointLight = new PointLight(pointLightColor,
                                  pointLightPosition,
                                  pointLightAttenuation);

      //Create a BoundingSphere object and use it to the
      // define the illumination region. Illuminate all of
      // the objects within a radius of one unit from
      // the origin in 3D space.
      pointLight.setInfluencingBounds(new BoundingSphere(
                           new Point3d(0.0,0.0,0.0),1.0));
    }//end createPointLight
    //--------------------------------------------------//

    //Create an empty Java 3D universe and associate it 
    // with the Canvas3D object in the CENTER of the
    // frame.  Also specify the apparent location of the
    // viewer's eye.
    void createTheUniverse(){
      simpleUniverse = new SimpleUniverse(canvas3D);
      simpleUniverse.getViewingPlatform().
                             setNominalViewingTransform();
    }//end createTheUniverse
    //--------------------------------------------------//

    //This method causes the yellow sphere to rotate
    // around the vertical axis in 3D space.
    void animateYellowSphere(){
      yellowRotXformGroup = 
                      rotate(yellowSph,new Alpha(2,5000));
    }//end animateYellowSphere
    //--------------------------------------------------//

    //Given an incoming node object and an Alpha object,
    // this method will return a TransformGroup object
    // that is designed to rotate the node around the
    // vertical axis in 3D space according to the number
    // of cycles and cycle time specified by the Alpha
    // object.
    TransformGroup rotate(Node node,Alpha alpha){

      TransformGroup xformGroup = new TransformGroup();
      xformGroup.setCapability(
                    TransformGroup.ALLOW_TRANSFORM_WRITE);

      //Create an interpolator for rotating the node.
      RotationInterpolator interpolator = 
               new RotationInterpolator(alpha,xformGroup);

      //Establish the animation region for this
      // interpolator.
      interpolator.setSchedulingBounds(new BoundingSphere(
                           new Point3d(0.0,0.0,0.0),1.0));

      //Populate the xform group.
      xformGroup.addChild(interpolator);
      xformGroup.addChild(node);

      return xformGroup;

    }//end rotate
    //--------------------------------------------------//

    //Given an incoming node object and a vector object,
    // this method will return a transform group designed
    // to translate that node according to that vector.
    TransformGroup translate(Node node,Vector3f vector){

        Transform3D transform3D = new Transform3D();
        transform3D.setTranslation(vector);
        TransformGroup transformGroup = 
                                     new TransformGroup();
        transformGroup.setTransform(transform3D);

        transformGroup.addChild(node);
        return transformGroup;
    }//end translate
    //--------------------------------------------------//

    //The purpose of this method is to create and return
    // a transform group designed to perform a counter-
    // clockwise rotation about the x, y, and z axes 
    // belonging to an incoming node.  The three incoming
    // angle values must be specified in radians. Don't
    // confuse this with a RotationInterpolator.  This is
    // not an interpolation operation.  Rather, it is a
    // one-time transform.
    TransformGroup tiltTheAxes(Node node,
                               double xAngle,
                               double yAngle,
                               double zAngle){

      Transform3D tiltAxisXform = new Transform3D();
      Transform3D tempTiltAxisXform = new Transform3D();

      //Construct and then multiply two rotation transform
      // matrices..
      tiltAxisXform.rotX(xAngle);
      tempTiltAxisXform.rotY(yAngle);
      tiltAxisXform.mul(tempTiltAxisXform);

      //Construct the third rotation transform matrix and
      // multiply it by the result of previously 
      // multiplying the two earlier matrices.
      tempTiltAxisXform.rotZ(zAngle);
      tiltAxisXform.mul(tempTiltAxisXform);

      TransformGroup tiltedGroup = new TransformGroup(
                                           tiltAxisXform);
      tiltedGroup.addChild(node);

      return tiltedGroup;
    }//end tiltTheAxes
    //==================================================//
    
  }//end inner class TheScene

}//end class Java3D009