public class SamplePlayShow {

    public static void main (String[] args) {
	try {
	    SoundPlayer player = new SoundPlayer();
	    player.play("roar.wav");

	    PictureViewer viewer = new PictureViewer();
	    viewer.show("cute.gif");
	}
	catch (MultimediaException e) {
	    System.out.println(e.getMessage());
	}
    }
}
