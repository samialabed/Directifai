import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class Directifai {

	private static ClarifaiClient clarifai = new ClarifaiClient(); // create a clarifai client, get keys from the environment variables

	public static void main(String[] args) throws IOException {


		if (args.length < 1) {
			System.err.println("Usage: java -jar Directifai-1-jar-with-dependencies.jar inputDirectory");
			return;
		}

		String targetFolder = args[0];
		File[] files = new File(targetFolder).listFiles();

		if (files == null)
			throw new FileNotFoundException("No files detected in the given path");

		for (File file : files) {
			if (ImageIO.read(file) == null)
				continue; // skip if current file isn't an image
			System.out.println("Processing: " + file.getName());
			sortIntoDirectoryByTag(file);
		}
		System.out.println("Finished processing images");
	}

	// move files based on their most probable tag given by Clarifai's API
	private static void sortIntoDirectoryByTag(File image) throws IOException {

		List<RecognitionResult> results =
			clarifai.recognize(new RecognitionRequest(image));
		// get the most probable tag
		Tag tag = results.get(0).getTags().get(0);

		String targetDir = "./output/" + tag.getName() + "/";

		// create directory for tag
		if (new File(targetDir).mkdirs())
			System.out.println("New directory created: " + tag.getName());

		// move file to newly created directory
		Path moveFrom = FileSystems.getDefault().getPath(image.getAbsolutePath());
		Path target = FileSystems.getDefault().getPath(targetDir + image.getName());
		Files.move(moveFrom, target, StandardCopyOption.REPLACE_EXISTING);
	}


}
