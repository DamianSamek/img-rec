package com.wsiz.imagerecognition;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

@RestController
public class MainController {

	private static final String IMG_EXTENSION = ".jpg";
	private static final String CONFIDENCE = "-conf-";
	private static byte[] GRAPH_DEFINITION;
	private static List<String> LABELS;
	private static final String INCEPTION_GRAPH = "tensorflow_inception_graph.pb";
	private static final String LABEL_STRINGS = "imagenet_comp_graph_label_strings.txt"; 
	{
		try {
			GRAPH_DEFINITION = readAllBytesOrExit(Paths.get(INCEPTION_GRAPH));
			LABELS = readAllLines(Paths.get(LABEL_STRINGS)); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView page() {
		return getHomeView();
	}

	@RequestMapping(value = "/predict", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView predict(@ModelAttribute FormDTO formDTO) throws IOException {
		Path baseDirPath = Paths.get("images");
		if (baseDirPath != null) {
			FileSystemUtils.deleteRecursively(baseDirPath);
		}

		Float threshold = formDTO.getThreshold();
		List<RecognitionObject> recognizedObjects = new ArrayList<>();
		List<RecognitionObject> unrecognizedObjects = new ArrayList<>();
		for (MultipartFile file : formDTO.getFiles()) {
			RecognitionObject recognizedObject = getRecognitionObject(file);
			if (recognizedObject.getConfidence() >= threshold) {
				recognizedObjects.add(recognizedObject);
			} else {
				unrecognizedObjects.add(recognizedObject);
			}
		}
		String directoryPath = Files.createDirectory(baseDirPath).toString();

		Map<String, List<RecognitionObject>> recognizedObjectsGrouped = recognizedObjects.stream()
				.collect(Collectors.groupingBy(RecognitionObject::getObject));

		createDirectoriesAndFiles(directoryPath, recognizedObjectsGrouped);
		if(!unrecognizedObjects.isEmpty()) {
			Path unrecognizedPath = Paths.get(directoryPath, "unrecognized");
			String unrecognizedPath_ = Files.createDirectory(unrecognizedPath).toString();
			createFiles(unrecognizedPath_, unrecognizedObjects);
		}

		Runtime.getRuntime().exec("explorer " + directoryPath);
		return getHomeView();
	}

	private void createDirectoriesAndFiles(String parentPath, Map<String, List<RecognitionObject>> objectsGrouped) throws IOException {
		for (Map.Entry<String, List<RecognitionObject>> entry : objectsGrouped.entrySet()) {
			String objectName = entry.getKey();
			Path path_ = Paths.get(parentPath, objectName);
			String dirPath = Files.createDirectory(path_).toString();
			createFiles(dirPath, entry.getValue());
		}
	}
	
	private void createFiles(String dirPath, List<RecognitionObject> objects) throws IOException {
		int counter = 0;
		for (RecognitionObject obj : objects) {
			String fileName = counter + CONFIDENCE + obj.getConfidence() + IMG_EXTENSION;
			Path filePath = Paths.get(dirPath, fileName);
			Files.write(filePath, obj.getFile().getBytes());
			counter++;
		}
	}

	private RecognitionObject getRecognitionObject(MultipartFile file) throws IOException {
		byte[] imageBytes = file.getBytes();
		Tensor image = Tensor.create(imageBytes);
		float[] labelProbabilities = executeInceptionGraph(GRAPH_DEFINITION, image);
		int bestLabelIdx = maxIndex(labelProbabilities);
		String name = LABELS.get(bestLabelIdx);
		float confidence =  labelProbabilities[bestLabelIdx] * 100f;
		RecognitionObject recognizedObject = new RecognitionObject(name, file, confidence);
		return recognizedObject;
	}

	private static float[] executeInceptionGraph(byte[] graphDef, Tensor image) {
		Graph g = new Graph();
		g.importGraphDef(graphDef);
		Session s = new Session(g);
		Tensor result = s.runner().feed("DecodeJpeg/contents", image).fetch("softmax").run().get(0);
		final long[] rshape = result.shape();
		int nlabels = (int) rshape[1];
		s.close();
		return result.copyTo(new float[1][nlabels])[0];
	}

	private static int maxIndex(float[] probabilities) {
		int best = 0;
		for (int i = 1; i < probabilities.length; ++i) {
			if (probabilities[i] > probabilities[best]) {
				best = i;
			}
		}
		return best;
	}

	private static byte[] readAllBytesOrExit(Path path) throws IOException {
		return Files.readAllBytes(path);
	}

	private static List<String> readAllLines(Path path) throws IOException {
		return Files.readAllLines(path, Charset.forName("UTF-8"));
	}
	
	private ModelAndView getHomeView() {
		return new ModelAndView("home");
	}
}
